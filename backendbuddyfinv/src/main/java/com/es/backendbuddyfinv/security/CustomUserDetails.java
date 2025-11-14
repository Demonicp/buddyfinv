package com.es.backendbuddyfinv.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;


public class CustomUserDetails extends User {


    private final Long idUsuario;
    private final String rol;
    private final Long idAdministrador;

    public CustomUserDetails(com.es.backendbuddyfinv.model.Usuario usuario) {
        super(
            usuario.getUsuario(), // username
            usuario.getPassword(), // password
            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getDescripcion())) // autoridad
        );
        this.idUsuario = usuario.getId(); // ID personalizado
        this.rol = usuario.getRol().getDescripcion();
        this.idAdministrador = usuario.getAdministrador() != null ? usuario.getAdministrador().getId() : null;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getRol() {
        return rol;
    }

    public Long getIdAdministrador() {
        return idAdministrador;
    }
}

