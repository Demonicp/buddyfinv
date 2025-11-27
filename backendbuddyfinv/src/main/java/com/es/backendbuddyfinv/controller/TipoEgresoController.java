package com.es.backendbuddyfinv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.model.TipoEgreso;
import com.es.backendbuddyfinv.service.impl.TipoEgresoService;

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

