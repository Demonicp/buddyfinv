package com.es.backendbuddyfinv.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.repository.UsuarioRepository;
import com.es.backendbuddyfinv.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new CustomUserDetails(usuario);

    }
}