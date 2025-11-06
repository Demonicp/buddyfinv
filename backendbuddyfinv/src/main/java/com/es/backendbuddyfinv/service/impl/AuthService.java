package com.es.backendbuddyfinv.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.dto.AuthRequest;
import com.es.backendbuddyfinv.dto.AuthResponse;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.security.JwtUtil;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authManager, UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthRequest request) {
        // 1. Autenticar credenciales
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Obtener usuario completo
        Usuario usuario = usuarioService.findByUsuario(request.getUsername());

        // 3. Extraer datos
        String rol = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        Long idUsuario = usuario.getId();
        Long idAdministrador = usuario.getAdministrador() != null ? usuario.getAdministrador().getId() : null;

        // 4. Generar token
        String token = jwtUtil.generateToken(usuario.getUsuario(), rol, idUsuario, idAdministrador);

        // 5. Construir respuesta
        return new AuthResponse(token, rol, usuario.getUsuario(), idUsuario, idAdministrador);
    }
}