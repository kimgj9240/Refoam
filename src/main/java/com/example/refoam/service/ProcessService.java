package com.example.refoam.service;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.dto.MoldTempForm;
import com.example.refoam.dto.ProcessProgressForm;
import com.example.refoam.dto.ProductChartDataForm;
import com.example.refoam.repository.AlertLogRepository;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ProcessRepository processRepository;
    private final AlertLogRepository alertLogRepository;
    private final OrderService orderService;
    private final StandardService standardService;
    private final StandardEvaluator standardEvaluator;
    private final SimpMessagingTemplate messagingTemplate;
    private final TaskScheduler taskScheduler;
    private final MonitoringService monitoringService;

    @Transactional
    public void startMainProcess(Long orderId) {
        Orders order = orderService.findOneOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        if (!order.getOrderState().equals("배합완료") && !order.getOrderState().equals("진행 중")) {
            throw new IllegalStateException("공정 가능한 상태가 아닙니다.");
        }

        if (order.getOrderState().equals("배합완료")) {
            order.setOrderState("진행 중");
            order.setCompletedCount(0);
            orderService.save(order);
        }
        messagingTemplate.convertAndSend(
                "/topic/process",
                new ProcessProgressForm(order.getId(), 0, order.getOrderQuantity(), 0.0, order.getOrderState())
        );
        long baseTime = System.currentTimeMillis();
        for (int i = 0; i < order.getOrderQuantity(); i++) {
            final int index = i;
            int delay = i * 5000; // 5초 간격 (20초)

            taskScheduler.schedule(() -> {
                Orders o = orderService.findOneOrder(orderId).orElseThrow();
                if (o.getCompletedCount() >= o.getOrderQuantity()) return;
                log.info("orderId={}, index={}, 시작됨", orderId, index);
                // 로트넘버 생성
                int sequenceNumber = index % 10 + 1;
                int lotNumberIndex = index / 10 + 1;
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMdd"); // 현재 날짜를 YYYYMMDD 형식으로 변환
                String currentDate = LocalDateTime.now().format(dateTimeFormatter);
                String lot = String.format("%02d",lotNumberIndex);
                String sequence = String.format("%02d",sequenceNumber);

                String lotNumber = o.getProductName().name() + "_" + o.getId() + "_"
                        + lot + "_" + sequence + "_" + currentDate;

                ProductStandardValue  productStandardValue = new ProductStandardValue();

                // 공정 1건 생성 => 규격 생성 코드가 너무 길어 메서드로 분리함, createStandard
                Standard standard = productStandardValue.createStandard();
                ProductLabel label = standardEvaluator.evaluate(standard.getInjPressurePeak(), standard.getMoldTemperature(), standard.getTimeToFill(),
                        standard.getCycleTime(), standard.getPlasticizingTime(),standard.getBackPressurePeak());
                standard.setProductLabel(label);
                standardService.save(standard);

                Process process = Process.builder()
                        .order(o)
                        .lotNumber(lotNumber)
                        .standard(standard)
                        .status((label == ProductLabel.OK) ? "OK" : label.name())
                        .processDate(LocalDateTime.now())
                        .build();
                processRepository.save(process);
                monitoringService.sendAchievementUpdate();
                standard.setProcess(process);
                standardService.save(standard);

                // 누적 완료 수 증가
                o.setCompletedCount(o.getCompletedCount() + 1);
                log.info("카운트 {}", o.getCompletedCount());
                long errorCount = processRepository.countByOrderAndStatusNot(o, "OK");
                double errorRate = Math.round((double) errorCount / o.getOrderQuantity() * 100.0) / 100.0;
                o.setOrderState("진행 중");
                // 공정 종료 조건
                if (o.getCompletedCount() >= o.getOrderQuantity()) {
                    o.setErrorRate(errorRate);
                    o.setOrderState("공정완료");
                    if (errorRate >= 0.3 && !alertLogRepository.existsByOrderAndCheckedFalse(o)) {
                        AlertLog alert = AlertLog.builder()
                                .order(o)
                                .employee(o.getEmployee())
                                .message("다량의 에러 발생 (에러율: " + String.format("%.1f%%", errorRate * 100) + ")")
                                .checked(false)
                                .createdDate(LocalDateTime.now())
                                .build();
                        alertLogRepository.save(alert);
                    }
                }

                orderService.save(o);
                log.info("완료 수: {}, 전체 수: {}", o.getCompletedCount(), o.getOrderQuantity());
                log.info("공정 예약됨: index = {}", index);
                log.info("전송 준비: orderId={}, completed={}", o.getId(), o.getCompletedCount());
                // WebSocket 전송
                int completedCount = o.getCompletedCount();
                int totalCount = o.getOrderQuantity();
                String status = o.getOrderState();
                log.info("웹소켓 전송: orderId={}, completed={}, total={}, errorRate={}",
                        o.getId(), completedCount, totalCount, errorRate);

                messagingTemplate.convertAndSend(
                        "/topic/process",
                        new ProcessProgressForm(o.getId(), completedCount, totalCount, errorRate, status)
                );

                messagingTemplate.convertAndSend(
                        "/topic/temperature/" + o.getProductName().name(),standard.getMoldTemperature()
                );
                socketHandler();
            }, new Date(baseTime + delay));
        }
    }

    public List<Process> findProcesses() {
        return processRepository.findAll();
    }

    public Optional<Process> findOneProcess(Long processId) {
        return processRepository.findById(processId);
    }

    public List<Process> findAllOrder(Long orderId) {
        return processRepository.findAllByOrder_Id(orderId);
    }

    public List<MoldTempForm> findRecentMoldTemperatures(String productName) {
        List<Process> processes = processRepository.findTop20ByOrder_ProductNameOrderByProcessDateDesc(ProductName.valueOf(productName));
        return processes.stream()
                .map(p -> new MoldTempForm(p.getProcessDate(), p.getStandard().getMoldTemperature()))
                .sorted(Comparator.comparing(MoldTempForm::getTime)) // 오래된 순으로 정렬
                .collect(Collectors.toList());
    }

    public void socketHandler(){
        List<ProductName> productNameList = List.of(
                ProductName.NORMAL,
                ProductName.BUMP,
                ProductName.HALF
        );
        for(ProductName products : productNameList) {
            List<Orders> allOrders = orderService.findTodayOrders();
            long okProductCount = 0;
            long errProductCount = 0;
            for (Orders monitoringOrder : allOrders) {

                okProductCount += processRepository.countTodayByOrderAndStatus(monitoringOrder, "OK", products);
                errProductCount += processRepository.countTodayByOrderAndStatusNot(monitoringOrder, "OK", products);

                messagingTemplate.convertAndSend(
                        "/topic/product-count",
                        new ProductChartDataForm(String.valueOf(products), okProductCount, errProductCount, LocalDateTime.now())
                );
            }
        }
    }

    public List<ProductChartDataForm> getStatsByProduct() {
        List<Orders> allOrders = orderService.findTodayOrders();

        Map<ProductName, List<Orders>> grouped = allOrders.stream()
                .collect(Collectors.groupingBy(Orders::getProductName));

        List<ProductChartDataForm> result = new ArrayList<>();
        for (Map.Entry<ProductName, List<Orders>> entry : grouped.entrySet()) {
            String productName = entry.getKey().name();
            List<Orders> orders = entry.getValue();

            long okCount = 0;
            long errorCount = 0;

            for (Orders order : orders) {
                okCount += processRepository.countTodayByOrderAndStatus(order, "OK", order.getProductName());
                errorCount += processRepository.countTodayByOrderAndStatusNot(order, "OK", order.getProductName());
            }

            result.add(new ProductChartDataForm(productName, okCount, errorCount, LocalDateTime.now()));
        }

        return result;
    }

    // 페이지네이션 구현용 메서드
    public Page<Process> getList(Long orderId, int page){
        PageRequest pageable = PageRequest.of(page, 12);
        return this.processRepository.findAllByOrder_Id(orderId,pageable);
    }
}
