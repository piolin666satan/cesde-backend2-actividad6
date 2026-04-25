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
import com.example.demo_basic.repository.OdontologoRepository;
import com.example.demo_basic.repository.PacienteRepository;
import com.example.demo_basic.repository.CitaRepository;

@Service
public class CitaService {
    
    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public CitaEntity crearCita(CitaDTO dto) {

        // 1. Validar choque de horario del odontologo.
        if (citaRepository.existsByOdontologo_IdAndFechaHora(dto.getOdontologoId(), dto.getFechaHora())) {
            throw new RuntimeException("El odontólogo ya tiene una cita en ese horario.");
        }

        double costoFinal = (dto.getCosto() != null) ? dto.getCosto() : 0.0;

        // Buscamos el paciente real de la base de datos
        PacienteEntity paciente = pacienteRepository.findById(dto.getPacienteId())
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + dto.getPacienteId()));
        
        // Regla: Si es menor de edad, requerir acudiente
        if (paciente.getEdad() < 18 && (dto.getNombreAcudiente() == null || dto.getNombreAcudiente().isEmpty())) {
            throw new RuntimeException("Paciente menor de edad requiere acudiente obligatorio.");
        }

        // Regla: Calcular costo (30% descuento si tiene seguro)
        if (paciente.getTieneSeguro()) {
            costoFinal = costoFinal * 0.70;
        }
        */

        LocalDateTime inicioSemana = dto.getFechaHora().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime finSemana = inicioSemana.plusDays(6).with(LocalTime.MAX);
        
        if (citaRepository.countCitasPendientesSemana(dto.getPacienteId(), inicioSemana, finSemana) >= 1) {
            throw new RuntimeException("El paciente ya tiene una cita pendiente esta semana.");
        }

        // Buscamos el odontólogo real de la base de datos
        OdontologoEntity odontologo = odontologoRepository.findById(dto.getOdontologoId())
            .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado con ID: " + dto.getOdontologoId()));

        CitaEntity cita = new CitaEntity();
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado(EstadoCita.PENDIENTE);

        DetalleCita detalle = new DetalleCita();
        detalle.setMotivoCita(dto.getMotivoCita());
        detalle.setCosto(costoFinal);
        cita.setDetalleCita(detalle);

        // Asignamos las entidades PERSISTIDAS (traídas del repo)
        cita.setPaciente(paciente);
        cita.setOdontologo(odontologo);

        return citaRepository.save(cita);
    }

    public void cancelarCita(Long id) {
        CitaEntity cita = citaRepository.findById(id).orElseThrow();

        if (LocalDateTime.now().plusHours(2).isAfter(cita.getFechaHora())) {
            throw new RuntimeException("No se puede cancelar con menos de 2 horas de anticipación.");
        }

        citaRepository.delete(cita);
    }

    public List<CitaEntity> obtenerTodas() {
        return citaRepository.findAll();
    }
}