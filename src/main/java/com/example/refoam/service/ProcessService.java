package com.example.refoam.service;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.dto.ProcessProgressForm;
import com.example.refoam.repository.AlertLogRepository;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void startMainProcess(Long orderId) {
        Orders order = orderService.findOneOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        if (!order.getOrderState().equals("배합완료") && !order.getOrderState().equals("진행 중")) {
            throw new IllegalStateException("공정 가능한 상태가 아닙니다.");
        }

        if (order.getOrderState().equals("배합완료")) {
            order.setOrderState("진행 중");
            orderService.save(order);
        }
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
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd"); // 현재 날짜를 YYYYMMDD 형식으로 변환
                String currentDate = LocalDateTime.now().format(dateTimeFormatter);
                String lot = String.format("%02d",lotNumberIndex);
                String sequence = String.format("%03d",sequenceNumber);

                String lotNumber = o.getProductName().name() + "_" + o.getId() + "_"
                        + lot + "_" + sequence + "_" + currentDate;

                ProductStandardValue  productStandardValue = new ProductStandardValue();

                // 공정 1건 생성
                double melt = productStandardValue.getRandomValue(ProductStandardValue.MIN_MELT_TEMPERATURE, ProductStandardValue.MAX_MELT_TEMPERATURE);
                double mold = productStandardValue.getRandomValue(ProductStandardValue.MIN_MOLD_TEMPERATURE, ProductStandardValue.MAX_MOLD_TEMPERATURE);
                double screw = productStandardValue.getRandomValue(ProductStandardValue.MIN_SCREW_POS_END_HOLD, ProductStandardValue.MAX_SCREW_POS_END_HOLD);
                double injpress = productStandardValue.getRandomValue(ProductStandardValue.MIN_INJ_PRESSURE_PEAK, ProductStandardValue.MAX_INJ_PRESSURE_PEAK);
                double fill = productStandardValue.getRandomFill();
                double plast = productStandardValue.getRandomValue(ProductStandardValue.MIN_PLASTICIZING_TIME, ProductStandardValue.MAX_PLASTICIZING_TIME);
                double cycle = productStandardValue.getRandomValue(ProductStandardValue.MIN_CYCLE_TIME, ProductStandardValue.MAX_CYCLE_TIME);
                double closeForce = productStandardValue.getRandomValue(ProductStandardValue.MIN_CLOSING_FORCE, ProductStandardValue.MAX_CLOSING_FORCE);
                double clampPeak = productStandardValue.getRandomValue(ProductStandardValue.MIN_CLAMPING_FORCE_PEAK, ProductStandardValue.MAX_CLAMPING_FORCE_PEAK);
                double trqPeak = productStandardValue.getRandomValue(ProductStandardValue.MIN_TORQUE_PEAK, ProductStandardValue.MAX_TORQUE_PEAK);
                double trqMean = productStandardValue.getRandomValue(ProductStandardValue.MIN_TORQUE_MEAN, ProductStandardValue.MAX_TORQUE_MEAN);
                double backPress = productStandardValue.getRandomValue(ProductStandardValue.MIN_BACK_PRESSURE_PEAK, ProductStandardValue.MAX_BACK_PRESSURE_PEAK);
                double shot = productStandardValue.getRandomValue(ProductStandardValue.MIN_SHOT_VOLUME, ProductStandardValue.MAX_SHOT_VOLUME);

                ProductLabel label = standardEvaluator.evaluate(injpress, mold, fill, cycle, plast);

                Standard standard = Standard.builder()
                        .meltTemperature(melt)
                        .moldTemperature(mold)
                        .timeToFill(fill)
                        .plasticizingTime(plast)
                        .cycleTime(cycle)
                        .closingForce(closeForce)
                        .clampingForcePeak(clampPeak)
                        .torquePeak(trqPeak)
                        .torqueMean(trqMean)
                        .backPressurePeak(backPress)
                        .injPressurePeak(injpress)
                        .screwPosEndHold(screw)
                        .shotVolume(shot)
                        .productLabel(label)
                        .build();
                standardService.save(standard);

                Process process = Process.builder()
                        .order(o)
                        .lotNumber(lotNumber)
                        .standard(standard)
                        .status((label == ProductLabel.OK) ? "OK" : label.name())
                        .processDate(LocalDateTime.now())
                        .build();
                processRepository.save(process);
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
            }, new Date(baseTime + delay));
        }
    }

    public List<Process> findProcesses(){
        return processRepository.findAll();
    }

    public Optional<Process> findOneProcess(Long processId){
        return processRepository.findById(processId);
    }

    public List<Process> findAllOrder(Long orderId){
        return processRepository.findAllByOrder_Id(orderId);
    }
    // 페이지네이션 구현용 메서드
    public Page<Process> getList(Long orderId, int page){
        PageRequest pageable = PageRequest.of(page, 12);
        return this.processRepository.findAllByOrder_Id(orderId,pageable);
    }

}
