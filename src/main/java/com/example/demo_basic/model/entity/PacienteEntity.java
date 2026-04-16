package com.example.demo_basic.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
public class PacienteEntity extends BaseEntity {

    private String nombre;
    private String documento;
    // Otros campos...
}
