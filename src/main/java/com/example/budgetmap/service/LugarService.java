package com.example.budgetmap.service;

import com.example.budgetmap.model.Lugar;
import com.example.budgetmap.repository.LugarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LugarService {

    private final LugarRepository lugarRepository;

    public LugarService(LugarRepository lugarRepository) {
        this.lugarRepository = lugarRepository;
    }

    public List<Lugar> findAll() {
        return lugarRepository.findAll();
    }

    public Optional<Lugar> findById(Long id) {
        return lugarRepository.findById(id);
    }

    public Lugar save(Lugar lugar) {
        return lugarRepository.save(lugar);
    }

    public void deleteById(Long id) {
        lugarRepository.deleteById(id);
    }

    public List<Lugar> findByCiudad(String ciudad) {
        return lugarRepository.findByCiudadIgnoreCase(ciudad);
    }

    // Helpers usados por HomeController (implementaciones simples)
    public List<Lugar> findFeatured(int limit) {
        // Implementación simple: devolver first N elementos; puedes cambiar por
        // criterios reales
        return lugarRepository.findAll().stream().limit(limit).toList();
    }
}
