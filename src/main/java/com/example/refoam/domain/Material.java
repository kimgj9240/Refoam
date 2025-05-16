package com.example.refoam.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@ToString(exclude = "materialQuantity")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private long id;

    @Setter
    @Enumerated(EnumType.STRING)
    private MaterialName materialName;

    @Setter
    private int materialQuantity;

    @Getter
    private LocalDateTime materialDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
