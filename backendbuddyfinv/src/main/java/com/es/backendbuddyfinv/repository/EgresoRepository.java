package com.es.backendbuddyfinv.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.Egreso;

@Repository
public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    
    
    
    @Query("""
    SELECT DISTINCT e FROM Egreso e
    JOIN FETCH e.tipoEgreso
    JOIN FETCH e.metodoPago
    WHERE e.propietario.id = :idPropietario
""")
List<Egreso> findByPropietario(@Param("idPropietario") Long idPropietario);




    @Query("""
        SELECT e FROM Egreso e
        JOIN FETCH e.tipoEgreso
        JOIN FETCH e.metodoPago
        WHERE e.propietario.id = :idUsuario AND e.costo = :costo
    """)
    List<Egreso> filtrarByCosto(@Param("idUsuario") Long idUsuario, @Param("costo") Long costo);
    

    

    @Query("SELECT SUM(e.costo) AS egresoTotal FROM Egreso e WHERE e.propietario.id = :idPropietario")
    Double sumEgresosByPropietarioId(@Param("idPropietario") Long idPropietario);




    @Query("SELECT e FROM Egreso e " +
    "JOIN FETCH e.tipoEgreso te " +
    "JOIN FETCH e.metodoPago mp " +
    "WHERE e.propietario.id = :idPropietario " +
    "AND e.fecha BETWEEN :fechaInicio AND :fechaFin " +
    "AND (:categoria IS NULL OR te.descripcion = :categoria) " +
    "AND (:metodoPago IS NULL OR mp.descripcion = :metodoPago)")
List<Egreso> filtrar(@Param("idPropietario") Long idPropietario,
                  @Param("fechaInicio") LocalDate fechaInicio,
                  @Param("fechaFin") LocalDate fechaFin,
                  @Param("categoria") String categoria,
                  @Param("metodoPago") String metodoPago);










    @Query("""
        SELECT e FROM Egreso e
        JOIN FETCH e.tipoEgreso
        JOIN FETCH e.metodoPago
        JOIN FETCH e.propietario
        WHERE e.propietario.id = :idUsuario
        AND e.fecha >= :fechaInicio
        AND e.fecha < :fechaFin
        ORDER BY e.fecha DESC
    """)
    List<Egreso> filtrarPorFechas(@Param("idUsuario") Long idUsuario,
                                   @Param("fechaInicio") LocalDate fechaInicio,
                                   @Param("fechaFin") LocalDate fechaFin);
    
    
    

   
    

}