package com.es.backendbuddyfinv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.Producto;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("""
    SELECT DISTINCT p FROM Producto p
    JOIN FETCH p.tipoProducto
    JOIN FETCH p.estadoProducto
    JOIN FETCH p.propietario
    JOIN FETCH p.detalleInventarios d
    WHERE p.propietario.id = :idPropietario
""")
List<Producto> findByPropietarioConInventario(@Param("idPropietario") Long idPropietario);
/////SANTIAGO MODIFCIAR PRODUCTO
List<Producto> findByPropietarioId(Long propietarioId);
List<Producto> findByPropietarioUsuario(String usuario);

// Caso 1: El producto pertenece directamente al propietario
@Query("SELECT p FROM Producto p WHERE p.idProducto = :idProducto AND p.propietario.id = :idPropietario")
Optional<Producto> findByIdAndPropietario(@Param("idProducto") Long idProducto, @Param("idPropietario") Long idPropietario);
/////FIN SANTIAGO MODDIFICAR PRODUCTO
    
}
