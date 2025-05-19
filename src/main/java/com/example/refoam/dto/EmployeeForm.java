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
public class EmployeeForm {

    private  Long id;
    @NotEmpty(message = "아이디입력은 필수입니다.")
    private String loginId;

    @NotEmpty(message = "이름입력은 필수입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;

    @NotEmpty(message = "직위는 필수입니다.")
    private String position;

    @NotEmpty(message = "이메일입력은 필수입니다.")
    private String email;
}
