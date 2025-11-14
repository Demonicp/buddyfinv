package com.es.backendbuddyfinv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "estado_productos")
@Data
public class EstadoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_pro")
    private Long idEstadoPro;

    @Column(name = "observacion")
    private String observacion;

    // Constructor por defecto
    public EstadoProducto() {
    }

    // Constructor con parámetros
    public EstadoProducto(String observacion) {
        this.observacion = observacion;
    }

}
