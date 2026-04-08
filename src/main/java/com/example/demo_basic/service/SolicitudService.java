package com.example.demo_basic.service;

import com.example.demo_basic.model.entity.Adoptante;
import com.example.demo_basic.model.entity.Mascota;
import com.example.demo_basic.model.entity.Solicitud;
import com.example.demo_basic.model.enums.EstadoMascota;
import com.example.demo_basic.model.enums.EstadoSolicitud;
import com.example.demo_basic.model.enums.TamanoMascota;
import com.example.demo_basic.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private AdoptanteService adoptanteService;

    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }

    public Solicitud findById(Long id) {
        return findEntity(id);
    }

    public List<Solicitud> findByEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado);
    }

    public List<Solicitud> findByAdoptante(Long adoptanteId) {
        return solicitudRepository.findByAdoptanteId(adoptanteId);
    }

    @Transactional
    public Solicitud crearSolicitud(Solicitud request) {
        Mascota mascota = mascotaService.findEntity(request.getMascota().getId());
        Adoptante adoptante = adoptanteService.findEntity(request.getAdoptante().getId());

        validarMascotaDisponible(mascota);
        validarMayorEdad(adoptante);
        validarMaximoSolicitudesActivas(adoptante);
        validarPatioSiEsGrande(mascota, adoptante);

        request.setMascota(mascota);
        request.setAdoptante(adoptante);
        request.setEstado(EstadoSolicitud.PENDIENTE);

        mascota.setEstado(EstadoMascota.EN_PROCESO);
        mascotaService.save(mascota);

        return solicitudRepository.save(request);
    }

    @Transactional
    public Solicitud cambiarEstado(Long id, EstadoSolicitud nuevoEstado) {
        Solicitud solicitud = findEntity(id);
        Mascota mascota = mascotaService.findEntity(solicitud.getMascota().getId());

        solicitud.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoSolicitud.APROBADA) {
            mascota.setEstado(EstadoMascota.ADOPTADO);
            mascotaService.save(mascota);
        }

        if (nuevoEstado == EstadoSolicitud.RECHAZADA) {
            mascota.setEstado(EstadoMascota.DISPONIBLE);
            mascotaService.save(mascota);
        }

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public void delete(Long id) {
        findEntity(id);
        solicitudRepository.deleteById(id);
    }

    private void validarMascotaDisponible(Mascota mascota) {
        if (mascota.getEstado() != EstadoMascota.DISPONIBLE) {
            throw new IllegalArgumentException("La mascota debe estar DISPONIBLE para crear la solicitud.");
        }
    }

    private void validarMayorEdad(Adoptante adoptante) {
        if (adoptante.getEdad() == null || adoptante.getEdad() <= 18) {
            throw new IllegalArgumentException("El adoptante debe ser mayor de 18 años.");
        }
    }

    private void validarMaximoSolicitudesActivas(Adoptante adoptante) {
        long activas = solicitudRepository.countByAdoptanteIdAndEstado(adoptante.getId(), EstadoSolicitud.PENDIENTE);
        if (activas >= 2) {
            throw new IllegalArgumentException("El adoptante ya tiene 2 solicitudes activas.");
        }
    }

    private void validarPatioSiEsGrande(Mascota mascota, Adoptante adoptante) {
        if (mascota.getTamano() == TamanoMascota.GRANDE && !Boolean.TRUE.equals(adoptante.getTienePatio())) {
            throw new IllegalArgumentException("Para una mascota GRANDE el adoptante debe tener patio.");
        }
    }

    private Solicitud findEntity(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada con id: " + id));
    }
}
