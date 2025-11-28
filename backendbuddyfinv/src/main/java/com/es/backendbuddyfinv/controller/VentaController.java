package com.es.backendbuddyfinv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.dto.VentaCrearDTO;
import com.es.backendbuddyfinv.dto.VentaDetalladaDTO;
import com.es.backendbuddyfinv.dto.VentaResponseDTO;
import com.es.backendbuddyfinv.model.Venta;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.service.impl.VentaService;




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
    
       
    
        // NOTE: `listarVentasDetalladas` now includes `empleadoId` in the returned DTOs
        List<VentaDetalladaDTO> ventas = ventaService.listarVentasDetalladas(idPropietario);
        return ResponseEntity.ok(ventas);
    }

    @PostMapping("/crearVenta")
    public ResponseEntity<?> crearVenta(@RequestBody VentaCrearDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Venta ventaGuardada = ventaService.registrarVenta(dto, authentication);
            VentaResponseDTO response = ventaService.toResponse(ventaGuardada);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            // loguea la excepción para debugging (usa el logger de la clase)
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear la venta");
        }
    }

    /*@GetMapping("/filtrar")
        public ResponseEntity<List<VentaDetalladaDTO>> filtrarVentas(
        @RequestParam(required = false) Long idVenta,
        @RequestParam(required = false) String fechaDesde,
        @RequestParam(required = false) String fechaHasta,
        @RequestParam(required = false) Double totalMin,
        @RequestParam(required = false) Double totalMax,
        @RequestParam(required = false) String metodoPago
        //porque añadir clientes y empleados si no lo piden en la hu

) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long idPropietario = userDetails.getIdUsuario();

    List<VentaDetalladaDTO> ventas = ventaService.filtrarVentas(
        idPropietario,
        idVenta,
        fechaDesde,
        fechaHasta,
        totalMin,
        totalMax,
        metodoPago
        
    );

    return ResponseEntity.ok(ventas);
}
*/

    

}
