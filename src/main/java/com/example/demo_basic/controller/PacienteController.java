package com.example.demo_basic.controller;

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
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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