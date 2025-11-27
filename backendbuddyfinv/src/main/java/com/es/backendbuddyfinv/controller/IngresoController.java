package com.es.backendbuddyfinv.controller;

import com.es.backendbuddyfinv.dto.IngresoDTO;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.service.impl.IngresoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingresos")
@CrossOrigin(origins = "*")
public class IngresoController {

    @Autowired
    private IngresoService ingresoService;

    // ============================
    //   LISTAR INGRESOS DEL USUARIO
    // ============================
    @GetMapping("/mis-ingresos")
    public ResponseEntity<List<IngresoDTO>> obtenerIngresosDelUsuario() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long idUsuario = userDetails.getIdUsuario();
        String rol = userDetails.getRol();

        List<IngresoDTO> ingresos = ingresoService.listarDTO(idUsuario, rol);

        return ResponseEntity.ok(ingresos);
    }

    // ============================
    //   FILTRAR POR RANGO DE FECHAS
    // ============================
    @GetMapping("/filtrar")
    public ResponseEntity<List<IngresoDTO>> filtrarPorFechas(
            @RequestParam String inicio,
            @RequestParam String fin) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long idUsuario = userDetails.getIdUsuario();
        String rol = userDetails.getRol();

        List<IngresoDTO> ingresos = ingresoService.filtrarPorRango(idUsuario, rol, inicio, fin);

        return ResponseEntity.ok(ingresos);
    }
}