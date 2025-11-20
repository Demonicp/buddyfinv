package com.es.backendbuddyfinv.controller;



import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.dto.EstadoVentaDTO;
import com.es.backendbuddyfinv.service.impl.EstadoVentaService;

@RestController
@RequestMapping("/EstadoVenta")

public class EstadoVentaController {

    private final EstadoVentaService estadoVentaService;

    public EstadoVentaController(EstadoVentaService estadoVentaService) {
        this.estadoVentaService = estadoVentaService;
    }

    @GetMapping("/estados")
    public ResponseEntity<List<EstadoVentaDTO>> listarEstadosVenta() {
        List<EstadoVentaDTO> lista = estadoVentaService.listarTodos();
        return ResponseEntity.ok(lista);
    }
}
