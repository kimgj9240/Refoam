package com.example.refoam.service;

import com.example.refoam.domain.Standard;
import com.example.refoam.repository.StandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StandardService {
    private final StandardRepository standardRepository;

    public void save(Standard standard){
        standardRepository.save(standard);
    }
}
