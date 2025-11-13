package com.es.backendbuddyfinv.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.model.MetodoPago;
import com.es.backendbuddyfinv.repository.MetodoPagoRepository;

/**
 * Servicio para operaciones de negocio con Métodos de Pago
 * Proporciona métodos para consultar métodos de pago desde la base de datos
 * 
 * Los métodos de pago disponibles son:
 * - 1: Efectivo
 * - 2: Transferencia
 * - 3: Tarjeta
 */
@Service
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    /**
     * Obtiene todos los métodos de pago disponibles en la base de datos
     * @return Lista de todos los métodos de pago
     */
    public List<MetodoPago> getAllMetodosPago() {
        return metodoPagoRepository.findAll();
    }

    /**
     * Busca un método de pago por su ID
     * @param id ID del método de pago (1=Efectivo, 2=Transferencia, 3=Tarjeta)
     * @return Optional con el método de pago si existe, vacío si no se encuentra
     */
    public Optional<MetodoPago> getMetodoPagoById(Integer id) {
        return metodoPagoRepository.findById(id);
    }
}

