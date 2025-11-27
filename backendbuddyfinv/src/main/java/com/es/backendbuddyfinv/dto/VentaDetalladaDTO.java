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
    // AGREGADO: identificador del usuario que realizó la venta
    private Long empleadoId;
    private String cliente;
    private List<DetalleProductoDTO> productos;

    public VentaDetalladaDTO(Long idVenta, LocalDateTime fecha, double total,
                             String estadoVenta, String metodoPago, String empleado, Long empleadoId, String cliente,
                             List<DetalleProductoDTO> productos) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.total = total;
        this.estadoVenta = estadoVenta;
        this.metodoPago = metodoPago;
        this.empleado = empleado;
        // AGREGADO: asignar empleadoId (id del usuario que realizó la venta)
        this.empleadoId = empleadoId;
        this.cliente = cliente;
        this.productos = productos;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}
