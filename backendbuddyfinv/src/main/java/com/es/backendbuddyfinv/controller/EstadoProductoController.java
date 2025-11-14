package com.es.backendbuddyfinv.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.dto.EstadoProductoDTO;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.service.EstadoProductoService;

@RestController
@RequestMapping("/estado-producto")
public class EstadoProductoController {

    @Autowired
    private EstadoProductoService estadoProductoService;

    @GetMapping("/todos")
    public ResponseEntity<List<EstadoProductoDTO>> obtenerEstados() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String rol = userDetails.getRol();

        List<EstadoProductoDTO> estados = estadoProductoService
            .getTodosSiEsAdmin(rol)
            .stream()
            .map(EstadoProductoDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(estados);
    }
}
