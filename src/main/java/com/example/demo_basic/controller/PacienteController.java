package com.example.demo_basic.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo_basic.model.entity.PacienteEntity;
import com.example.demo_basic.service.PacienteService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Gestion de pacientes")
public class PacienteController {
        private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public List<PacienteEntity> listarPacientes() {
        return pacienteService.listarPacientes();
    }

    @PostMapping
    public PacienteEntity crearPaciente(@RequestBody PacienteEntity paciente) {
        return pacienteService.guardarPaciente(paciente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteEntity> obtenerPaciente(@PathVariable Long id) {
        return pacienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
