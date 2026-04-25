package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.PacienteEntity;
import com.example.demo_basic.repository.PacienteRepository; // Ajusta el paquete según tu proyecto

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<PacienteEntity> findAll() {
        return pacienteRepository.findAll();
    }

    public Optional<PacienteEntity> findById(Long id) {
        return pacienteRepository.findById(id);
    }

    public PacienteEntity save(PacienteEntity paciente) {
        return pacienteRepository.save(paciente);
    }

    public void deleteById(Long id) {
        pacienteRepository.deleteById(id);
    }

    public @Nullable Object update(Long id, PacienteEntity paciente) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}