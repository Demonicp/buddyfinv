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

    @Query(value =
       "select p.id_producto " +
       "from productos p " +
       "where (p.id_producto::text = :q) or (p.nombre ilike concat('%', :q, '%')) " +
       "order by (p.nombre ilike concat(:q, '%')) desc, p.nombre " +
       "limit :limit",
       nativeQuery = true)
    List<Long> searchIdsByQ(@Param("q") String q, @Param("limit") int limit);

    /**
     * Trae productos con sus relaciones necesarias para mapeo (evita N+1).
     * Usar después de searchIdsByQ para obtener datos completos respetando limit/orden.
     */
    @Query("select distinct p from Producto p "
         + "left join fetch p.propietario prop "
         + "left join fetch p.tipoProducto tp "
         + "left join fetch p.estadoProducto ep "
         + "left join fetch p.detalleInventarios di "
         + "where p.idProducto in :ids")
    List<Producto> findWithRelationsByIds(@Param("ids") List<Long> ids);

    /**
     * Trae un producto con relaciones (para búsqueda por id).
     */
    @Query("select p from Producto p "
         + "left join fetch p.propietario prop "
         + "left join fetch p.tipoProducto tp "
         + "left join fetch p.estadoProducto ep "
         + "left join fetch p.detalleInventarios di "
         + "where p.idProducto = :id")
    Optional<Producto> findWithRelationsById(@Param("id") Long id);




}
