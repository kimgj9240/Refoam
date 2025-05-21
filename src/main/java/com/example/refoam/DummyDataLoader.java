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

        for (int i=1; i<=50; i++){
            Employee employee3 = Employee.builder()
                    .loginId("test"+i)
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

        Standard standard1 = Standard.builder()
                .backPressurePeak(146.3)
                .closingForce(895.2)
                .clampingForcePeak(917.3)
                .cycleTime(75.74)
                .meltTemperature(105.784)
                .moldTemperature(81.027)
                .plasticizingTime(3.42)
                .injPressurePeak(864.7)
                .screwPosEndHold(8.72)
                .shotVolume(18.84)
                .timeToFill(6.292)
                .torqueMean(105.4)
                .torquePeak(123.5)
                .build();
        standardService.save(standard1);
        Process process = Process.builder()
                .status("PENDING")
                .order(order)
                .processDate(LocalDateTime.now())
                .standard(standard1)
                .build();
        processService.save(process);

        Standard standard2 = Standard.builder()
                .backPressurePeak(146.3)
                .closingForce(897.4)
                .clampingForcePeak(916)
                .cycleTime(75.71)
                .meltTemperature(105.835)
                .moldTemperature(81.037)
                .plasticizingTime(3.43)
                .injPressurePeak(868.9)
                .screwPosEndHold(8.68)
                .shotVolume(18.88)
                .timeToFill(6.292)
                .torqueMean(104.7)
                .torquePeak(115.4)
                .build();
        standardService.save(standard2);
        Process process2 = Process.builder()
                .status("PENDING")
                .order(order)
                .processDate(LocalDateTime.now())
                .standard(standard2)
                .build();
        processService.save(process2);

        Standard standard3 = Standard.builder()
                .backPressurePeak(146.8)
                .closingForce(895.2)
                .clampingForcePeak(915.3)
                .cycleTime(74.89)
                .meltTemperature(105.182)
                .moldTemperature(79.975)
                .plasticizingTime(6.61)
                .injPressurePeak(897.9)
                .screwPosEndHold(8.89)
                .shotVolume(18.67)
                .timeToFill(6.292)
                .torqueMean(76.5)
                .torquePeak(94.5)
                .build();
        standardService.save(standard3);
        Process process3 = Process.builder()
                .status("PENDING")
                .order(order)
                .processDate(LocalDateTime.now())
                .standard(standard3)
                .build();
        processService.save(process3);

        Standard standard4 = Standard.builder()
                .backPressurePeak(146)
                .closingForce(890.4)
                .clampingForcePeak(912.1)
                .cycleTime(74.86)
                .meltTemperature(104.7)
                .moldTemperature(78.872)
                .plasticizingTime(3.4)
                .injPressurePeak(813.4)
                .screwPosEndHold(8.45)
                .shotVolume(19.12)
                .timeToFill(6.292)
                .torqueMean(105.8)
                .torquePeak(121.1)
                .build();
        standardService.save(standard4);
        Process process4 = Process.builder()
                .status("PENDING")
                .order(order)
                .processDate(LocalDateTime.now())
                .standard(standard4)
                .build();
        processService.save(process4);

        Standard standard5 = Standard.builder()
                .backPressurePeak(146.6)
                .closingForce(917.1)
                .clampingForcePeak(935.9)
                .cycleTime(74.8)
                .meltTemperature(105.515)
                .moldTemperature(80.659)
                .plasticizingTime(4.01)
                .injPressurePeak(903.6)
                .screwPosEndHold(8.81)
                .shotVolume(18.76)
                .timeToFill(6.968)
                .torqueMean(93)
                .torquePeak(111.5)
                .build();
        standardService.save(standard5);
        Process process5 = Process.builder()
                .status("PENDING")
                .order(order)
                .processDate(LocalDateTime.now())
                .standard(standard5)
                .build();
        processService.save(process5);

        Standard standard6 = Standard.builder()
                .backPressurePeak(146.3)
                .closingForce(881.9)
                .clampingForcePeak(897)
                .cycleTime(74.8)
                .meltTemperature(106.465)
                .moldTemperature(81.38)
                .plasticizingTime(3.19)
                .injPressurePeak(926.3)
                .screwPosEndHold(8.82)
                .shotVolume(18.75)
                .timeToFill(6.968)
                .torqueMean(104.7)
                .torquePeak(119.3)
                .build();
        standardService.save(standard6);
        Process process6 = Process.builder()
                .status("PENDING")
                .order(order)
                .processDate(LocalDateTime.now())
                .standard(standard6)
                .build();
        processService.save(process6);

        Standard standard7 = Standard.builder()
                .backPressurePeak(145.6)
                .closingForce(886.9)
                .clampingForcePeak(904)
                .cycleTime(74.83)
                .meltTemperature(106.476184302648)
                .moldTemperature(80.617)
                .plasticizingTime(3.16)
                .injPressurePeak(922.3)
                .screwPosEndHold(8.82)
                .shotVolume(18.73)
                .timeToFill(7.124)
                .torqueMean(104.3)
                .torquePeak(116.9)
                .build();
        standardService.save(standard7);
        Process process7 = Process.builder()
                .status("PENDING")
                .order(order)
                .processDate(LocalDateTime.now())
                .standard(standard7)
                .build();
        processService.save(process7);

        Standard standard8 = Standard.builder()
                .backPressurePeak(145.6)
                .closingForce(919.409791006952)
                .clampingForcePeak(935.9)
                .cycleTime(74.81)
                .meltTemperature(105.505)
                .moldTemperature(81.362)
                .plasticizingTime(3.16)
                .injPressurePeak(930.5)
                .screwPosEndHold(8.59)
                .shotVolume(18.73)
                .timeToFill(6.968)
                .torqueMean(104.9)
                .torquePeak(113.9)
                .build();
        standardService.save(standard8);
        Process process8 = Process.builder()
                .status("PENDING")
                .order(order)
                .processDate(LocalDateTime.now())
                .standard(standard8)
                .build();
        processService.save(process8);

    }

}
