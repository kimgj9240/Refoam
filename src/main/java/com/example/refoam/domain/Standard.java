package com.example.refoam.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Standard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standard_id")
    private Long id;

    private double meltTemperature;

    private double moldTemperature;

    private double timeToFill;

    private double plasticizingTime;

    private double cycleTime;

    private double closingForce;

    private double clampingForcePeak;

    private double torquePeak;

    private double torqueMean;

    private double backPressurePeak;

    private double injPressurePeak;

    private double screwPosEndHold;

    private double shotVolume;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @OneToMany(mappedBy = "standard")
    private List<Label> labels = new ArrayList<>();

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL)
    private List<Process> processes = new ArrayList<>();
}
