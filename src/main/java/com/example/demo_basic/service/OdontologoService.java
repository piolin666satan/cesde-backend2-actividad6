package com.example.demo_basic.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo_basic.model.entity.OdontologoEntity;
import com.example.demo_basic.repository.OdontologoRepository;

@Service
public class OdontologoService {
    @Autowired
    private OdontologoRepository odontologoRepository;

    public List<OdontologoEntity> listarTodos() {
        return odontologoRepository.findAll();
    }

    public OdontologoEntity guardar(OdontologoEntity odontologo) {
        return odontologoRepository.save(odontologo);
    }

    public OdontologoEntity obtenerPorId(Long id) {
        return odontologoRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        odontologoRepository.deleteById(id);
    }
}

