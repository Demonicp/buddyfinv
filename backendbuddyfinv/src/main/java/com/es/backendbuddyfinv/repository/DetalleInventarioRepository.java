package com.es.backendbuddyfinv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.DetalleInventario;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleInventarioRepository extends JpaRepository<DetalleInventario, Long> {
    
    // Buscar detalles de inventario por producto
    //esteban moreno tambien hace uso de este metodo para crear ventas
    List<DetalleInventario> findByProductoIdProducto(Long idProducto);
    
    // Sumar todas las cantidades disponibles de un producto desde DetalleInventario
    @Query("SELECT COALESCE(SUM(di.cantidadDisponible), 0) FROM DetalleInventario di WHERE di.producto.idProducto = :idProducto")
    Integer sumarCantidadDisponiblePorProducto(@Param("idProducto") Long idProducto);

    //metodos para crear venta feture by estebanmoreno

    Optional<DetalleInventario> findByProductoIdProductoAndPropietarioId(Long idProducto, Long propietarioId);


}

