package com.es.backendbuddyfinv.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;


public class CustomUserDetails extends User {

    private final Long idUsuario;

    public CustomUserDetails(com.es.backendbuddyfinv.model.Usuario usuario) {
        super(
            usuario.getUsuario(), // username
            usuario.getPassword(), // password
            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getDescripcion())) // autoridad
        );
        this.idUsuario = usuario.getId(); // ID personalizado
    }

    public Long getIdUsuario() {
        return idUsuario;
    }
}
