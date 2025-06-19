package com.example.refoam;

import com.example.refoam.service.DiscordNotifier;
import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.repository.ErrorStatisticsRepository;
import com.example.refoam.repository.OrderRepository;
import com.example.refoam.repository.ProcessRepository;
import com.example.refoam.service.OrderMonitorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsScheduler {
    private final OrderRepository orderRepository;
    private final ErrorStatisticsRepository errorStatisticsRepository;
    private final ProcessRepository processRepository;
    private final OrderMonitorService orderMonitorService;
    private final DiscordNotifier discordNotifier;

    @Scheduled(fixedRate = 300000)//interval 5 minutes
    public void statistics(){
        log.info("statistics 스케줄러 호출됨 : {}", LocalDateTime.now());
        List<Orders> ordersList = orderRepository.findAllByOrderStateAndStatisticsIntervalCheck("공정완료",false);
        for(Orders orders : ordersList){
            LocalDateTime interval = LocalDateTime.now().minusMinutes(5);//interval 5 minutes
            List<Process> processList = processRepository.findByOrderAndProcessDateInterval(orders, interval);
            if(processList.isEmpty()) continue;

            int errorCount = 0;
            for(Process process : processList){
                if(process.getStandard().getProductLabel()!=ProductLabel.OK){
                    errorCount +=1;
                }
            }
            if(errorCount >0){
                ErrorStatistics errorStatistics = ErrorStatistics.builder()
                        .order(orders)
                        .errorDate(LocalDateTime.now())
                        .errorCount(errorCount)
                        .build();
                errorStatisticsRepository.save(errorStatistics);
            }
            orders.setStatisticsIntervalCheck(true);
            orderRepository.save(orders);
        }

    }
    @Scheduled(initialDelay = 0, fixedRate = 30000)
    public void errCountMonitor() {
        log.info("⏰ errCountMonitor 스케줄러 호출됨 : {}", LocalDateTime.now());
        List<Orders> ordersList = orderRepository.findAllByOrderStateAndStatisticsIntervalCheckAndSmtpCheck("공정완료", true, false);

        for (Orders orders : ordersList) {
            int orderQty = orders.getOrderQuantity();
            String productName = String.valueOf(orders.getProductName());
            String email = orders.getEmployee().getEmail();

            Integer errCount = errorStatisticsRepository.findMaxErrorCountGroupedByOrderId(orders);
            if (errCount == null || errCount == 0) continue;

            double errorRate = (double) errCount / orderQty;

            // 에러율이 30% 이하인 경우 스킵
            if (errorRate <= 0.3) {
                log.info("⚠️ 주문 {} errorRate {} <= 0.3, 전송 제외", orders.getId(), errorRate);
                continue;
            }

            // 디스코드 알림 전송 조건: 아직 알림을 보낸 적 없는 주문
            if (!orders.isDiscordCheck()) {
                String message = String.format(
                        "🚨 [주문 %d] %s 제품 공정 중 에러율 %.2f%% (에러 %d건 / 총 %d건)",
                        orders.getId(), productName, errorRate * 100, errCount, orderQty
                );
                log.info("📨 디스코드 알림 전송 시도: 주문 {}", orders.getId());
                discordNotifier.sendAlert(message);
                // 재전송 방지를 위한 플래그 저장
                orders.setDiscordCheck(true);
            }

            orderRepository.save(orders);

            // 이메일 전송 조건: 메일 체크 '사용'로 되어 있을 경우
            if (orders.getEmployee().isSendMail()) {
                orderMonitorService.errorCheck(email, orderQty, errCount);
                log.info("📧 email 전송: {}, 주문 {}, 에러 {}", email, orderQty, errCount);
                orders.setSmtpCheck(true);
            }

            orderRepository.save(orders);
        }
    }
}
