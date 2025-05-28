package com.example.refoam;

import com.example.refoam.domain.ErrorStatistics;
import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.domain.ProductLabel;
import com.example.refoam.repository.ErrorStatisticsRepository;
import com.example.refoam.repository.OrderRepository;
import com.example.refoam.repository.ProcessRepository;
import com.example.refoam.service.EmailService;
import com.example.refoam.service.OrderMonitorService;
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

    @Scheduled(fixedRate = 60000)//interval 1 minutes
    public void statistics(){
        log.info("statistics 스케줄러 호출됨 : {}", LocalDateTime.now());
        List<Orders> ordersList = orderRepository.findAllByOrderStateAndStatisticsIntervalCheck("공정완료",false);
        for(Orders orders : ordersList){
            LocalDateTime interval = LocalDateTime.now().minusMinutes(1);//interval 1 minutes
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
    @Scheduled(fixedRate = 60000)//interval 1 minutes
    public void errCountMonitor(){
        log.info("errCountMonitor 스케줄러 호출됨 : {}", LocalDateTime.now());
        List<Orders> ordersList = orderRepository.findAllByOrderStateAndStatisticsIntervalCheckAndSmtpCheck("공정완료",true,false);
        for(Orders orders : ordersList){
            int orderQty = orders.getOrderQuantity();
            if(!orders.getEmployee().isSendMail()) continue;

            String email = orders.getEmployee().getEmail();
            int errCount =errorStatisticsRepository.findMaxErrorCountGroupedByOrderId(orders);
            if (errCount == 0) continue;
            orderMonitorService.errorCheck("refoam.test@gmail.com",orderQty,errCount);
            log.info("email send : {}",email + orderQty + errCount);
            //orderMonitorService.errorCheck(email,orderQty,errCount);
            orders.setSmtpCheck(true);
            orderRepository.save(orders);
        }
    }
}
