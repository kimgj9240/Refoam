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
            //features 순서 : melt_temperature	mold_temperature	time_to_fill	plasticizing_time	cycle_time	closing_force
            //clamping_force_peak	torque_peak	torque_mean	back_pressure_peak	inj_pressure_peak	screw_pos_end_hold	shot_volume
            double[] features = {std.getMeltTemperature(), std.getMoldTemperature(), std.getTimeToFill(), std.getPlasticizingTime(), std.getCycleTime(), std.getClosingForce(),
                    std.getClampingForcePeak(), std.getTorquePeak(), std.getTorqueMean(), std.getBackPressurePeak(), std.getInjPressurePeak(), std.getScrewPosEndHold(), std.getShotVolume()};

            QualityRequest request = new QualityRequest();
            request.setFeatures(features);
            // 1. Flask 예측 요청

            //1-1  RestTemplate 생성 - 스프링에서 HTTP 요청을 쉽게 보낼 수 있는 클래스 (클라이언트 역할)
            RestTemplate restTemplate = new RestTemplate();

            //1-2. 요청 헤더 설정 - Content-Type을 JSON으로 설정 → Flask는 JSON 형식의 데이터를 받는 것으로 기대하고 있음
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            //1-3. 요청 엔티티 생성 - 실제 보낼 요청 본문과 헤더를 함께 담은 HttpEntity 생성 → 이걸 통해 Flask로 전송
            HttpEntity<QualityRequest> entity = new HttpEntity<>(request, headers);

            //1-4. Flask API 호출
            ResponseEntity<QualityResponse> response = restTemplate.exchange(  //exchange() 메서드로 HTTP POST 요청을 보냄
                    FLASK_URL,
                    HttpMethod.POST,
                    entity,
                    QualityResponse.class   //응답을 PredictResponse.class 형태로 자동 변환, response.getBody()로 Flask가 준 예측 결과 꺼냄,
            );

            String qualityCheckLabel = response.getBody().getQualityCheckLabel();   //Flask 응답(JSON)을 자바 객체(PredictResponse)로 바꾼 후 그 안에 있는 prediction 값을 꺼냄
            //flask 확인 => return jsonify({"qualityCheckLabel": float(prediction[0])})

            //2. 원본 Json저장 - 예측 요청(request)을 JSON 문자열로 직렬화(serialize) 해서 저장
            //2-1 ObjectMapper 생성 - 자바 객체 ↔ JSON 문자열 간 변환 담당
            ObjectMapper mapper = new ObjectMapper();
            String inputJson = null;
            try {
                inputJson = mapper.writeValueAsString(request);  //request 객체(PredictRequest)를 JSON 문자열로 바꿈
            } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
                log.error("예측 처리 중 에러 발생", e); // 실무에서는 log 사용 권장
            }

            // 3. 예측 요청 정보와 예측 결과를 DB에 저장
            Optional<QualityCheck> qualityCheck = findOneByStandard(std);
            QualityCheck record;
            if (qualityCheck.isEmpty()) {
                record = new QualityCheck(); // 새로 생성
            } else {
                record = qualityCheck.get();     // Optional에서 값 추출
            }
            record.setInputDate(inputJson);  //inputJson (예측 입력값의 JSON 문자열)을 저장
            record.setCheckResult(qualityCheckLabel);  //Flask로부터 받은 예측 결과 값을 저장
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
