package com.es.backendbuddyfinv.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class IngresoDTO {
    private Long idIngreso;
    private LocalDate fecha;
    private double totalDiario;
    private int totalFacturas;
    private String nombreEmpleado;
}
