package com.example.demo_basic.controller;

<<<<<<< HEAD
import com.example.demo_basic.model.entity.PacienteEntity;
import com.example.demo_basic.service.PacienteService;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<PacienteEntity> getAll() {
        return pacienteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteEntity> getById(@PathVariable Long id) {
        return pacienteService.findById(id)
=======
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
>>>>>>> origin/feacture/pacientes
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

<<<<<<< HEAD
    @PostMapping
    public PacienteEntity create(@RequestBody PacienteEntity paciente) {
        return pacienteService.save(paciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<@Nullable Object> update(@PathVariable Long id, @RequestBody PacienteEntity paciente) {
        // Asumiendo que tu service maneja la lógica de actualización
        return ResponseEntity.ok(pacienteService.update(id, paciente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pacienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
=======
}
>>>>>>> origin/feacture/pacientes
