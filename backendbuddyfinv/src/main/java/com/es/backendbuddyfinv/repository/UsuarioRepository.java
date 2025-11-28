package com.es.backendbuddyfinv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.dto.UsuarioDTOfind;
import com.es.backendbuddyfinv.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByUsuario(String usuario);
    boolean existsByEmail(String email);
    boolean existsByUsuarioOrEmail(String usuario, String email);
    Usuario findByEmail(String email);
    //esteban moreno uso este metodo para crear ventas
    Optional<Usuario> findByUsuario(String usuario);

    List<Usuario> findByAdministradorId(Long idAdministrador);
    
    //David Solarte creó este metodo para buscar username o email
    Optional<Usuario> findByUsuarioOrEmail(String usuario, String email);
    
    boolean existsByNitUsuario(String nitUsuario);
    boolean existsByNombre(String nombre);
    boolean existsByNegocio(String negocio);

    @Query("SELECT new com.es.backendbuddyfinv.dto.UsuarioDTOfind(u.id,u.nitUsuario, u.nombre, u.email, u.usuario) " +
    "FROM Usuario u WHERE u.administrador.id = :idPropietario")
List<UsuarioDTOfind> findByPropietario(@Param("idPropietario") Long idPropietario);



}



    
