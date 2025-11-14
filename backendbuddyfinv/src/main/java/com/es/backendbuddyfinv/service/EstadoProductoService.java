package com.es.backendbuddyfinv.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.model.EstadoProducto;
import com.es.backendbuddyfinv.repository.EstadoProductoRepository;

@Service
public class EstadoProductoService {

    @Autowired
    private EstadoProductoRepository estadoProductoRepository;

    public List<EstadoProducto> getTodosSiEsAdmin(String rol) {
        if ("ADMIN".equalsIgnoreCase(rol)) {
            return estadoProductoRepository.findAll();
        } else {
            return List.of();
        }
    }
}
