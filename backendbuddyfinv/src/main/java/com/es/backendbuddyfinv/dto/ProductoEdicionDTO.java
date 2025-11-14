package com.es.backendbuddyfinv.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoEdicionDTO {
    private Long idProducto;
    private String nombre;
    private double precio;
    private Long idTipoProducto;
    private Long idEstadoProducto;

    public ProductoEdicionDTO(com.es.backendbuddyfinv.model.Producto producto) {
        this.idProducto = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.idTipoProducto = producto.getTipoProducto().getIdTipoProducto();
        this.idEstadoProducto = producto.getEstadoProducto().getIdEstadoPro();
    }
}
