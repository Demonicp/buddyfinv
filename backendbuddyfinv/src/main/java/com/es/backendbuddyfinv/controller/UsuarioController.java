package com.es.backendbuddyfinv.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.es.backendbuddyfinv.dto.UsuarioDTO;
import com.es.backendbuddyfinv.dto.PerfilUsuarioDTO;
import com.es.backendbuddyfinv.dto.UsuarioCrearDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import com.es.backendbuddyfinv.dto.UsuarioDTO;
import com.es.backendbuddyfinv.dto.UsuarioDTOfind;
import com.es.backendbuddyfinv.dto.UsuarioEdicionDTO;
import com.es.backendbuddyfinv.dto.UsuarioResponseDTO;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.security.JwtUtil;
import com.es.backendbuddyfinv.service.impl.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")       
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    //Obtener id Admin
    private Long obtenerAdministradorDesdeToken(String authHeader){
        String token = authHeader.replace("Bearer", "");
        return jwtUtil.getIdAdministrador(token);
    }

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

    //tuve que crear el DTOresponse para usuario ya que estaba anidando y generaba el error de json /BY ESTEBAN MORENO
    @PostMapping(value = "/agregar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> crearEmpleado(
        @Valid @RequestBody UsuarioCrearDTO dto,
        @RequestHeader("Authorization") String authHeader) {

        long idAdmin = obtenerAdministradorDesdeToken(authHeader);
        Usuario nuevoEmpleado = usuarioService.crearEmpleado(idAdmin, dto);

        UsuarioResponseDTO resp = UsuarioResponseDTO.fromEntity(nuevoEmpleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<UsuarioDTO>> listarEmpleados(@RequestHeader("Authorization") String authHeader){
        Long adminId = obtenerAdministradorDesdeToken(authHeader);
        List<Usuario> empleados = usuarioService.listarEmpleadosPorAdmin(adminId);
        List<UsuarioDTO> dto = empleados.stream().map(UsuarioDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    // Actualizar usuario
    @PutMapping("update/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                            @Valid @RequestBody UsuarioEdicionDTO dto) {
        try {
            if (!usuarioService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            Usuario actualizado = usuarioService.updateUsuario(id, dto);

            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Aquí usas tu DTO de respuesta
            UsuarioResponseDTO resp = UsuarioResponseDTO.fromEntity(actualizado);
            return ResponseEntity.ok(resp);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al actualizar usuario: " + e.getMessage());
        }
    }

    // Eliminar usuario
    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarUsuario(@RequestParam Long idUsuario) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            boolean eliminado = usuarioService.deleteUsuario(idUsuario);
            if (eliminado) {
                return ResponseEntity.ok("Usuario eliminado exitosamente");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar usuario: " + e.getMessage());
        }
    }


    //no borrar funcion de juan david
  //esto es de juan david no borrar
    @GetMapping("/allUsersByPropietario")
    public ResponseEntity<List<UsuarioDTOfind>> obtenerEgresosDetallados() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long idPropietario = userDetails.getIdUsuario();

        List<UsuarioDTOfind> usuarios = usuarioService.listarDTOsPorUsuario(idPropietario);
        return ResponseEntity.ok(usuarios);
    }











/////////////////SANTIAGO MONTENEGRO MOSTRAR PERFIL INICIO

@GetMapping("/perfil")
public ResponseEntity<PerfilUsuarioDTO> obtenerPerfilUsuarioAutenticado() {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioService.findByUsuario(username);
        PerfilUsuarioDTO dto = new PerfilUsuarioDTO(usuario);

        return ResponseEntity.ok(dto);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}


/////////////////SANTIAGO MONTENEGRO MOSTRAR PERFIL INICIO FIN
}
