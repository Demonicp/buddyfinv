package com.es.backendbuddyfinv.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * DTO ligero para autocomplete / selectores de producto.
 * Diseñado para no colisionar con otros DTOs del proyecto.
 */

 @Data
public class ProductoSelectorDTO implements Serializable {

    private Long id;
    private String nombre;
    private Integer cantidadDisponible;
    private Double precio;

    public ProductoSelectorDTO() {}

    public ProductoSelectorDTO(Long id, String nombre, Integer cantidadDisponible, Double precio) {
        this.id = id;
        this.nombre = nombre;

        this.cantidadDisponible = cantidadDisponible;
        this.precio = precio;
    }

    // en com.es.backendbuddyfinv.dto.ProductoMapper o dentro del service
    public static ProductoSelectorDTO toSelectorDto(com.es.backendbuddyfinv.model.Producto p, Integer cantidadDisponible) {
        if (p == null) return null;
        Long id = p.getIdProducto(); // adapta si tu getter tiene otro nombre
        String nombre = p.getNombre();
        Double precio = p.getPrecio();
        return new ProductoSelectorDTO(id, nombre, cantidadDisponible, precio);
}
}

