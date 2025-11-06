package com.es.backendbuddyfinv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "inventarios")
@Data
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long idInventario;

    @Column(name = "cantidad_disponible", nullable = false)
    private int cantidadDisponible;

    // Relación con Producto
    @OneToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    //propietario del negocio
    @ManyToOne
    @JoinColumn(name = "id_propietario")
    private Usuario propietario;

    // Constructor por defecto
    public Inventario() {
    }

    // Constructor con parámetros
    public Inventario(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

}
