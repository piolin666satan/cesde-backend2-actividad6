package com.example.demo_basic.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "odontologos")

public class Odontologo extends BaseEntity {
    
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "especialidad", nullable = false, length = 100)
    private String especialidad;

    @Column(name = "tarjeta_profesional", nullable = false, unique = true)
    private int tarjetaProfesional;
}
