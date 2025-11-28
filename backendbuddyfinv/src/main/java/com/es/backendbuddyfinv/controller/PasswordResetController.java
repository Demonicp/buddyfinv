package com.es.backendbuddyfinv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.es.backendbuddyfinv.dto.PasswordResetRequest;
import com.es.backendbuddyfinv.dto.PasswordResetUpdateRequest;
import com.es.backendbuddyfinv.dto.PasswordResetVerifyRequest;
import com.es.backendbuddyfinv.service.impl.PasswordResetService;
import com.es.backendbuddyfinv.service.impl.UsuarioService;

@RestController
@RequestMapping("/auth/password")
public class PasswordResetController {
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @Autowired
    private UsuarioService usuarioService;

    /**
     * Solicitar envío de código. Respuesta siempre genérica.
     */
    @PostMapping("/request")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequest request){
        passwordResetService.requestReset(request.getUsernameOrEmail());
        return ResponseEntity.ok().body( "Si existe una cuenta asociada, se enviará un código al correo registrado.");
    }
    /**
     * Verificar código
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody PasswordResetVerifyRequest request){
        try{
            passwordResetService.verifyCode(request.getUsernameOrEmail(), request.getCode());
            return ResponseEntity.ok().body("Código verificado correctamente.");
        }catch(RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Actualizar contraseña (solo si se verificó el código)
     */
    @PostMapping("/update")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordResetUpdateRequest request){
        //validaciones basicas
        String np= request.getNewPassword();
        String cp= request.getConfirmPassword();
        if (np == null || cp == null || !np.equals(cp)) {
            return ResponseEntity.badRequest().body("Las contraseñas no son iguales.");
        }

        // validación de reglas mínimas (8-30, mayúscula, número, especial, sin espacios)
        if (!np.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,30}$") || np.contains(" ")) {
            return ResponseEntity.badRequest().body("La contraseña no cumple las reglas de seguridad.");
        }
        try{
            passwordResetService.updatePassword(request.getUsernameOrEmail(), np, usuarioService);
            return ResponseEntity.ok().body("Contraseña cambiada exitosamente.");
        }catch(RuntimeException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
