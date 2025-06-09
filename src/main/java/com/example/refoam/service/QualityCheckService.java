package com.example.refoam.service;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.dto.*;
import com.example.refoam.repository.QualityCheckRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PrimitiveIterator;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QualityCheckService {
    private final QualityCheckRepository qualityCheckRepository;
    private final StandardService standardService;
    private final ProcessService processService;
    private final String FLASK_URL = "http://localhost:5000/quality";
    @Transactional
    public void getQualityCheck(Long orderId) {
        Process findprocess = processService.findOneProcess(orderId).orElseThrow(()-> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        List<Process> processes = processService.findAllOrder(findprocess.getId());
        for(Process process : processes) {
            Standard std = standardService.findOne(process.getId()).orElseThrow(() -> new IllegalArgumentException("해당 공정을 찾을 수 없습니다."));
            //features 생성
            double[] features = {std.getMeltTemperature(), std.getMoldTemperature(), std.getTimeToFill(), std.getPlasticizingTime(), std.getCycleTime(), std.getClosingForce(),
                    std.getClampingForcePeak(), std.getTorquePeak(), std.getTorqueMean(), std.getBackPressurePeak(), std.getInjPressurePeak(), std.getScrewPosEndHold(), std.getShotVolume()};

            QualityRequest request = new QualityRequest();
            request.setFeatures(features);

            // 1. Flask 예측 요청
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<QualityRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<QualityResponse> response = restTemplate.exchange(  //exchange() 메서드로 HTTP POST 요청을 보냄
                    FLASK_URL,
                    HttpMethod.POST,
                    entity,
                    QualityResponse.class
            );
            String qualityCheckLabel = response.getBody().getQualityCheckLabel();

            //2. 원본 Json저장
            ObjectMapper mapper = new ObjectMapper();
            String inputJson = null;
            try {
                inputJson = mapper.writeValueAsString(request);
            } catch (JsonProcessingException e) {
                log.error("예측 처리 중 에러 발생", e);
            }
            // 3. 예측 요청 정보와 예측 결과를 DB에 저장
            Optional<QualityCheck> qualityCheck = findOneByStandard(std);
            QualityCheck record;
            if (qualityCheck.isEmpty()) {
                record = new QualityCheck();
            } else {
                record = qualityCheck.get();
            }
            record.setInputDate(inputJson);
            record.setCheckResult(qualityCheckLabel);
            record.setStandard(std);   //검수 요청하는 제품
            //4.저장
            qualityCheckRepository.save(record);
        }
    }
    public QualityCheck selectQualityCheck(Long orderId){
        Process selectprocess = processService.findOneProcess(orderId).orElseThrow(()-> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        return selectprocess.getStandard().getQualityCheck();
    }
    public Optional<QualityCheck> findOneByStandard(Standard standard){
        return qualityCheckRepository.findByStandard(standard);
    }
}
