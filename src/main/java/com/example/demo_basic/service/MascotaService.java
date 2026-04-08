package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.Mascota;
import com.example.demo_basic.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    public Mascota findById(Long id) {
        return findEntity(id);
    }

    @Transactional
    public Mascota save(Mascota request) {
        return mascotaRepository.save(request);
    }

    @Transactional
    public Mascota update(Long id, Mascota request) {
        Mascota mascota = findEntity(id);
        mascota.setNombre(request.getNombre());
        mascota.setEspecie(request.getEspecie());
        mascota.setEdad(request.getEdad());
        mascota.setTamano(request.getTamano());
        mascota.setEstado(request.getEstado());
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public void delete(Long id) {
        findEntity(id);
        mascotaRepository.deleteById(id);
    }

    public Mascota findEntity(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mascota no encontrada con id: " + id));
    }
}
