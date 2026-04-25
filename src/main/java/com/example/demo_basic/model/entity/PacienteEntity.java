package com.example.demo_basic.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "PacienteEntity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PacienteEntity extends BaseEntity {
    
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "edad", nullable = false)
    private Integer  edad;

    @Column(name = "tiene_seguro", nullable = false)
    private Boolean tieneSeguro;

    @Column(name = "nombre_acudiente", length = 80)
    private String nombreAcudiente; 

     public int getEdad() { return edad; }
     public boolean isTieneSeguro() { return tieneSeguro; }
     public String getNombreAcudiente() { return nombreAcudiente; }

 
    
}