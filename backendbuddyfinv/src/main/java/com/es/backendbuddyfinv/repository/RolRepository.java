package com.es.backendbuddyfinv.repository;

import com.es.backendbuddyfinv.model.Rol;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByDescripcion(String descripcion);
}




