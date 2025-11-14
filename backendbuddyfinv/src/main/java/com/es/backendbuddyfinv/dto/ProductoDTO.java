package com.es.backendbuddyfinv.dto;




import com.es.backendbuddyfinv.model.Producto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //constructor vacio
public class ProductoDTO {
    private Long id;
    private String nombre;
    private double precio;
    private String tipoProducto;
    private String estadoProducto;
    private String propietario;
    private Integer cantidadDisponible; //revisar la clase producto

    // Constructor para convertir de Producto a ProductoDTO
    public ProductoDTO(com.es.backendbuddyfinv.model.Producto producto) {
        this.id = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.tipoProducto = producto.getTipoProducto().getObservacion();
        this.estadoProducto = producto.getEstadoProducto().getObservacion();
        this.propietario = producto.getPropietario().getNombre();
        // cantidadDisponible se asignará desde el controlador
    }
    
    // Constructor con cantidadDisponible
    public ProductoDTO(com.es.backendbuddyfinv.model.Producto producto, Integer cantidadDisponible) {
        this.id = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.tipoProducto = producto.getTipoProducto().getObservacion();
        this.estadoProducto = producto.getEstadoProducto().getObservacion();
        this.propietario = producto.getPropietario().getNombre();
        this.cantidadDisponible = cantidadDisponible;
    }
}
