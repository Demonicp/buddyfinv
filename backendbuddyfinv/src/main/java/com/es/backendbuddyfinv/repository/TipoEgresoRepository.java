package com.es.backendbuddyfinv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.TipoEgreso;

@Repository
public interface TipoEgresoRepository extends JpaRepository<TipoEgreso, Long> {
    /**
     * Busca un tipo de egreso por su descripción (case-insensitive)
     * Este método es usado para verificar si una categoría ya existe antes de crearla
     * 
     * @param descripcion Descripción del tipo de egreso (ej: "Gastos administrativos")
     * @return Optional con el TipoEgreso si existe, vacío si no se encuentra
     */
    @Query("SELECT t FROM TipoEgreso t WHERE LOWER(TRIM(t.descripcion)) = LOWER(TRIM(:descripcion))")
    Optional<TipoEgreso> findByDescripcion(@Param("descripcion") String descripcion);
}

