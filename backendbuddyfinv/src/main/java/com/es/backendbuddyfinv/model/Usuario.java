package com.es.backendbuddyfinv.model;

import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nit_usuario", nullable = false, unique = true)
    private String nitUsuario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "usuario", nullable = false, unique = true)
    private String usuario;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "negocio")
    private String negocio;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
    
    @ManyToOne
    @JoinColumn(name = "id_administrador")
    private Usuario administrador; // Solo si el usuario es EMPLEADO

    @OneToMany(mappedBy = "propietario")
    private List<Egreso> egresos;

    @OneToMany(mappedBy = "propietario")
    private List<Venta> ventas;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingreso> ingresos;
}