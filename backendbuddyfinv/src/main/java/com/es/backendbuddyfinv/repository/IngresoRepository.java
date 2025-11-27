package com.es.backendbuddyfinv.repository;

import com.es.backendbuddyfinv.model.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    List<Ingreso> findByPropietarioId(Long idPropietario);

    @Query("""
        SELECT i
        FROM Ingreso i
        WHERE i.fecha BETWEEN :fechaInicio AND :fechaFin
        ORDER BY i.fecha DESC
    """)
    List<Ingreso> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
}