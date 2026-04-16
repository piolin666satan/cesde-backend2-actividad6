package com.example.demo_basic.model.embeddable;

import com.example.demo_basic.model.enums.MotivoCita;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DetalleCita {
    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cita", nullable = false)
    private MotivoCita motivoCita;

    @Column(name = "costo", nullable = false)
    private double costo;
}
