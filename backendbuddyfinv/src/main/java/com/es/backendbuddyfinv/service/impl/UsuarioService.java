package com.es.backendbuddyfinv.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import com.es.backendbuddyfinv.model.Usuario;

import com.es.backendbuddyfinv.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    

}
