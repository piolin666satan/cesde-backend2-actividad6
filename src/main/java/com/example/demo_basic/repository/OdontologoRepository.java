package com.example.demo_basic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo_basic.model.entity.Odontologo;

@Repository
public interface OdontologoRepository  extends JpaRepository<Odontologo, Long> {
    
}
