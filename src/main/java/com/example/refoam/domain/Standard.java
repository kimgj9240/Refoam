package com.example.refoam.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @OneToMany(mappedBy = "standard", cascade = CascadeType.ALL)
    private List<Process> processes = new ArrayList<>();
}
