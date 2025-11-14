package com.es.backendbuddyfinv.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoCrearDTO {
    private String nombre;
    private double precio;
    Long  tipoProductoId; // Puede ser el nombre o el código
    Long estadoProductoId;
    Integer cantidadDisponible; // Cantidad inicial en inventario

}




