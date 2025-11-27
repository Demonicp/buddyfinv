package com.es.backendbuddyfinv.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.dto.UsuarioCrearDTO;
import com.es.backendbuddyfinv.dto.UsuarioDTOfind;
import com.es.backendbuddyfinv.model.Rol;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.repository.RolRepository;
import com.es.backendbuddyfinv.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;

    UsuarioService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario findByUsuario(String username) {
        return usuarioRepository.findByUsuario(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    //David Solarte crea el metodo para buscar por usuario o email
    public Optional<Usuario> findByUsuarioOrEmail(String usuarioOrEmail){
        return usuarioRepository.findByUsuarioOrEmail(usuarioOrEmail, usuarioOrEmail);
    }
    //David Solarte crea el metodo para actualizar password(hasheada)
    public void actualizarPassword(Usuario usuario, String nuevaPassword){
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
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

    public Usuario crearEmpleado(Long idAdmin, UsuarioCrearDTO dto){
        if(usuarioRepository.existsByNitUsuario(dto.getNitUsuario())){
            throw new RuntimeException("El NIT que desea registrar ya pertenece a un usuario.");
        }

        if(usuarioRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("El email que desea registrar ya esta asociado a un usuario.");
        }

        if(usuarioRepository.existsByUsuario(dto.getUsuario())){
            throw new RuntimeException("El nombre de usurio que desea registrar no esta disponible.");
        }
        
        Usuario admin = usuarioRepository.findById(idAdmin)
        .orElseThrow(() -> new RuntimeException("administrador no encontrado"));

        Rol rolEmpleado = rolRepository.findByDescripcion("EMPLEADO")
        .orElseThrow(() -> new RuntimeException("Rol EMPLEADO no encrontrado"));

        Usuario empleado = new Usuario();
        empleado.setNitUsuario(dto.getNitUsuario());
        empleado.setNombre(dto.getNombre());
        empleado.setEmail(dto.getEmail());
        empleado.setUsuario(dto.getUsuario());
        empleado.setPassword(dto.getPassword());
        empleado.setNegocio(admin.getNegocio());
        empleado.setRol(rolEmpleado);
        empleado.setAdministrador(admin);

        return usuarioRepository.save(empleado);
    }

    public List<Usuario> listarEmpleadosPorAdmin(Long idAdmin){
        return usuarioRepository.findByAdministradorId(idAdmin);
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
