package com.example.refoam.repository;

import com.example.refoam.domain.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessRepository extends JpaRepository<Process, Long> {
    List<Process> findAllByOrder_Id(Long orderId);

    Page<Process> findAllByOrder_Id(Long orderId, Pageable pageable);

}
