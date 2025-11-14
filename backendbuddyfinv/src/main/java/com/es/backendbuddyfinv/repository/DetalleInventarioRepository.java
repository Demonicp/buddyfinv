package com.es.backendbuddyfinv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.DetalleInventario;
import java.util.List;

@Repository
public interface DetalleInventarioRepository extends JpaRepository<DetalleInventario, Long> {
    
    // Buscar detalles de inventario por producto
    List<DetalleInventario> findByProductoIdProducto(Long idProducto);
    
    // Sumar todas las cantidades disponibles de un producto desde DetalleInventario
    @Query("SELECT COALESCE(SUM(di.cantidadDisponible), 0) FROM DetalleInventario di WHERE di.producto.idProducto = :idProducto")
    Integer sumarCantidadDisponiblePorProducto(@Param("idProducto") Long idProducto);
}

