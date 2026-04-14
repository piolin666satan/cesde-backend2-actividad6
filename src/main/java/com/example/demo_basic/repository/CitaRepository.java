package com.example.demo_basic.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo_basic.model.entity.CitaEntity;

public interface CitaRepository extends JpaRepository<CitaEntity, Long> {
    
    //verifica que el odontologo no tenga una cita en el mismo horario.
    boolean existsByOdontologo_IdAndFechaHora(Long odontologoId, LocalDateTime fechaHora);

    //Cuenta cuántas citas "Pendiente" tiene un paciente en una semana determinada.
    @Query("SELECT COUNT(c) FROM CitaEntity c WHERE c.paciente.id = :pacienteId " + 
        "AND c.estado = 'PENDIENTE' " + 
        "AND c.fechaHora BETWEEN :inicio AND :fin")
    Long countCitasPendientesSemana(Long pacienteId, LocalDateTime inicio, LocalDateTime fin);
}