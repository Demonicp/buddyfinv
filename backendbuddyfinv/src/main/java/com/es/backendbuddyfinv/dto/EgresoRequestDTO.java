package com.es.backendbuddyfinv.dto;

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
    private String observacion; // Concepto del egreso
    private String categoria; // Categoría/tipo de egreso
    private Double costo; // Valor del egreso
    private String fecha; // Fecha en formato YYYY-MM-DD
    private Integer idMetodoPago; // ID del método de pago
    
}

