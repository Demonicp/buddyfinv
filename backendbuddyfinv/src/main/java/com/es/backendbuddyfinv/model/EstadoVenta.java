package com.es.backendbuddyfinv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "estado_ventas")
@Data
public class EstadoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_venta")
    private Long idEstadoVenta;

    @Column(name = "observacion")
    private String observacion;



    // Constructor por defecto
    public EstadoVenta() {
    }

    // Constructor con parámetros
    public EstadoVenta(String observacion) {
        this.observacion = observacion;

    }

}
