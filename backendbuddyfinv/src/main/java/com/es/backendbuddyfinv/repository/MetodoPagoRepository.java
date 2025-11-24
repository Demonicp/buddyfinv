package com.es.backendbuddyfinv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import com.es.backendbuddyfinv.model.MetodoPago;

/**
 * Repositorio para operaciones con la entidad MetodoPago
 * Proporciona métodos CRUD básicos heredados de JpaRepository
 * 
 * El tipo de ID es Integer porque MetodoPago usa int idMetodoPago
 * 
 * Métodos disponibles automáticamente:
 * - findAll(): Obtener todos los métodos de pago
 * - findById(Integer id): Buscar por ID
 * - save(MetodoPago): Guardar o actualizar
 * - deleteById(Integer id): Eliminar por ID
 */
@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
    


}

