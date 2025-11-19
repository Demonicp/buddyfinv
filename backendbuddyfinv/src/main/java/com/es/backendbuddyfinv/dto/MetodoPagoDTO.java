package com.es.backendbuddyfinv.dto;

import lombok.Data;

@Data
public class MetodoPagoDTO {
    private Integer id;
    private String nombre;

    public MetodoPagoDTO(int id2, String nombre) {
        this.id = id2;
        this.nombre = nombre;
    }
}