package com.example.refoam;

import com.example.refoam.domain.Employee;
import com.example.refoam.domain.Material;
import com.example.refoam.domain.MaterialName;
import com.example.refoam.domain.PositionName;
import com.example.refoam.service.EmployeeService;
import com.example.refoam.service.MaterialService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class DummyDataLoader implements CommandLineRunner {
    private final EmployeeService employeeService;
    private final MaterialService materialService;


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
    }
}
