package com.example.budgetmap.service;

import com.example.budgetmap.model.Evento;
import com.example.budgetmap.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> findById(Long id) {
        return eventoRepository.findById(id);
    }

    public Evento save(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void deleteById(Long id) {
        eventoRepository.deleteById(id);
    }

    public List<Evento> findByLugarId(Long lugarId) {
        return eventoRepository.findByLugarId(lugarId);
    }

    public List<Evento> findNextEvents(int limit) {
        LocalDateTime now = LocalDateTime.now();
        return eventoRepository.findByFechaInicioAfter(now).stream().limit(limit).toList();
    }
}
