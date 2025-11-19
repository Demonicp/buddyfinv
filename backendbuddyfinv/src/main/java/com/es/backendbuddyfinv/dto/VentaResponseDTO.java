package com.es.backendbuddyfinv.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class VentaResponseDTO {
        private Long idVenta;
    private String cliente;
    private LocalDateTime fecha;
    private Double total;
    private List<DetalleVentaResponseDTO> detalles;
    // getters / setters by lombok

    
}
