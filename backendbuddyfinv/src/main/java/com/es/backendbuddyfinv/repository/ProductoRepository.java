package com.es.backendbuddyfinv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.Producto;

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
    /// en crear ventas esteban moreno tambine hace uso de este metodo
    List<Producto> findByPropietarioId(Long propietarioId);
    List<Producto> findByPropietarioUsuario(String usuario);

    // Caso 1: El producto pertenece directamente al propietario
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :idProducto AND p.propietario.id = :idPropietario")
    Optional<Producto> findByIdAndPropietario(@Param("idProducto") Long idProducto, @Param("idPropietario") Long idPropietario);
    /////FIN SANTIAGO MODDIFICAR PRODUCTO

        


    //implementacion esteban moreno roa /feature/ crear venta

    List<Producto> findByTipoProductoIdTipoProducto(Long idTipoProducto);

    List<Producto> findByEstadoProductoIdEstadoPro(Long idEstadoPro);

    Optional<Producto> findByIdProductoAndPropietarioId(Long idProducto, Long propietarioId);



}
