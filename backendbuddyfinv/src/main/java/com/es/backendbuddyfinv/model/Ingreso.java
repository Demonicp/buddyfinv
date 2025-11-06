package com.es.backendbuddyfinv.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ingresos")
@Data
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingreso")
    private Long idIngreso;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "total_diario", nullable = false)
    private double totalDiario;

    @Column(name = "total_facturas",nullable = false)
    private int totalFacturas;




    //propietario del negocio
    @ManyToOne
    @JoinColumn(name = "id_propietario")
    private Usuario propietario;

    

    // Constructor por defecto
    public Ingreso() {
    }
        // Constructor con parámetros
    public Ingreso(LocalDate fecha, double totalDiario, int totalFacturas) {
        this.fecha = fecha;
        this.totalDiario = totalDiario;
        this.totalFacturas = totalFacturas;
    }

    
    
    @OneToMany(mappedBy = "ingreso")
    private List<DetalleIngreso> detalleIngresos;

}
