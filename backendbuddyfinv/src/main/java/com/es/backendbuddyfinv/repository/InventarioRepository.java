package com.es.backendbuddyfinv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.Inventario;
//import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    // Sumar todas las cantidades disponibles de un producto
    @Query("SELECT COALESCE(SUM(d.cantidadDisponible), 0) FROM DetalleInventario d WHERE d.producto.idProducto = :idProducto")
    Integer sumarCantidadDisponiblePorProducto(@Param("idProducto") Long idProducto);
}

