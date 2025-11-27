package com.es.backendbuddyfinv.dto;

import lombok.Data;

@Data

public class DetalleProductoDTO {
    private Long productoId;
    private String nombreProducto;
    private int cantidad;
    private double subtotal;
    private String estadoProducto;
    private double precioUnitario;

    public DetalleProductoDTO(Long productoId, String nombreProducto, int cantidad, double subtotal, String estadoProducto, double precioUnitario) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.estadoProducto = estadoProducto;
        this.precioUnitario = precioUnitario;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
}