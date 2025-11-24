package com.es.backendbuddyfinv.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.es.backendbuddyfinv.model.DetalleInventario;


@Data
@NoArgsConstructor

public class ProductoReabastecerDTO {
    private Long idProducto;
    private String nombre;
    private double precio;
    private Long idEstadoProducto;
    private int cantidad; 

    public ProductoReabastecerDTO(com.es.backendbuddyfinv.model.Producto producto) {
        this.idProducto = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.idEstadoProducto = producto.getEstadoProducto().getIdEstadoPro();
        this.cantidad=producto.getDetalleInventarios().stream()
                        .mapToInt(DetalleInventario::getCantidadDisponible)
                        .sum();

                        //para obtener la cantidad total disponible del producto en todos los inventarios
                        //para 

    }
}



