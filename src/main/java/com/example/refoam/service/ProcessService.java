package com.example.refoam.service;

import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.domain.ProductName;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ProcessRepository processRepository;

    public void save(Process process){
        processRepository.save(process);
    }

    public List<Process> findProcesses(){

        return processRepository.findAll();
    }

    public Optional<Process> findOneProcess(Long processId){
        return processRepository.findById(processId);
    }

    public List<Process> findAllOrder(Long orderId){
        // 규격내 랜덤값 생성
//        ProductStandardValue productStandardValue = new ProductStandardValue();
//        log.info("랜덤값 : {}", productStandardValue.getRandomValue(ProductStandardConst.MIN_MELT_TEMPERATURE, ProductStandardConst.MAX_MELT_TEMPERATURE));
        return processRepository.findAllByOrder_Id(orderId);
    }
}
