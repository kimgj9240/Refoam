package com.example.refoam.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateForm {

    private  Long id;

    private String loginId;

    private String username;

    private String password;

    @NotEmpty(message = "직위는 필수입니다.")
    private String position;

    @NotEmpty(message = "전화번호입력은 필수입니다.")
    private String phone;

}
