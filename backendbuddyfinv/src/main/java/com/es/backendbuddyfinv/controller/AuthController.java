package com.es.backendbuddyfinv.controller;

import com.es.backendbuddyfinv.dto.AuthRequest;
import com.es.backendbuddyfinv.dto.AuthResponse;

import com.es.backendbuddyfinv.service.impl.AuthService;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try{
            //System.out.println("Recibido username=" + request.getUsername() + ", password=" + request.getPassword());
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            // Errores de formato en usuario o contraseña
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }catch(org.springframework.security.authentication.BadCredentialsException e){
            // Contraseña incorrecta
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

        }catch (RuntimeException e){
            // Usuario no existe o bloqueo por intentos
            if (e.getMessage().contains("Limite de intentos")){
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            // Error generico del servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Esrror interno del servidor");
        }
    }
}