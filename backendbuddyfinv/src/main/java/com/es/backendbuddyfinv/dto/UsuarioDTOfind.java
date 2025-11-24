package com.es.backendbuddyfinv.dto;

import lombok.Data;

@Data
public class UsuarioDTOfind {
    private Long id;
    private String nitUsuario;
    private String nombre;
    private String email;
    private String usuario;
    // Constructor para convertir de Usuario a UsuarioDTO
    public UsuarioDTOfind(Long id,String nitUsuario, String nombre, String email, String usuario) {
        this.nitUsuario = nitUsuario;
        this.nombre = nombre;
        this.email = email;
        this.usuario = usuario;
        this.id= id;
    }
    
}


