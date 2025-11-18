package com.es.backendbuddyfinv.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.es.backendbuddyfinv.dto.EstadoVentaDTO;
import com.es.backendbuddyfinv.repository.EstadoVentaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoVentaService {

    private final EstadoVentaRepository repo;

    public EstadoVentaService(EstadoVentaRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<EstadoVentaDTO> listarTodos() {
        return repo.findAllAsDto(); // o repo.findAll().stream()... si no usas proyección
    }
}
