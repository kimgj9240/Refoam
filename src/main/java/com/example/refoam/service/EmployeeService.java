package com.example.refoam.service;

import com.example.refoam.domain.Employee;
import com.example.refoam.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        Employee employee = employeeRepository.findByLoginId(loginId).orElse(null);
        if (employee == null){ return null;}
        return employee;
    }

    //전체 회원 조회
    public List<Employee> employeeList(){
        return employeeRepository.findAll();
    }

    // 단건 조회
    public Optional<Employee> findOneEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    //find login id
    public Optional<Employee> findLoginIdEmployee(String loginId) {
        return employeeRepository.findByLoginId(loginId);
    }

    // delete employee
    @Transactional
    public void deleteEmployee(Long employeeId) {
        this.employeeRepository.deleteById(employeeId);
    }

}
