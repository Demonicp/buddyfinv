package com.es.backendbuddyfinv.model;

import java.time.LocalDate;

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
@Table(name = "egresos")
@Data



public class Egreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_egreso")
    private Long idEgreso;

    @Column(name = "costo", nullable = false)
    private Double costo;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "fecha")
    private LocalDate fecha;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "id_tipo_egreso", nullable = false)
    private TipoEgreso tipoEgreso;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;


    //propietario del negocio
    @ManyToOne
    @JoinColumn(name = "id_propietario",nullable=false)
    private Usuario propietario;

    // Constructor por defecto
    public Egreso() {
    }

    // Constructor con parámetros
    public Egreso(double costo, String observacion,TipoEgreso tipoEgreso,MetodoPago metodoPago,Usuario propietario) {
        this.costo = costo;
        this.observacion = observacion;
        this.tipoEgreso = tipoEgreso;
        this.metodoPago = metodoPago;
        this.propietario = propietario;
        this.fecha = LocalDate.now(); // Asignar fecha actual por defecto
    }
    
    // Constructor con fecha explícita
    public Egreso(double costo, String observacion, LocalDate fecha, TipoEgreso tipoEgreso, MetodoPago metodoPago, Usuario propietario) {
        this.costo = costo;
        this.observacion = observacion;
        this.fecha = fecha;
        this.tipoEgreso = tipoEgreso;
        this.metodoPago = metodoPago;
        this.propietario = propietario;
    }

}
