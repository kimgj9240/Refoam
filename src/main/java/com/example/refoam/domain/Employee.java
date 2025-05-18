package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.*;

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
    private long id;

    private String loginId;

    private String username;

    @Setter
    private String password;

    @Setter
    private String phone;

    @Setter
    @Enumerated(EnumType.STRING)
    private PositionName position;

    @OneToMany(mappedBy = "employee")
    private List<Order> orders= new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    private List<Material> materials = new ArrayList<>();


}
