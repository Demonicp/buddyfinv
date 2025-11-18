package com.es.backendbuddyfinv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.dto.EstadoVentaDTO;
import com.es.backendbuddyfinv.model.EstadoVenta;

@Repository
public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, Long> {

    @Query("select new com.es.backendbuddyfinv.dto.EstadoVentaDTO(e.idEstadoVenta, e.observacion) from EstadoVenta e")
    List<EstadoVentaDTO> findAllAsDto();


}