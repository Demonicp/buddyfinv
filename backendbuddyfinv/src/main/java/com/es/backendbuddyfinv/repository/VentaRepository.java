package com.es.backendbuddyfinv.repository;

import java.time.LocalDateTime;
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


    List<Venta> findByPropietarioId(Long PropietarioId);

    List<Venta> findByUsuarioId(Long usuarioId);

    /*@Query("""
    SELECT DISTINCT v FROM Venta v
    JOIN FETCH v.detalleVentas dv
    JOIN FETCH dv.producto
    JOIN FETCH v.usuario
    JOIN FETCH v.metodoPago
    JOIN FETCH v.estadoVenta
    WHERE v.propietario.id = :idPropietario
    AND (:idVenta IS NULL OR v.idVenta = :idVenta)
    AND (:desde IS NULL OR v.fecha >= :desde)
    AND (:hasta IS NULL OR v.fecha <= :hasta)
    AND (:totalMin IS NULL OR v.total >= :totalMin)
    AND (:totalMax IS NULL OR v.total <= :totalMax)
    ORDER BY v.fecha DESC
""")
List<Venta> filtrarVentas(
    @Param("idPropietario") Long idPropietario,
    @Param("idVenta") Long idVenta,
    @Param("desde") LocalDateTime desde,
    @Param("hasta") LocalDateTime hasta,
    @Param("totalMin") Double totalMin,
    @Param("totalMax") Double totalMax,
    @Param("metodoPago") String metodoPago
);*/
}
