package com.es.backendbuddyfinv.dto;

import com.es.backendbuddyfinv.model.EstadoProducto;
import lombok.Data;

@Data
public class EstadoProductoDTO {
    private Long idEstadoPro;
    private String observacion;

    public EstadoProductoDTO(EstadoProducto estado) {
        this.idEstadoPro = estado.getIdEstadoPro();
        this.observacion = estado.getObservacion();
    }
}
