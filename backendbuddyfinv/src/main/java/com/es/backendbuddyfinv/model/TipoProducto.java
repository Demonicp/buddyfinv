package com.es.backendbuddyfinv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "tipo_productos")
@Data
public class TipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_producto")
    private Long idTipoProducto;

    
    //propietario del negocio
    @ManyToOne
    @JoinColumn(name = "id_propietario")
    @JsonIgnore // Opcional: oculta el propietario si no lo necesitás en el frontend
    private Usuario propietario;

    @Column(name = "observacion")
    private String observacion;

    

    // Constructor por defecto
    public TipoProducto() {
    }

    // Constructor con parámetros
    public TipoProducto(String observacion) {
        this.observacion = observacion;
    }

}
