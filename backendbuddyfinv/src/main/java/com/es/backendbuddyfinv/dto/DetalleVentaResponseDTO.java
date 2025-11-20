package com.es.backendbuddyfinv.dto;

import lombok.Data;



@Data

public class DetalleVentaResponseDTO {
        private Long idDetalleVenta;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    // getters / setters by lombok

    
}
