package com.example.demo_basic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo_basic.model.entity.PacienteEntity;

public interface PacienteRepository extends JpaRepository<PacienteEntity, Long>{
    
}
