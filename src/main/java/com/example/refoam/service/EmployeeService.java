package com.example.refoam.service;

import com.example.refoam.domain.Employee;
import com.example.refoam.dto.EmployeeDto;
import com.example.refoam.repository.EmployeeRepository;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@lombok.extern.slf4j.Slf4j
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    //회원가입
    @Transactional
    public Long save(Employee employee) {
        employeeRepository.save(employee);
        return employee.getId();
    }

    // 중복회원 검증
    public Employee validateDuplicate(String loginId){
        Employee employee = employeeRepository.findByLoginIdAndActiveTrue(loginId).orElse(null);
        if (employee == null){ return null;}
        return employee;
    }

    //전체 회원 조회
    public List<EmployeeDto> employeeList() {
        return employeeRepository.findAll().stream()
                .filter(Employee::isActive) // 퇴사자는 안 보이게
                .map(EmployeeDto::from)
                .collect(Collectors.toList());
    }


    // 단건 조회
    public Optional<Employee> findOneEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    //find login id
    public Optional<Employee> findLoginIdEmployee(String loginId) {
        return employeeRepository.findByLoginIdAndActiveTrue(loginId);
    }

    // delete employee 진짜로 삭제하면 문제 생겨서 비활성화처리만 하는거로
    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("직원이 없습니다."));

        employee.setActive(false); // 실제 삭제 대신 비활성화
    }


    // 페이징 구현용
    public Page<EmployeeDto> getList(int page) {
        // 최신순으로 보이게하기
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));

        PageRequest pageable = PageRequest.of(page, 12, Sort.by(sorts));

        Page<Employee> employees = employeeRepository.findAll(pageable);

        // 퇴사자 제외하고 DTO 변환
        List<EmployeeDto> filtered = employees.getContent().stream()
                .filter(Employee::isActive)
                .map(EmployeeDto::from)
                .collect(Collectors.toList());

        log.info("직원 리스트: {}",
                employees.getContent().stream().map(Employee::getLoginId).collect(Collectors.toList()));


        return new PageImpl<>(filtered, pageable, filtered.size());
    }



}
