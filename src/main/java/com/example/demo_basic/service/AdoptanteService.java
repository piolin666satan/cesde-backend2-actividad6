package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.Adoptante;
import com.example.demo_basic.repository.AdoptanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdoptanteService {

    @Autowired
    private AdoptanteRepository adoptanteRepository;

    public List<Adoptante> findAll() {
        return adoptanteRepository.findAll();
    }

    public Adoptante findById(Long id) {
        return findEntity(id);
    }

    @Transactional
    public Adoptante save(Adoptante request) {
        return adoptanteRepository.save(request);
    }

    @Transactional
    public Adoptante update(Long id, Adoptante request) {
        Adoptante adoptante = findEntity(id);
        adoptante.setNombre(request.getNombre());
        adoptante.setIdentificacion(request.getIdentificacion());
        adoptante.setEdad(request.getEdad());
        adoptante.setTienePatio(request.getTienePatio());
        return adoptanteRepository.save(adoptante);
    }

    @Transactional
    public void delete(Long id) {
        findEntity(id);
        adoptanteRepository.deleteById(id);
    }

    public Adoptante findEntity(Long id) {
        return adoptanteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Adoptante no encontrado con id: " + id));
    }
}
