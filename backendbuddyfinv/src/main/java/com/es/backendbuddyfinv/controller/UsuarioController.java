package com.es.backendbuddyfinv.controller;

import com.es.backendbuddyfinv.dto.UsuarioDTO;

import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.service.impl.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")       
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registro de usuario
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Validar que no exista el usuario
            if (usuarioService.existsByUsuarioOrEmail(usuario.getUsuario(), usuario.getEmail())) {
                return ResponseEntity.badRequest().body("El usuario o email ya existe");
            }

            

            // Encriptar la contraseña
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // Guardar usuario
            Usuario nuevoUsuario = usuarioService.createUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar usuario: " + e.getMessage());
        }
    }

    // Obtener todos los usuarios
    @GetMapping("/all")
    public ResponseEntity<List<UsuarioDTO>> obtenerTodosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            System.out.println("Usuarios encontrados: " + usuarios.size());
            
            // Convertir a DTOs para evitar referencias circulares
            List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(usuariosDTO);
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint de prueba simple
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Backend funcionando correctamente");
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        try {
            Optional<Usuario> usuarioOptional = usuarioService.getUsuarioById(id);
            
            if (usuarioOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioOptional.get();
            
            // Actualizar campos
            if (usuarioDetails.getNombre() != null) usuario.setNombre(usuarioDetails.getNombre());
            if (usuarioDetails.getEmail() != null) usuario.setEmail(usuarioDetails.getEmail());
            if (usuarioDetails.getUsuario() != null) usuario.setUsuario(usuarioDetails.getUsuario());
            if (usuarioDetails.getPassword() != null) {
                usuario.setPassword(passwordEncoder.encode(usuarioDetails.getPassword()));
            }
            if (usuarioDetails.getNegocio() != null) usuario.setNegocio(usuarioDetails.getNegocio());
            
            

            Usuario usuarioActualizado = usuarioService.updateUsuario(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar usuario: " + e.getMessage());
        }
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            boolean eliminado = usuarioService.deleteUsuario(id);
            if (eliminado) {
                return ResponseEntity.ok().body("Usuario eliminado exitosamente");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar usuario: " + e.getMessage());
        }
    }
}
