package com.es.backendbuddyfinv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "detalle_inventario")
@Data
public class DetalleInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_inventario")
    private Long idDetalleInventario;

    @Column(name = "cantidad_disponible", nullable = false)
    private int cantidadDisponible;

    // Relación con Producto
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Relación con Inventario
    @ManyToOne
    @JoinColumn(name = "id_inventario", nullable = false)
    private Inventario inventario;

    // Propietario del negocio (opcional)
    @ManyToOne
    @JoinColumn(name = "id_propietario")
    private Usuario propietario;

    // Constructor por defecto
    public DetalleInventario() {
    }

    // Constructor con parámetros
    public DetalleInventario(int cantidadDisponible, Producto producto, Inventario inventario) {
        this.cantidadDisponible = cantidadDisponible;
        this.producto = producto;
        this.inventario = inventario;
    }

}

