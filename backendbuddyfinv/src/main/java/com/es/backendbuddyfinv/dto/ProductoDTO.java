package com.es.backendbuddyfinv.dto;



import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private double precio;
    private String tipoProducto;
    private String estadoProducto;
    private String propietario;

    // Constructor para convertir de Producto a ProductoDTO
    public ProductoDTO(com.es.backendbuddyfinv.model.Producto producto) {
        this.id = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.tipoProducto = producto.getTipoProducto().getObservacion();
        this.estadoProducto = producto.getEstadoProducto().getObservacion();
        this.propietario = producto.getPropietario().getNombre();
    }
}
