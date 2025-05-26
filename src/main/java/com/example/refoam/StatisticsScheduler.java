package com.example.refoam;

import com.example.refoam.domain.ErrorStatistics;
import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.domain.ProductLabel;
import com.example.refoam.repository.ErrorStatisticsRepository;
import com.example.refoam.repository.OrderRepository;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsScheduler {
    private final OrderRepository orderRepository;
    private final ErrorStatisticsRepository errorStatisticsRepository;
    private final ProcessRepository processRepository;

    @Scheduled(fixedRate = 60000)//interval 1 minutes
    public void statistics(){
        log.info("스케줄러 호출됨 : {}", LocalDateTime.now());
        List<Orders> ordersList = orderRepository.findAllByOrderStateAndStatisticsIntervalCheck("공정완료",false);
        for(Orders orders : ordersList){
            LocalDateTime interval = LocalDateTime.now().minusMinutes(1);//interval 1 minutes
            List<Process> processList = processRepository.findByOrderAndProcessDateInterval(orders, interval);
            if(processList.isEmpty()) return;

            int errorCount = 0;
            for(Process process : processList){
                if(process.getStandard().getProductLabel()!=ProductLabel.OK){
                    errorCount +=1;
                    ErrorStatistics errorStatistics = ErrorStatistics.builder()
                            .order(orders)
                            .errorDate(LocalDateTime.now())
                            .errorCount(errorCount)
                            .build();
                    errorStatisticsRepository.save(errorStatistics);
                }
            }
        }

    }
}
