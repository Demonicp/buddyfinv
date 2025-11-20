package com.es.backendbuddyfinv.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.model.MetodoPago;
import com.es.backendbuddyfinv.dto.MetodoPagoDTO;
import com.es.backendbuddyfinv.service.impl.MetodoPagoService;

@RestController
@RequestMapping("/MetodoPago")

public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    @GetMapping
    public ResponseEntity<List<MetodoPagoDTO>> listarMetodosPago() {
        List<MetodoPago> entidades = metodoPagoService.getAllMetodosPago();
        List<MetodoPagoDTO> dtos = entidades.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private MetodoPagoDTO mapToDto(MetodoPago m) {
        if (m == null) return null;
        Integer id = m.getIdMetodoPago();
        return new MetodoPagoDTO(id, m.getDescripcion());
    }
}