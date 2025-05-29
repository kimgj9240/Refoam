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
    private final ProcessService processService;
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

//        for (int i = 1; i <= 50; i++) {
//            Employee employee3 = Employee.builder()
//                    .loginId("test" + i)
//                    .username("관리자")
//                    .password("1111")
//                    .position(PositionName.ADMIN)
//                    .email("test@email.com")
//                    .build();
//            employeeService.save(employee3);
//        }
//stream()을 호출하면 데이터를 함수형 방식으로 처리할 수 있도록 도와줌
        //map()은 스트림의 각 요소를 다른 값으로 변환하는 역할
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
//        List<Orders> orders = productNameList.stream().map(
//                productName -> Orders.builder()
//                        .productName(ProductName.valueOf(productName.name()))
//                        .orderQuantity(10)
//                        .orderDate(LocalDateTime.now())
//                        .orderState("준비 중")
//                        .employee(employee)
//                        .build()).toList();


        Random orderRandom = new Random();
        int minQuantity = 5;
        int maxQuantity = 20;
        AtomicInteger index = new AtomicInteger(0);
        List<Orders> orders = productNameList.stream().map(productName -> {
            int daysAgo = index.getAndIncrement() % 9; // 0,1,2,3,4 반복
            LocalDateTime orderDate = LocalDateTime.now().minusDays(9 - daysAgo);

            int orderQuantity = orderRandom.nextInt(maxQuantity - minQuantity + 1) + minQuantity; // min~max 랜덤

            return Orders.builder()
                    .productName(ProductName.valueOf(productName.name()))
                    .orderQuantity(orderQuantity)
                    .orderDate(orderDate)
                    .orderState("준비 중")
                    .employee(employee)
                    .build();
        }).toList();
        orders.forEach(orderService::save);

//        List<Orders> orders = productNameList.stream().map(
//                productName -> Orders.builder()
//                        .productName(ProductName.valueOf(productName.name()))
//                        .orderQuantity(5)
//                        .orderDate(LocalDateTime.now())
//                        .orderState("준비 중")
//                        .employee(employee)
//                        .build()).toList();
//        orders.forEach(orderService::save);

        double[] backPressurePeak = {1145.6,145.6,147,145.6,146.6,146.3,146.6,145.4,146.8,146,146.4,146.6,146.3,146.3,144.9,145.4};
        double[] closingForce ={886.9,919.409791006952,908.6,879.410870514183,917.1,881.9,887,882.8,895.2,890.4,893.5,896.6,897.4,895.2,891.7,900.5};
        double[] clampingForcePeak ={904,935.9,902.344823440673,902.033653277801,935.9,897,903.5,900.1,915.3,912.1,917.3,913.6,916,917.3,912.7,918.4};
        double[] cycleTime ={74.83,74.81,74.81,74.82,74.8,74.8,74.83,74.83,74.89,74.86,75.74,75.77,75.71,75.74,75.71,75.64};
        double[] meltTemperature ={106.4761843,105.505,105.505,106.47482732,
                105.515,106.465,106.242,106.46,
                105.182,104.7,105.443,105.467,
                105.835,105.784,105.905,123.432
        };
        double[] moldTemperature ={80.617,81.362,80.411,81.162,
                80.659,81.38,81.349,81.547,
                79.975,78.872,80.666,80.689,
                81.037,81.027,81.208,81.973};
        double[] plasticizingTime ={3.16,3.16,4.08,3.16,4.01,3.19,3.18,3.19,6.61,3.4,5.94,5.25,3.43,3.42,3.32,2.98};
        double[] injPressurePeak ={922.3,930.5,933.1,922.3,903.6,926.3,925.1,926.6,897.9,813.4,829.6,852.9,868.9,864.7,895.7,901.6};
        double[] screwPosEndHold ={8.82,8.59,8.8,8.85,8.81,8.82,8.88,8.83,8.89,8.45,8.59,8.69,8.68,8.72,8.9,8.94};
        double[] shotVolume ={18.73,18.73,18.98,18.73,18.76,18.75,18.69,18.74,18.67,19.12,18.98,18.87,18.88,18.84,18.67,18.62
        };
        double[] timeToFill ={7.124,6.968,6.864,6.864,
                6.968,6.968,6.968,6.968,
                6.292,6.292,6.188,6.292,
                6.292,6.292,6.292,11.128};
        double[] torqueMean ={104.3,104.9,106.503495621329,104.9,93,104.7,103.3,103.6,76.5,105.8,81.1,82.6,104.7,105.4,101.7,102.5};
        double[] torquePeak ={116.9,113.9,120.5,127.3,111.5,119.3,115.4,115.1,94.5,121.1,97.4,101.9,115.4,123.5,115.7,111.5};

        String[] label = {"ERR_TIME_01","ERR_TIME_01","ERR_TIME_01","ERR_TIME_01","OK","OK","OK","OK","OK","OK","OK","OK","ERR_TIME_01","ERR_TIME_01","ERR_TIME_01","ERR_TIME_01"};




        // 7일치 더미 데이터 생
        // 랜덤 인스턴스 생성
        Random random = new Random();


        for (int d = 6; d >= 0; d--) {
            LocalDateTime baseDate = LocalDate.now().minusDays(d).atTime(10, 0);

            Orders orders1 = Orders.builder()
                    .productName(ProductName.NORMAL)
                    .orderQuantity(10)
                    .orderDate(baseDate)
                    .orderState("공정완료")
                    .employee(employee)
                    .build();
            orderService.save(orders1);

            ProductStandardValue productStandardValue = new ProductStandardValue();


            for (int i = 0; i < 10; i++) {
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
}


