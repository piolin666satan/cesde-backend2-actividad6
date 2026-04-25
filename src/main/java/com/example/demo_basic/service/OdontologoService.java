package com.example.demo_basic.service;

import java.time.LocalDateTime;
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
        if (odontologo.getTarjetaProfesional() <= 0) {
            throw new RuntimeException("La tarjeta profesional debe ser un número positivo.");
        }
        return odontologoRepository.save(odontologo);
    }

    public void validarDisponibilidad(Long odontologoId, LocalDateTime fechaHoraNueva) {
        OdontologoEntity odontologo = odontologoRepository.findById(odontologoId)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        if (odontologo.getCita() != null) {
            LocalDateTime fechaCitaActual = odontologo.getCita().getFechaHora();
            
            if (fechaCitaActual.equals(fechaHoraNueva)) {
                throw new RuntimeException("El odontólogo " + odontologo.getNombre() + 
                                           " ya tiene una cita en ese horario.");
            }
        }
    }

    public OdontologoEntity obtenerPorId(Long id) {
        return odontologoRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        odontologoRepository.deleteById(id);
    }
}