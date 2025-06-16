package com.example.refoam.service;

import com.example.refoam.domain.PredictionOrderInput;
import com.example.refoam.domain.PredictionRecord;
import com.example.refoam.dto.PredictRequest;
import com.example.refoam.dto.PredictResponse;
import com.example.refoam.dto.PredictResult;
import com.example.refoam.repository.PredictionRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class PredictService {
    private final PredictionRecordRepository predictionRecordRepository;
    private final String FLASK_URL = "http://localhost:8000/predict";

    public PredictResult getPrediction(PredictRequest request) {

        // 1. Flask 예측 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PredictRequest> entity = new HttpEntity<>(request, headers);

        //Flask API 호출
        ResponseEntity<PredictResponse> response = restTemplate.exchange(
                FLASK_URL,
                HttpMethod.POST,
                entity,
                PredictResponse.class
        );
        //Flask 응답(JSON)을 자바 객체(PredictResponse)로 바꾼 후 그 안에 있는 prediction 값을 꺼냄
        double prediction = response.getBody().getPrediction();

        //2. 원본 Json저장
        ObjectMapper mapper = new ObjectMapper();
        String inputJson = null;
        try {
            inputJson = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("예측 처리 중 에러 발생", e);
        }

        // 3. 예측 요청 정보와 예측 결과를 DB에 저장
        PredictionRecord record = new PredictionRecord();
        record.setInputData(inputJson);
        record.setPrediction(prediction);
        record.setPredictedAt(LocalDateTime.now());

        // 4. 입력값 1건씩 PredictionOrderInput으로 변환
        List<PredictionOrderInput> orderInputList = request.getOrders().stream().map(order -> {
            PredictionOrderInput input = new PredictionOrderInput();
            input.setDate(order.getDate());
            input.setQty(order.getQty());
            input.setMaterialName(order.getMaterialName2());
            input.setRecord(record);
            return input;
        }).toList();

        //양방형 설정
        record.setOrderInputs(orderInputList);

        //5.저장
        predictionRecordRepository.save(record);

        //예측 날짜 계산
        LocalDate maxDate = request.getOrders().stream()
                .map(order -> LocalDate.parse(order.getDate()))  //날짜 String -> LocalDate
                .max(Comparator.naturalOrder()) // 가장 큰 날짜 (가장 미래), Comparator.naturalOrder => 오름차순 비교 (기본 정렬 방식)
                .orElse(LocalDate.now());  // 만약 없으면 오늘 날짜 사용

        LocalDate predictedDate = maxDate.plusDays(3);

        return new PredictResult(prediction, predictedDate);
    }
}
