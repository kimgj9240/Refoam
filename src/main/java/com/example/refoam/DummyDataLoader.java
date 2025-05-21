package com.example.refoam;

import com.example.refoam.domain.*;
import com.example.refoam.domain.Process;
import com.example.refoam.service.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class DummyDataLoader implements CommandLineRunner {
    private final EmployeeService employeeService;
    private final MaterialService materialService;
    private final OrderService orderService;
    private final StandardService standardService;
    private final ProcessService processService;


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
                MaterialName.TITANIUM_DIOXIDE,
                MaterialName.ULTRAMARINE_BLUE,
                MaterialName.CARBON_BLACK,
                MaterialName.IRON_OXIDE_RED
        );

        for (int i = 1; i <= 50; i++) {
            Employee employee3 = Employee.builder()
                    .loginId("test" + i)
                    .username("관리자")
                    .password("1111")
                    .position(PositionName.ADMIN)
                    .email("test@email.com")
                    .build();
            employeeService.save(employee3);
        }
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
                ProductName.NORMAL30,
                ProductName.NORMAL60,
                ProductName.NORMAL90,
                ProductName.BUMP30,
                ProductName.BUMP60,
                ProductName.BUMP90,
                ProductName.HALF30,
                ProductName.HALF60,
                ProductName.HALF90
        );
        List<Orders> orders = productNameList.stream().map(
                productName -> Orders.builder()
                        .productName(ProductName.valueOf(productName.name()))
                        .orderQuantity(10)
                        .orderDate(LocalDateTime.now())
                        .orderState("준비 중")
                        .employee(employee)
                        .build()).toList();
        orders.forEach(orderService::save);

        Orders order = orderService.findOneOrder(1L).orElseThrow();

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


        for (int i = 0; i < 16; i++) {
            Process process = Process.builder()
                    .status("PENDING")
                    .order(order)
                    .processDate(LocalDateTime.now())
                    .build();
            processService.save(process);

            Standard standard = Standard.builder()
                    .backPressurePeak(backPressurePeak[i])
                    .closingForce(closingForce[i])
                    .clampingForcePeak(clampingForcePeak[i])
                    .cycleTime(cycleTime[i])
                    .meltTemperature(meltTemperature[i])
                    .moldTemperature(moldTemperature[i])
                    .plasticizingTime(plasticizingTime[i])
                    .injPressurePeak(injPressurePeak[i])
                    .screwPosEndHold(screwPosEndHold[i])
                    .shotVolume(shotVolume[i])
                    .timeToFill(timeToFill[i])
                    .torqueMean(torqueMean[i])
                    .torquePeak(torquePeak[i])
                    .process(process)
                    .build();
            standardService.save(standard);
            Process findprocess = processService.findOneProcess(process.getId()).orElseThrow();
            findprocess.setStandard(standard);
            processService.save(findprocess);
        }
    }
}
