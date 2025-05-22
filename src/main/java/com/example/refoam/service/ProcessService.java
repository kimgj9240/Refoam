package com.example.refoam.service;

import com.example.refoam.domain.Orders;
import com.example.refoam.domain.Process;
import com.example.refoam.domain.ProductLabel;
import com.example.refoam.domain.Standard;
import com.example.refoam.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {
    private final ProcessRepository processRepository;
    private final OrderService orderService;
    private final StandardService standardService;
    private final StandardEvaluator standardEvaluator;

    public void startMainProcess(Long orderId) {
        Orders order = orderService.findOneOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));

        ProductStandardValue productStandardValue = new ProductStandardValue();

        // 조회한 주문 번호의 주문 수량
        for (int i = 0; i < order.getOrderQuantity(); i++) {
            // 랜덤값 생성
            double melt = productStandardValue.getRandomValue(ProductStandardValue.MIN_MELT_TEMPERATURE, ProductStandardValue.MAX_MELT_TEMPERATURE);
            double mold = productStandardValue.getRandomValue(ProductStandardValue.MIN_MOLD_TEMPERATURE, ProductStandardValue.MAX_MOLD_TEMPERATURE);
            double screw = productStandardValue.getRandomValue(ProductStandardValue.MIN_SCREW_POS_END_HOLD, ProductStandardValue.MAX_SCREW_POS_END_HOLD);
            double injpress = productStandardValue.getRandomValue(ProductStandardValue.MIN_INJ_PRESSURE_PEAK, ProductStandardValue.MAX_INJ_PRESSURE_PEAK);
            double fill = productStandardValue.getRandomValue(ProductStandardValue.MIN_TIME_TO_FILL, ProductStandardValue.MAX_TIME_TO_FILL);
            double plast = productStandardValue.getRandomValue(ProductStandardValue.MIN_PLASTICIZING_TIME, ProductStandardValue.MAX_PLASTICIZING_TIME);
            double cycle = productStandardValue.getRandomValue(ProductStandardValue.MIN_CYCLE_TIME, ProductStandardValue.MAX_CYCLE_TIME);
            double closeForce = productStandardValue.getRandomValue(ProductStandardValue.MIN_CLOSING_FORCE, ProductStandardValue.MAX_CLOSING_FORCE);
            double clampPeak = productStandardValue.getRandomValue(ProductStandardValue.MIN_CLAMPING_FORCE_PEAK, ProductStandardValue.MAX_CLAMPING_FORCE_PEAK);
            double trqPeak = productStandardValue.getRandomValue(ProductStandardValue.MIN_TORQUE_PEAK, ProductStandardValue.MAX_TORQUE_PEAK);
            double trqMean = productStandardValue.getRandomValue(ProductStandardValue.MIN_TORQUE_MEAN, ProductStandardValue.MAX_TORQUE_MEAN);
            double backpress = productStandardValue.getRandomValue(ProductStandardValue.MIN_BACK_PRESSURE_PEAK, ProductStandardValue.MAX_BACK_PRESSURE_PEAK);
            double shot = productStandardValue.getRandomValue(ProductStandardValue.MIN_SHOT_VOLUME, ProductStandardValue.MAX_SHOT_VOLUME);

            // 값 측정하여 라벨 평가
            ProductLabel label = standardEvaluator.evaluate(screw, injpress, mold, fill);

            // standard 생성
            Standard standard = Standard.builder()
                    .meltTemperature(melt)
                    .moldTemperature(mold)
                    .timeToFill(fill)
                    .plasticizingTime(plast)
                    .cycleTime(cycle)
                    .closingForce(closeForce)
                    .clampingForcePeak(clampPeak)
                    .torquePeak(trqPeak)
                    .torqueMean(trqMean)
                    .backPressurePeak(backpress)
                    .injPressurePeak(injpress)
                    .screwPosEndHold(screw)
                    .shotVolume(shot)
                    .productLabel(label)
                    .build();
            standardService.save(standard);

            // process 생성
            Process process = Process.builder()
                    .order(order) // process가 어느 주문에 속하는지
                    .standard(standard)
                    .status("공정완료")
                    .processDate(LocalDateTime.now())
                    .build();
            processRepository.save(process);

            // 양방향 연관
            standard.setProcess(process);
            // standard 다시 저장
            standardService.save(standard);
        }
        order.setOrderState("공정완료");
        orderService.save(order);
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
