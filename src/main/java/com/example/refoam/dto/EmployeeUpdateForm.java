package com.example.refoam.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotEmpty(message = "이메일입력은 필수입니다.")
    private String email;

    @NotNull(message = "메일알림 여부를 체크하세요.")
    private boolean sendMail;

}
