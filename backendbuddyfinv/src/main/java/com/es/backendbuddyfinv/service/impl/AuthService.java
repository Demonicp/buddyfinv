package com.es.backendbuddyfinv.service.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.dto.AuthRequest;
import com.es.backendbuddyfinv.dto.AuthResponse;
import com.es.backendbuddyfinv.dto.RegisterRequest;
import com.es.backendbuddyfinv.dto.RegisterResponse;
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

    
    

    public RegisterResponse register(RegisterRequest request){


        //clave para controlar intentos de usuario email y nit
        String key = getAttemptKey(request);

        //verificar bloqueo por intentos
        if(bloqueoUsuarios.containsKey(key)){
            long tiempoRestante = bloqueoUsuarios.get(key) - System.currentTimeMillis();
            if (tiempoRestante  > 0){
                return new RegisterResponse(false, "Límite de intentos alcanzado, por favor intente de nuevo en "+ (tiempoRestante/1000)+" segundos");
            }else{
                bloqueoUsuarios.remove(key);
                intentosFallidos.remove(key);
            }
        }


        //validaciones de formato
        if(request.getNombre() == null || !request.getNombre().matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]+$")){
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El nombre del administrador solo puede contener letras y espacios");
        }

        if (request.getNegocio() == null || !request.getNegocio().matches("^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]+$")) {
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El nombre del negocio solo puede contener letras y espacios");
        }

        if (request.getEmail() == null || !request.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El correo electrónico no tiene un formato válido");
        }

        if (request.getUsername() == null || !request.getUsername().matches("^[A-Za-z0-9]{8,20}$")) {
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El campo usuario debe tener entre 8 y 20 caracteres alfanuméricos, sin espacios en blanco.");
        }
    
        if (request.getPassword() == null || !request.getPassword().matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[^\\s]{8,30}$")) {
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El campo contraseña debe tener entre 8 y 30 caracteres, incluir una mayúscula, un número y un carácter especial (sin espacios en blanco).");
        }
    
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "Las contraseñas ingresadas no coinciden, por favor intente de nuevo");
        }



        //validar duplicados
        if(usuarioService.existsByNombre(request.getNombre())){
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El nombre ya existe");
        }
        if(usuarioService.existsByNitUsuario(request.getNitUsuario())){
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El NIT ya esta registrado");
        }
        if(usuarioService.existsByEmail(request.getEmail())){
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El correo electronico ya esta en uso");
        }
        if(usuarioService.existsByNegocio(request.getNegocio())){
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El nombre del negocio ya existe");
        }
        if(usuarioService.existsByUsuario(request.getUsername())){
            incrementarIntentoYPosibleBloqueo(key);
            return new RegisterResponse(false, "El nombre de usuario ya esta en uso"); 
        }
        
        

        //Crear usuario y guardar
        Usuario nuevo = new Usuario();
        nuevo.setNombre(request.getNombre());
        nuevo.setNitUsuario(request.getNitUsuario());
        nuevo.setNegocio(request.getNegocio());
        nuevo.setEmail(request.getEmail());
        nuevo.setUsuario(request.getUsername());
        nuevo.setPassword(request.getPassword()); // sin encriptar 
        nuevo.setRol(usuarioService.obtenerRolPorDefecto()); // Ajusta según lógica de roles 

        usuarioService.createUsuario(nuevo);

        intentosFallidos.remove(key);
        bloqueoUsuarios.remove(key);

        //TODO: aquí puedes disparar envío de correo de bienvenida usando tu servicio de email

        return new RegisterResponse(true, "Registro exitoso, ahora puede iniciar sesion");
    }


    // Helper: arma la key para conteo de intentos
    private String getAttemptKey(RegisterRequest r) {
        if (r.getUsername() != null && !r.getUsername().isBlank()) return "reg_user:" + r.getUsername();
        if (r.getEmail() != null && !r.getEmail().isBlank()) return "reg_email:" + r.getEmail();
        if (r.getNitUsuario() != null && !r.getNitUsuario().isBlank()) return "reg_nit:" + r.getNitUsuario();
        return "reg_anon";
    }

    // Helper: incrementa intentos y bloquea si supera 3
    private void incrementarIntentoYPosibleBloqueo(String key) {
        int intentos = intentosFallidos.getOrDefault(key, 0) + 1;
        intentosFallidos.put(key, intentos);
        if (intentos >= 3) {
            bloqueoUsuarios.put(key, System.currentTimeMillis() + 30_000L); // 30s
        }
    }
    

}