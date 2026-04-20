package com.example.demo_basic.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo_basic.model.entity.OdontologoEntity;
import com.example.demo_basic.service.OdontologoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/odontologos")
public class OdontologoController {
    @Autowired
    private OdontologoService odontologoService;

    @GetMapping
    public List<OdontologoEntity> listar() {
        return odontologoService.listarTodos();
    }

    @PostMapping
    public OdontologoEntity crear(@RequestBody OdontologoEntity odontologo) {
        return odontologoService.guardar(odontologo);
    }

    @GetMapping("/{id}")
    public OdontologoEntity obtener(@PathVariable Long id) {
        return odontologoService.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        odontologoService.eliminar(id);
    }
}