package com.es.backendbuddyfinv.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.dto.UsuarioDTOfind;
import com.es.backendbuddyfinv.model.Rol;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.repository.RolRepository;
import com.es.backendbuddyfinv.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;

    public Usuario findByUsuario(String username) {
        return usuarioRepository.findByUsuario(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    // Crear un nuevo usuario
    public Usuario createUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


    // Obtener todos los usuarios
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener un usuario por ID
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Actualizar un usuario
    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            // Aquí puedes actualizar los campos específicos del usuario
            // usuario.setCampo(usuarioDetails.getCampo());
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    // Eliminar un usuario
    public boolean deleteUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verificar si existe un usuario
    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }

    public boolean existsByUsuarioOrEmail(String usuario, String email) {
        return usuarioRepository.existsByUsuarioOrEmail(usuario, email);
    }

     //verificar que exista el nombre de usuario
    public boolean existsByUsuario(String usuario){
        return usuarioRepository.existsByUsuario(usuario);
    }
    //verificar que exista el email
    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }
    //verificar que exista el nit usuario
    public boolean existsByNitUsuario(String nitUsuario){
        return usuarioRepository.findAll().stream().anyMatch(u -> u.getNitUsuario().equals(nitUsuario));
    }
    public boolean existsByNombre(String nombre){
        return usuarioRepository.existsByNombre(nombre);
    }
    public boolean existsByNegocio(String negocio){
        return usuarioRepository.existsByNegocio(negocio);
    }
    
    public Rol obtenerRolPorDefecto(){
        return rolRepository.findByDescripcion("ADMIN")
        .orElseThrow(() -> new RuntimeException("Rol por defecto o admin no encontrado"));
    }

    public List<UsuarioDTOfind> listarDTOsPorUsuario(Long idPropietario) {
        return usuarioRepository.findByPropietario(idPropietario);
    }
    

}
