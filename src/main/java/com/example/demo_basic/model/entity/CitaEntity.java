package com.example.demo_basic.model.entity;

import java.time.LocalDateTime;

import com.example.demo_basic.model.embeddable.DetalleCita;
import com.example.demo_basic.model.enums.EstadoCita;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "citas")
public class CitaEntity extends BaseEntity {

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Embedded
    private DetalleCita detalleCita;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne
    @JoinColumn(name = "odontologo_id", nullable = false)
    private OdontologoEntity odontologo;

    public Object stream() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stream'");
    }
}
