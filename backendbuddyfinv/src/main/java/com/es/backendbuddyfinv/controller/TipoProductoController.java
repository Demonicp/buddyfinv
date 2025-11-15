package com.es.backendbuddyfinv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import com.es.backendbuddyfinv.dto.TipoProductoDTO;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.service.impl.TipoProductoService;

@RestController
@RequestMapping("/tipo-producto")
public class TipoProductoController {

    @Autowired
    private TipoProductoService tipoProductoService;

    @GetMapping("/mis-tipos")
    public ResponseEntity<List<TipoProductoDTO>> obtenerTiposDelUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long idUsuario = userDetails.getIdUsuario();
        String rol = userDetails.getRol();

        List<TipoProductoDTO> tipos = tipoProductoService
            .getTiposVisiblesPorUsuario(idUsuario, rol)
            .stream()
            .map(TipoProductoDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(tipos);
    }
}