package com.example.refoam.repository;

import com.example.refoam.domain.PredictionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRecordRepository extends JpaRepository<PredictionRecord, Long> {
}
