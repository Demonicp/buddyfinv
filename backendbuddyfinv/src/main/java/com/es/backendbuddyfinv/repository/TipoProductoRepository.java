package com.es.backendbuddyfinv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import com.es.backendbuddyfinv.model.TipoProducto;
import java.util.List;

@Repository
public interface TipoProductoRepository extends JpaRepository<TipoProducto, Long> {
    List<TipoProducto> findByPropietarioId(Long idPropietario);

}
