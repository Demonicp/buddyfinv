package com.es.backendbuddyfinv.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EgresoDTO {
    private Long id;
    private Double costo;
    
    @Size(max = 300, message = "Las observaciones no pueden superar los 300 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,áéíóúÁÉÍÓÚñÑ-]*$", message = "Las observaciones solo pueden contener caracteres alfanuméricos")
    private String observacion;
    
    private String fecha;
    private Long idMetodoPago;
    private String descripcionMetodoPago;
    private Long idTipoEgreso;
    private String descripcionTipoEgreso;
    private Long idPropietario;

    public EgresoDTO(com.es.backendbuddyfinv.model.Egreso egreso) {
        this.id = egreso.getIdEgreso();
        this.costo = egreso.getCosto();
        this.observacion = egreso.getObservacion();
        this.fecha = egreso.getFecha() != null ? egreso.getFecha().toString() : null;

        if (egreso.getMetodoPago() != null) {
            this.idMetodoPago = (long) egreso.getMetodoPago().getIdMetodoPago();
            this.descripcionMetodoPago = egreso.getMetodoPago().getDescripcion();
        }

        if (egreso.getTipoEgreso() != null) {
            this.idTipoEgreso = egreso.getTipoEgreso().getIdTipoEgreso();
            this.descripcionTipoEgreso = egreso.getTipoEgreso().getDescripcion();
        }

        if (egreso.getPropietario() != null) {
            this.idPropietario = egreso.getPropietario().getId(); // ajusta si el campo se llama distinto
        }
    }
}