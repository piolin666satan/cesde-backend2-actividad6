package com.example.demo_basic.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo_basic.model.entity.CitaEntity;

public interface CitaRepository extends JpaRepository<CitaEntity, Long> {
    
    // Al usar el guion bajo (_Id), Spring busca el atributo 'id' dentro de 'odontologo'
    // Como tu variable en CitaEntity ya se llama 'odontologo', esto funcionará perfecto.
    boolean existsByOdontologo_IdAndFechaHora(Long odontologoId, LocalDateTime fechaHora);

    @Query("SELECT COUNT(c) FROM CitaEntity c WHERE c.paciente.id = :pacienteId " + 
        "AND c.estado = 'PENDIENTE' " + 
        "AND c.fechaHora BETWEEN :inicio AND :fin")
    Long countCitasPendientesSemana(Long pacienteId, LocalDateTime inicio, LocalDateTime fin);
}