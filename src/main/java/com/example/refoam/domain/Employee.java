package com.example.refoam.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
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
    private String email;

    @Setter
    @Enumerated(EnumType.STRING)
    private PositionName position;

    @OneToMany(mappedBy = "employee")
    private List<Order> orders= new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    private List<Material> materials = new ArrayList<>();


}
