package com.example.demo_basic.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo_basic.model.embeddable.DetalleCita;
import com.example.demo_basic.model.dto.CitaDTO;
import com.example.demo_basic.model.entity.CitaEntity;
import com.example.demo_basic.model.entity.OdontologoEntity;
import com.example.demo_basic.model.entity.PacienteEntity;
import com.example.demo_basic.model.enums.EstadoCita;
//import com.example.demo_basic.repository.OdontologoRepository;
//import com.example.demo_basic.repository.PacienteRepository;
import com.example.demo_basic.repository.CitaRepository;

@Service
public class CitaService {
    
    @Autowired
    private CitaRepository citaRepository;

    //@Autowired
    //private OdontologoRepository odontologoRepository;

    //@Autowired
    //private PacienteRepository pacienteRepository;

    public CitaEntity crearCita(CitaDTO dto) {

        // 1. Validar choque de horario del odontologo.
        if (citaRepository.existsByOdontologo_IdAndFechaHora(dto.getOdontologoId(), dto.getFechaHora())) {
            throw new RuntimeException("El odontólogo ya tiene una cita en ese horario.");
        }

        //Verificar que el paciente no tenga mas de una cita.
        double costoFinal = (dto.getCosto() != null) ? dto.getCosto() : 0.0;

/*
        PacienteEntity paciente = pacienteService.obtenerPorId(dto.getPacienteId());
        
        // Regla: Si es menor de edad, requerir acudiente
        if (paciente.getEdad() < 18 && (dto.getNombreAcudiente() == null || dto.getNombreAcudiente().isEmpty())) {
            throw new RuntimeException("Paciente menor de edad requiere acudiente obligatorio.");
        }

        // Regla: Calcular costo (30% descuento si tiene seguro)
        if (paciente.getTieneSeguro()) {
            costoFinal = costoFinal * 0.70;
        }
        */

        // 3. Validar máximo una cita "Pendiente" en la misma semana
        LocalDateTime inicioSemana = dto.getFechaHora().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime finSemana = inicioSemana.plusDays(6).with(LocalTime.MAX);
        
        if (citaRepository.countCitasPendientesSemana(dto.getPacienteId(), inicioSemana, finSemana) >= 1) {
            throw new RuntimeException("El paciente ya tiene una cita pendiente esta semana.");
        }


        //Entidad
        CitaEntity cita = new CitaEntity();
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado(EstadoCita.PENDIENTE);

        //Embebido
        DetalleCita detalle = new DetalleCita();
        detalle.setMotivoCita(dto.getMotivoCita());
        detalle.setCosto(costoFinal);
        cita.setDetalleCita(detalle);

        PacienteEntity p = new PacienteEntity(); p.setId(dto.getPacienteId());
        cita.setPaciente(p);
        
        OdontologoEntity o = new OdontologoEntity(); o.setId(dto.getOdontologoId());
        cita.setOdontologo(o);

        return citaRepository.save(cita);
    }

    public void cancelarCita(Long id) {
        CitaEntity cita = citaRepository.findById(id).orElseThrow();

        // 4. Impedir cancelación si faltan menos de 2 horas
        if (LocalDateTime.now().plusHours(2).isAfter(cita.getFechaHora())) {
            throw new RuntimeException("No se puede cancelar con menos de 2 horas de anticipación.");
        }

        citaRepository.delete(cita);
    }

    public List<CitaEntity> obtenerTodas() {
        return citaRepository.findAll();
    }
    
}
