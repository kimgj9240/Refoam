package com.example.refoam.repository;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.QualityCheck;
import com.example.refoam.domain.Standard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QualityCheckRepository extends JpaRepository<QualityCheck, Long>{
    Optional<QualityCheck> findByStandard(Standard standard);
}
