package com.es.backendbuddyfinv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.security.core.Authentication;


//import com.es.backendbuddyfinv.repository.ProductoRepository;

import com.es.backendbuddyfinv.repository.InventarioRepository;
import com.es.backendbuddyfinv.service.impl.ProductoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

import com.es.backendbuddyfinv.model.TipoEgreso;
import com.es.backendbuddyfinv.service.impl.TipoEgresoService;
@CrossOrigin(origins="http://localhost:5173")
@RestController
@RequestMapping("/tipo-egresos")
public class TipoEgresoController {

    @Autowired
    private TipoEgresoService tipoEgresoService;

    @GetMapping
    public ResponseEntity<List<TipoEgreso>> listarTiposEgreso() {
        List<TipoEgreso> tipos = tipoEgresoService.getAllTipoEgresos();
        return ResponseEntity.ok(tipos);
    }
}

