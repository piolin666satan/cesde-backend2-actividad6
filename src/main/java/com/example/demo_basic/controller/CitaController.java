package com.example.demo_basic.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo_basic.model.dto.CitaDTO;
import com.example.demo_basic.model.entity.CitaEntity;
import com.example.demo_basic.service.CitaService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/citas")
public class CitaController {
    
    @Autowired
    private CitaService citaService;

    @GetMapping
    public List<CitaEntity> listarTodas() {
        return citaService.obtenerTodas();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody CitaDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crearCita(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(Long id) {
        try {
            citaService.cancelarCita(id);
            return ResponseEntity.ok("Cita cancelada correctamente...");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
