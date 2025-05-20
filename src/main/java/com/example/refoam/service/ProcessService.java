package com.example.refoam.service;

import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.domain.ProductName;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ProcessRepository processRepository;

    public List<Process> findProcesses(){
        return processRepository.findAll();
    }

    public Optional<Process> findOneProcess(Long processId){
        return processRepository.findById(processId);
    }

    public List<Process> findAllOrder(Long orderId){
        return processRepository.findAllByOrder_Id(orderId);
    }
}
