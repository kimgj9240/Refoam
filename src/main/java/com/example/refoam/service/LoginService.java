package com.example.refoam.service;

import com.example.refoam.domain.Employee;
import com.example.refoam.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final EmployeeRepository employeeRepository;

    public Employee login(String loginId, String password){
        Employee employee = employeeRepository.findByLoginIdAndActiveTrue(loginId).orElse(null);

        if (employee == null){
            return null;
        }

        if (employee.getPassword().equals(password)){
            return employee;
        }
        return null;
    }
}
