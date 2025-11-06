package com.es.backendbuddyfinv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.dto.VentaDetalladaDTO;
import com.es.backendbuddyfinv.model.Venta;
import com.es.backendbuddyfinv.security.CustomUserDetails;

import com.es.backendbuddyfinv.service.impl.VentaService;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/ventas")
public class VentaController {
    
    @Autowired
    private VentaService ventaService;

    
    @GetMapping("/detalladas")
    public ResponseEntity<List<VentaDetalladaDTO>> obtenerVentasDetalladas() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long idPropietario = userDetails.getIdUsuario();
    
       
    
        List<VentaDetalladaDTO> ventas = ventaService.listarVentasDetalladas(idPropietario);
        return ResponseEntity.ok(ventas);
    }



        


    



    @PostMapping("/crearVenta")
    public Venta crearVenta(@RequestBody Venta venta) {
        return ventaService.createVenta(venta);
    }

    @GetMapping("/all")
    public List<Venta> getAllVentas() {
        return ventaService.getAllVentas();
    }

    @GetMapping("/{id}")
    public String getMethodName() {
        return new String();
    }
    
    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    
    


    

}
