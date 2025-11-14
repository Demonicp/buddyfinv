package com.es.backendbuddyfinv.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para recibir los datos del formulario de egreso desde el frontend
 * Este objeto se usa en el endpoint POST /Egresos/agregarEgreso
 * 
 * Campos:
 * - observacion: Concepto o descripción del egreso (ej: "Compra de materiales")
 * - categoria: Categoría o tipo de egreso (ej: "Gastos administrativos")
 *   Si la categoría no existe, se crea automáticamente en el backend
 * - costo: Valor monetario del egreso (ej: 25000.0)
 * - fecha: Fecha del egreso en formato String YYYY-MM-DD (ej: "2024-01-15")
 * - idMetodoPago: ID del método de pago (1=Efectivo, 2=Transferencia, 3=Tarjeta)
 */
@Data
public class EgresoRequestDTO {
    
    @Size(max = 300, message = "Las observaciones no pueden superar los 300 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,áéíóúÁÉÍÓÚñÑ-]*$", message = "Las observaciones solo pueden contener caracteres alfanuméricos")
    private String observacion; // Concepto del egreso
    
    private String categoria; // Categoría/tipo de egreso
    
    @NotNull(message = "El costo no puede estar vacío")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo debe ser mayor que cero")
    private Double costo; // Valor del egreso
    
    private String fecha; // Fecha en formato YYYY-MM-DD
    private Integer idMetodoPago; // ID del método de pago
    
}

