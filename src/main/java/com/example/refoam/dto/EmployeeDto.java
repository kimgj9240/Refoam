package com.example.refoam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import com.example.refoam.domain.Employee;

@Getter
@AllArgsConstructor
@Builder
public class EmployeeDto {
    // 주문을 한 적 있는 한 일개 직원을 삭제가능하게하고 그 직원이 퇴사했다는거를 뜨게 만드는 용

    private Long id;
    private String loginId;
    private String username;
    private String email;
    private String position;
    private String displayName; // 퇴사자 표시용 이름

    public static EmployeeDto from(Employee employee) {
        String displayName = employee.getUsername();
        if (!employee.isActive()) {
            displayName += " (퇴사)";
        }

        return EmployeeDto.builder()
                .id(employee.getId())
                .loginId(employee.getLoginId())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .position(String.valueOf(employee.getPosition()))
                .displayName(displayName)
                .build();
    }
}
