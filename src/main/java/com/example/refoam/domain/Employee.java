package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    private String loginId;

    private String username;

    @Setter
    private String password;

    @Setter
    private String email;

    @Setter
    private boolean sendMail;

    @Setter
    @Enumerated(EnumType.STRING)
    private PositionName position;

    // 주문이력있는 직원 삭제를 위한 코드
    @Builder.Default // @Builder가 있으면 필드 초기값을 무시하고 null/false로 초기화해버려서 이렇게 따로 선언해야함
    @Getter
    @Column(nullable = false)
    private boolean active = true;  //퇴사 시 false

    // 비번이나 민감한 정보가 유출이 될 수 있어서 setter대신에 직접 메서드가 낫다함
    @SuppressWarnings("unused")
    public void setActive(boolean active) {
        this.active = active;
    }


    @OneToMany(mappedBy = "employee")
    private List<Orders> orders= new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    private List<Material> materials = new ArrayList<>();




}
