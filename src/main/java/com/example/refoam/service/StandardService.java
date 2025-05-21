package com.example.refoam.service;

import com.example.refoam.domain.Standard;
import com.example.refoam.repository.StandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StandardService {
    private final StandardRepository standardRepository;

    public void save(Standard standard){
        standardRepository.save(standard);
    }

    //주문 번호로 공정 결과 조회
    public Optional<Standard> findOne(Long id){
        return standardRepository.findById(id);
    }
}
