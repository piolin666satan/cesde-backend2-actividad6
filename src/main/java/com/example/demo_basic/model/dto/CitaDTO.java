package com.example.demo_basic.model.dto;

import java.time.LocalDateTime;

import com.example.demo_basic.model.enums.MotivoCita;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CitaDTO {
    private Long odontologoId;
    private Long pacienteId;
    private LocalDateTime fechaHora;
    private MotivoCita motivoCita;
    private Double costo;
    private String nombreAcudiente;  //para validacion de algun menor de edad...
}
