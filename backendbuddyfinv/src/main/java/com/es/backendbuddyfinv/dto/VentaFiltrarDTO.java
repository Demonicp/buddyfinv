package com.es.backendbuddyfinv.dto;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class VentaFiltrarDTO {
    private Long idVenta;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double totalMinimo;
    private Double totalMaximo;
    private String estadoVenta;
    private String metodoPago;

 
    
}
