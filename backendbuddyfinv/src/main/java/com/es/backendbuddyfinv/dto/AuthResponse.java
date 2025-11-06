package com.es.backendbuddyfinv.dto;

import lombok.Data;


@Data

public class AuthResponse {
    private Long idAdministrador;
    private Long idUsuario;
    private String token;
    private String rol;
    private String username;
    
    public AuthResponse(String token, String rol, String username, Long idUsuario, Long idAdministrador) {
        this.token = token;
        this.rol = rol;
        this.username = username;
        this.idUsuario = idUsuario;
        this.idAdministrador = idAdministrador;
    }
    
    
}
