package com.example.budgetmap.service;

import com.example.budgetmap.model.Pqrs;
import com.example.budgetmap.model.Usuario;
import com.example.budgetmap.model.enums.EstadoPqrs;
import com.example.budgetmap.repository.PqrsRepository;
import com.example.budgetmap.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PqrsService {

    private final PqrsRepository pqrsRepository;
    private final UsuarioRepository usuarioRepository;

    public PqrsService(PqrsRepository pqrsRepository,
            UsuarioRepository usuarioRepository) {
        this.pqrsRepository = pqrsRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Pqrs> findAll() {
        return pqrsRepository.findAll();
    }

    public Optional<Pqrs> findById(Long id) {
        return pqrsRepository.findById(id);
    }

    public Pqrs save(Pqrs pqrs) {
        if (pqrs.getEstado() == null)
            pqrs.setEstado(EstadoPqrs.ABIERTA);
        return pqrsRepository.save(pqrs);
    }

    public void deleteById(Long id) {
        pqrsRepository.deleteById(id);
    }

    public List<Pqrs> findByUsuarioId(Long usuarioId) {
        return pqrsRepository.findByUsuarioId(usuarioId);
    }

    public List<Pqrs> findByEstado(String estado) {
        return pqrsRepository.findByEstado(estado);
    }

    public void assignToUser(Long pqrsId, Long usuarioId) {
        Optional<Pqrs> opt = pqrsRepository.findById(pqrsId);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (opt.isPresent() && usuarioOpt.isPresent()) {
            Pqrs p = opt.get();
            p.setAsignadoA(usuarioOpt.get());
            p.setEstado(EstadoPqrs.ASIGNADA);
            pqrsRepository.save(p);
        }
    }
}
