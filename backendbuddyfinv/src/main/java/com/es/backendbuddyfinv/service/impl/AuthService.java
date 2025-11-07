package com.es.backendbuddyfinv.service.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    //Mapa temporal para controlar intentos fallidos
    private final ConcurrentHashMap<String, Integer> intentosFallidos = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> bloqueoUsuarios = new ConcurrentHashMap<>();

    public AuthService(AuthenticationManager authManager, UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthRequest request) {
        
        String username = request.getUsername();
        String password = request.getPassword();

        // Validaciones basicas de formato segun la HU
        if (username == null || !username.matches("^[a-zA-Z0-9]{8,20}$")){
            throw new IllegalArgumentException("El campo usuario debe tener entre 8 y 20 caracteres alfanumericos, sin espacios en blanco");
        }
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[^\\s]{8,30}$")){
            throw new IllegalArgumentException("El campo contraseña debe tener entre 8 y 30 caracteres, incluir una mayuscula, un numero y un caracter especial (sin espacios en blanco)");
        }

        // Verificar sil el usuario esta bloqueado
        if (bloqueoUsuarios.containsKey(username)){
            long tiempoRestante = bloqueoUsuarios.get(username) - System.currentTimeMillis();
            if (tiempoRestante > 0){
                throw new RuntimeException("limite de intentos alcanzados. Por favor intente de nuevo en: " + (tiempoRestante/1000)+ " segundos.");
            }else{
                bloqueoUsuarios.remove(username);
                intentosFallidos.remove(username);
            }
        }

        // Comprobar Existencia de usuario antes de autenticar
        Usuario usuario = usuarioService.findByUsuario(username);
        if (usuario == null){
            throw new RuntimeException("Usuario no existe. Por favor verifique o registrese");
        }

        try{
            // 1. Autenticar credenciales
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // 2. Obtener usuario completo
            //Usuario usuario = usuarioService.findByUsuario(username);
            //if (usuario == null){
            //    throw new RuntimeException("Usuario no existe. Por favor verifique o registrese");
            //} 

            // 3. Extraer datos
            String rol = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            Long idUsuario = usuario.getId();
            Long idAdministrador = usuario.getAdministrador() != null ? usuario.getAdministrador().getId() : null;

            // 4. Generar token
            String token = jwtUtil.generateToken(usuario.getUsuario(), rol, idUsuario, idAdministrador);

            // Resetear contador de intentos fallidos
            intentosFallidos.remove(username);

            // 5. Construir respuesta
            return new AuthResponse(token, rol, usuario.getUsuario(), idUsuario, idAdministrador);
        }catch (BadCredentialsException e){
            int intentos = intentosFallidos.getOrDefault(username, 0)+1;
            intentosFallidos.put(username, intentos);

            if (intentos > 3){
                bloqueoUsuarios.put(username, System.currentTimeMillis() + 30000); // 30 segundos
                throw new RuntimeException("Limite de intentos alcanzados. Por favor intente de nuevo en 30 segundos.");
            }

            throw new BadCredentialsException("Contraseña invalida. Por favor verifique e intente de nuevo");
        }
    }
}