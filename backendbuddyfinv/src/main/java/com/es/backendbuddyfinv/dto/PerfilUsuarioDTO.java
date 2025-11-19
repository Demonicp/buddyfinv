package com.es.backendbuddyfinv.dto;


import lombok.Data;

@Data
public class PerfilUsuarioDTO {
    private String usuario;
    private String nombre;
    private String email;
    private String rol;

    public PerfilUsuarioDTO(com.es.backendbuddyfinv.model.Usuario usuario) {
        this.usuario = usuario.getUsuario();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol().getDescripcion();
    }
}