package com.example.demo_basic.model.entity;

import com.example.demo_basic.model.enums.TipoEspecialidad;

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

public class OdontologoEntity extends BaseEntity {
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoEspecialidad especialidad;

    @Column(name = "tarjeta_profesional", nullable = false, unique = true)
    private int tarjetaProfesional;

    @ManyToOne
    private Paciente paciente;

    @ManyToMany
    private Cita cita;
}
