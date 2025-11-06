package com.es.backendbuddyfinv.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data

public class VentaDetalladaDTO {
    private Long idVenta;
    private LocalDateTime fecha;
    private double total;
    private String estadoVenta;
    private String metodoPago;
    private String empleado;
    private List<DetalleProductoDTO> productos;

    public VentaDetalladaDTO(Long idVenta, LocalDateTime fecha, double total,
                             String estadoVenta, String metodoPago, String empleado,
                             List<DetalleProductoDTO> productos) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.total = total;
        this.estadoVenta = estadoVenta;
        this.metodoPago = metodoPago;
        this.empleado = empleado;
        this.productos = productos;
    }
}
