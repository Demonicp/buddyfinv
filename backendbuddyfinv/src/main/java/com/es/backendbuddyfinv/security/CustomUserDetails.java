package com.es.backendbuddyfinv.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Collection;


public class CustomUserDetails extends User {

    private final Long idUsuario;
    private final String rol;
    private final Long idAdministrador;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.idUsuario = null;
        this.rol = null;
        this.idAdministrador = null;
    }

    public CustomUserDetails(com.es.backendbuddyfinv.model.Usuario usuario) {
        super(
            usuario.getUsuario(),
            usuario.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getDescripcion()))
        );
        this.idUsuario = usuario.getId();
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
