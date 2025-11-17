package com.es.backendbuddyfinv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.es.backendbuddyfinv.model.EstadoVenta;

@Repository
public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, Long> {

}