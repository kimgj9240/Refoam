package com.example.refoam;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.repository.ProcessRepository;
import com.example.refoam.service.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.lang.Math.round;

@Component
@AllArgsConstructor
public class DummyDataLoader implements CommandLineRunner {
    private final EmployeeService employeeService;
    private final MaterialService materialService;
    private final OrderService orderService;
    private final StandardService standardService;
    private final ProcessRepository processRepository;


    @Override
    public void run(String... args) throws Exception {
        //run(String... args) <가변 인자 :기본적으로 여러개의 String 값을 받을 수 있는 배열같은 개념
        Employee employee = Employee.builder()
                .loginId("test")
                .username("관리자")
                .password("1111")
                .position(PositionName.ADMIN)
                .email("test@email.com")
                .build();
        employeeService.save(employee);

        Employee employee2 = Employee.builder()
                .loginId("test2")
                .username("직원")
                .password("1111")
                .position(PositionName.STAFF)
                .email("test2@email.com")
                .build();
        employeeService.save(employee2);


        List<MaterialName> materialNameList = List.of(
                MaterialName.EVA,
                MaterialName.P_WHITE,
                MaterialName.P_BLUE,
                MaterialName.P_BLACK,
                MaterialName.P_RED
        );

        List<Material> materials = materialNameList.stream().map(materialName -> Material.builder()
                .materialName(materialName)
                .materialQuantity(500)
                .materialDate(LocalDateTime.now())
                .employee(employee)
                .build()).toList();
        //toList() 변환된 Material객체들을 리스트로 모으기 위해 사용

        materials.forEach(materialService::save);
        List<ProductName> productNameList = List.of(
                ProductName.NORMAL,
                ProductName.BUMP,
                ProductName.HALF
        );


        // 7일치 더미 데이터 생
        // 랜덤 인스턴스 생성
        Random random = new Random();
        for (int d = 6; d > 0; d--) {
            LocalDateTime baseDate = LocalDate.now().minusDays(d).atTime(10, 0);
            List<Integer> qtyValues = List.of(10, 20, 30);

            for(int j=0;j<3;j++){
                int randomIndex = ThreadLocalRandom.current().nextInt(productNameList.size());
                int orderqty = qtyValues.get(new Random().nextInt(qtyValues.size()));//process를 orderQty만큼 생성하드록 수정
                Orders orders1 = Orders.builder()
                        .productName(productNameList.get(randomIndex))
                        .orderQuantity(orderqty)
                        .orderDate(baseDate)
                        .orderState("공정완료")
                        .employee(employee)
                        .build();
                orderService.save(orders1);

                ProductStandardValue productStandardValue = new ProductStandardValue();


                for (int i = 0; i < orderqty; i++) {
                    double melt = productStandardValue.getRandomValue(ProductStandardValue.MIN_MELT_TEMPERATURE, ProductStandardValue.MAX_MELT_TEMPERATURE);
                    double mold = productStandardValue.getRandomValue(ProductStandardValue.MIN_MOLD_TEMPERATURE, ProductStandardValue.MAX_MOLD_TEMPERATURE);
                    double screw = productStandardValue.getRandomValue(ProductStandardValue.MIN_SCREW_POS_END_HOLD, ProductStandardValue.MAX_SCREW_POS_END_HOLD);
                    double injpress = productStandardValue.getRandomValue(ProductStandardValue.MIN_INJ_PRESSURE_PEAK, ProductStandardValue.MAX_INJ_PRESSURE_PEAK);
                    double fill = productStandardValue.getRandomFill();
                    double plast = productStandardValue.getRandomValue(ProductStandardValue.MIN_PLASTICIZING_TIME, ProductStandardValue.MAX_PLASTICIZING_TIME);
                    double cycle = productStandardValue.getRandomValue(ProductStandardValue.MIN_CYCLE_TIME, ProductStandardValue.MAX_CYCLE_TIME);
                    double closeForce = productStandardValue.getRandomValue(ProductStandardValue.MIN_CLOSING_FORCE, ProductStandardValue.MAX_CLOSING_FORCE);
                    double clampPeak = productStandardValue.getRandomValue(ProductStandardValue.MIN_CLAMPING_FORCE_PEAK, ProductStandardValue.MAX_CLAMPING_FORCE_PEAK);
                    double trqPeak = productStandardValue.getRandomValue(ProductStandardValue.MIN_TORQUE_PEAK, ProductStandardValue.MAX_TORQUE_PEAK);
                    double trqMean = productStandardValue.getRandomValue(ProductStandardValue.MIN_TORQUE_MEAN, ProductStandardValue.MAX_TORQUE_MEAN);
                    double backPress = productStandardValue.getRandomValue(ProductStandardValue.MIN_BACK_PRESSURE_PEAK, ProductStandardValue.MAX_BACK_PRESSURE_PEAK);
                    double shot = productStandardValue.getRandomValue(ProductStandardValue.MIN_SHOT_VOLUME, ProductStandardValue.MAX_SHOT_VOLUME);
                    // ✅ 확률에 따라 상태 설정 (70% OK / 30% ERR_TEMP_01)
                    boolean isOk = random.nextDouble() < 0.7; // 0.0 ~ 0.999 중 70%는 true
                    String status = isOk ? "OK" : "ERR_TEMP_01";
                    ProductLabel label1 = isOk ? ProductLabel.OK : ProductLabel.ERR_TIME;
                    if (isOk){
                        orders1.setCompletedCount(orders1.getCompletedCount() + 1);
                        orderService.save(orders1);

                    }


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
                            .backPressurePeak(backPress)
                            .injPressurePeak(injpress)
                            .screwPosEndHold(screw)
                            .shotVolume(shot)
                            .productLabel(label1)
                            .build();
                    standardService.save(standard);

                    Process process = Process.builder()
                            .status(status)
                            .order(orders1)
                            .standard(standard)
                            .processDate(baseDate.plusMinutes(i))
                            .build();
                    processRepository.save(process);

                    standard.setProcess(process);
                    standardService.save(standard);
                }

            }
        }

        List<Orders> orders = productNameList.stream().map(productName -> {

            List<Integer> qtyValues = List.of(10, 20, 30);

            return Orders.builder()
                    .productName(ProductName.valueOf(productName.name()))
                    .orderQuantity(qtyValues.get(new Random().nextInt(qtyValues.size())))
                    .orderDate(LocalDateTime.now())
                    .orderState("준비 중")
                    .employee(employee)
                    .build();
        }).toList();
        orders.forEach(orderService::save);
    }
}


