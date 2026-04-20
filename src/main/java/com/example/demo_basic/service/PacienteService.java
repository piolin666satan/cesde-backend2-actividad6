package com.example.demo_basic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo_basic.model.entity.PacienteEntity;
import com.example.demo_basic.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public PacienteEntity guardarPaciente(PacienteEntity paciente) {
        // Validación de nombre
        if (paciente.getNombre() == null || paciente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio");
        }

        // Validación de edad
        if (paciente.getEdad() == null || paciente.getEdad() <= 0 || paciente.getEdad() > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 1 y 120 años");
        }

        // Validación de seguro
        if (paciente.getTieneSeguro() == null) {
            throw new IllegalArgumentException("Debe especificar si el paciente tiene seguro");
        }

        // Validación de duplicados (ejemplo simple)
        boolean existe = pacienteRepository
                .findAll()
                .stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(paciente.getNombre())
                        && p.getEdad().equals(paciente.getEdad()));
        if (existe) {
            throw new IllegalArgumentException("Ya existe un paciente con ese nombre y edad");
        }

        return pacienteRepository.save(paciente);
    }

public List<PacienteEntity> listarPacientes() {
    return pacienteRepository.findAll();
}

public Optional<PacienteEntity> buscarPorId(Long id) {
    return pacienteRepository.findById(id);
}

public void eliminarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }

}
