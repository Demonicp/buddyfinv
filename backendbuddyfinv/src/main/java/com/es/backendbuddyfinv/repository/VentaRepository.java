package com.es.backendbuddyfinv.repository;

import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT DISTINCT v FROM Venta v " +
    "JOIN FETCH v.usuario u " +
    "JOIN FETCH v.estadoVenta ev " +
    "JOIN FETCH v.metodoPago mp " +
    "JOIN FETCH v.detalleVentas dv " +
    "JOIN FETCH dv.producto p " +
    "JOIN FETCH p.estadoProducto ep " +
    "WHERE v.propietario.id = :idPropietario")
List<Venta> findVentasDetalladasByPropietarioId(@Param("idPropietario") Long idPropietario);

    @Query("""
        SELECT DISTINCT v FROM Venta v
        JOIN FETCH v.detalleVentas dv
        JOIN FETCH dv.producto
        JOIN FETCH v.usuario
        JOIN FETCH v.metodoPago
        JOIN FETCH v.estadoVenta
        JOIN FETCH v.propietario
    """)
    List<Venta> findAllWithGraphs();
}
