package com.es.backendbuddyfinv.repository;
import com.es.backendbuddyfinv.model.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface TipoProductoRepository extends JpaRepository<TipoProducto, Long> {
    TipoProducto findByObservacion(String observacion);
    ////SANTIAGO MONTENEGRO MODIFICAR PRODUCTO
    List<TipoProducto> findByPropietarioId(Long idPropietario);
    ////SANTIAGO MONTENEGRO MODIFICAR PRODUCTO FIN
}

