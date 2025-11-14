package com.es.backendbuddyfinv.dto;

import com.es.backendbuddyfinv.model.TipoProducto;
import lombok.Data;

@Data
public class TipoProductoDTO {
    private Long idTipoProducto;
    private String observacion;

    public TipoProductoDTO(TipoProducto tipo) {
        this.idTipoProducto = tipo.getIdTipoProducto();
        this.observacion = tipo.getObservacion();
    }

}
