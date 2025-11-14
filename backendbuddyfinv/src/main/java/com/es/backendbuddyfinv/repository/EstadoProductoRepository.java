package com.es.backendbuddyfinv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.es.backendbuddyfinv.model.EstadoProducto;
import java.util.List;

@Repository
public interface EstadoProductoRepository extends JpaRepository<EstadoProducto, Long> {
    List<EstadoProducto> findAll();
}
