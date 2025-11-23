package com.example.budgetmap.service;

import com.example.budgetmap.model.Establecimiento;
import com.example.budgetmap.repository.EstablecimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EstablecimientoService {

    private final EstablecimientoRepository establecimientoRepository;

    public EstablecimientoService(EstablecimientoRepository establecimientoRepository) {
        this.establecimientoRepository = establecimientoRepository;
    }

    public List<Establecimiento> findAll() {
        return establecimientoRepository.findAll();
    }

    public Optional<Establecimiento> findById(Long id) {
        return establecimientoRepository.findById(id);
    }

    public Establecimiento save(Establecimiento establecimiento) {
        return establecimientoRepository.save(establecimiento);
    }

    public void deleteById(Long id) {
        establecimientoRepository.deleteById(id);
    }

    public List<Establecimiento> findByCiudad(String ciudad) {
        return establecimientoRepository.findByCiudadIgnoreCase(ciudad);
    }

    public List<Establecimiento> findByCreadoPor(Long usuarioId) {
        return establecimientoRepository.findByCreadoPorId(usuarioId);
    }

    public List<Establecimiento> findByEstado(String estado) {
        return establecimientoRepository.findByEstado(estado);
    }

    // Helper simple para HomeController
    public List<Establecimiento> findTop10() {
        return establecimientoRepository.findAll().stream().limit(10).toList();
    }
}
