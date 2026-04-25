package com.example.demo_basic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo_basic.model.entity.PacienteEntity;

@Repository
public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {
    
}
