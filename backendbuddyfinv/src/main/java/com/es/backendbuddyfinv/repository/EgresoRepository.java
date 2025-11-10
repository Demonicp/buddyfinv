package com.es.backendbuddyfinv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.Egreso;

@Repository
public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    @Query("SELECT DISTINCT e FROM Egreso e " +
    "WHERE e.propietario.id = :idPropietario")
    List<Egreso> findByPropietario(@Param("idPropietario") Long idPropietario);



    @Query("SELECT SUM(e.costo) AS egresoTotal FROM Egreso e WHERE e.propietario.id = :idPropietario")
    Double sumEgresosByPropietarioId(@Param("idPropietario") Long idPropietario);

}