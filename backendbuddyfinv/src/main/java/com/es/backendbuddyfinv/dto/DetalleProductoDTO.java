package com.es.backendbuddyfinv.dto;

import lombok.Data;

@Data

public class DetalleProductoDTO {
    private String nombreProducto;
    private int cantidad;
    private double subtotal;
    private String estadoProducto;
    private double precioUnitario;

    public DetalleProductoDTO(String nombreProducto, int cantidad, double subtotal, String estadoProducto, double precioUnitario) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.estadoProducto = estadoProducto;
        this.precioUnitario = precioUnitario;
    }
}