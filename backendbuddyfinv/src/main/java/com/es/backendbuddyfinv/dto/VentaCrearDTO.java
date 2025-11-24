package com.es.backendbuddyfinv.dto;

import java.util.List;
import com.es.backendbuddyfinv.dto.DetalleVentaCrearDTO;

import lombok.Data;


@Data
public class VentaCrearDTO {
    private String cliente;
    private Integer metodoPagoId;
    private Long estadoVentaId;
    private List<DetalleVentaCrearDTO> detalles;
    // getters y setters por lombok
}
