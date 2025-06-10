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
import java.time.format.DateTimeFormatter;
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
    private final StandardEvaluator standardEvaluator;


    @Override
    public void run(String... args) throws Exception {
        //run(String... args) <가변 인자 :기본적으로 여러개의 String 값을 받을 수 있는 배열같은 개념
        Employee employee = Employee.builder()
                .loginId("test")
                .username("관리자")
                .password("1111")
                .position(PositionName.ADMIN)
                .email("refoam.test@gmail.com")
                .build();
        employeeService.save(employee);

        Employee employee2 = Employee.builder()
                .loginId("test2")
                .username("직원")
                .password("1111")
                .position(PositionName.STAFF)
                .email("refoam.test@gmail.com")
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

                double errorCount = 0;
                for (int i = 0; i < orderqty; i++) {
                    // 로트넘버 생성
                    final int index = i;
                    int sequenceNumber = index % 10 + 1;
                    int lotNumberIndex = index / 10 + 1;
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd"); // 현재 날짜를 YYYYMMDD 형식으로 변환
                    String currentDate = baseDate.format(dateTimeFormatter);
                    String lot = String.format("%02d",lotNumberIndex);
                    String sequence = String.format("%03d",sequenceNumber);

                    String lotNumber = orders1.getProductName().name() + "_" + orders1.getId() + "_"
                            + lot + "_" + sequence + "_" + currentDate;

                    // 공정 1건 생성  => 라벨을 확률에 따라 임의로 생성하면서 더미데이터의 품질 검증 시 오류가 과다 발생됨. standard 값을 기준으로 라벨 붙이게 수정
                    Standard standard = productStandardValue.createStandard();
                    ProductLabel label1 = standardEvaluator.evaluate(standard.getInjPressurePeak(), standard.getMoldTemperature(), standard.getTimeToFill(),
                            standard.getCycleTime(), standard.getPlasticizingTime());
                    standard.setProductLabel(label1);
                    standardService.save(standard);
                    if (label1.equals(ProductLabel.OK)){
                        orders1.setCompletedCount(orders1.getCompletedCount() + 1);
                        orderService.save(orders1);

                    }else {
                        errorCount += 1;
                    }
                    double errorRate = Math.round(errorCount / orderqty * 100.0) / 100.0;
                    orders1.setErrorRate(errorRate);
                    orderService.save(orders1);

                    Process process = Process.builder()
                            .status(String.valueOf(label1))
                            .order(orders1)
                            .lotNumber(lotNumber)
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


