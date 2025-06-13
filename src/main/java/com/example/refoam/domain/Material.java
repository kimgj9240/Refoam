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
    private Long id;

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

    // 퇴사자 표기용 임시 표시 이름
    @Transient  // db 추가 안되게
    @Getter @Setter
    private String materialDisplayName;

}
