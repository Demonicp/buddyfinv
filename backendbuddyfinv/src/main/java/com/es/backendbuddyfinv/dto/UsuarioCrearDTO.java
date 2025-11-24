package com.es.backendbuddyfinv.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioCrearDTO {
    @Pattern(regexp = "^[0-9]{10,15}$", message = "El NIT debe contener entre 10 y 15 dígitos numéricos")
    private String nitUsuario;
    @Size(min = 2, max = 100, message = "El nombre debe tener máximo 100 caracteres")
    private String nombre;
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email debe tener máximo 100 caracteres")
    private String email;
    @Pattern(regexp = "^[A-Za-z0-9]{3,30}$", message = "El usuario debe ser alfanumérico, max 30 caracteres")
    private String usuario;
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;
}
