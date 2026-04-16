package com.example.demo_basic.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Gestion de pacientes")
public class PacienteController {
    
}
