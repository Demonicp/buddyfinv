package com.es.backendbuddyfinv.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message="El nombre del administrador es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]+$", message="El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El NIT es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El NIT solo puede contener números")
    private String nitUsuario;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    private String email;

    @NotBlank(message = "El nombre del negocio es obligatorio")
    private String negocio;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9]{8,20}$", message = "El usuario debe tener entre 8 y 20 caracteres alfanuméricos")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 30, message = "La contraseña debe tener entre 8 y 30 caracteres")
    private String password;

    @NotBlank(message = "Debe confirmar la contraseña")
    private String confirmPassword;
}
