package com.es.backendbuddyfinv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.model.MetodoPago;
import com.es.backendbuddyfinv.service.impl.MetodoPagoService;

@RestController
@RequestMapping("/metodo-pagos") // mejor en minúsculas y plural
public class MetodoDpagoController {
    @Autowired
    private MetodoPagoService metodoService;

    @GetMapping
    public ResponseEntity<List<MetodoPago>> listarMetodosPago() {
        List<MetodoPago> metodos = metodoService.getAllMetodosPago();
        return ResponseEntity.ok(metodos);
    }
}


    

