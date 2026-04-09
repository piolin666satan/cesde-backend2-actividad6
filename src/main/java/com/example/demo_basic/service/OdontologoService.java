package com.example.demo_basic.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo_basic.model.entity.Odontologo;
import com.example.demo_basic.repository.OdontologoRepository;

@Service
public class OdontologoService {
    @Autowired
    private OdontologoRepository odontologoRepository;

    public List<Odontologo> listarTodos() {
        return odontologoRepository.findAll();
    }

    public Odontologo guardar(Odontologo odontologo) {
        return odontologoRepository.save(odontologo);
    }

    public Odontologo obtenerPorId(Long id) {
        return odontologoRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        odontologoRepository.deleteById(id);
    }
}

