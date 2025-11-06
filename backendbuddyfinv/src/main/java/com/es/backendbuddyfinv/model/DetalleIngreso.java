package com.es.backendbuddyfinv.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detalle_ingreso")
@Data
public class DetalleIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_ingreso")
    private Long idDetalleIngreso;

    @ManyToOne
    @JoinColumn(name = "id_ingreso", nullable = false)
    private Ingreso ingreso;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;


    // Constructor por defecto
    public DetalleIngreso() {
    }

    // Constructor con parámetros
    public DetalleIngreso(Ingreso ingreso, Venta venta) {
        this.ingreso = ingreso;
        this.venta = venta;
    }

    // Método auxiliar para acceder al usuario desde la venta
    public Usuario getUsuario() {
        return venta != null ? venta.getUsuario() : null;
    }
}