package com.es.backendbuddyfinv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    // Constructor por defecto
    public Rol() {
    }

    // Constructor con parámetros
    public Rol(String descripcion) {
        this.descripcion = descripcion;
    }

}
