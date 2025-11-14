package com.es.backendbuddyfinv.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.model.TipoProducto;
import com.es.backendbuddyfinv.repository.TipoProductoRepository;

@Service
public class TipoProductoService {

    @Autowired
    private TipoProductoRepository tipoProductoRepository;
    public List<TipoProducto> getTiposVisiblesPorUsuario(Long idUsuario, String rol) {
        if ("ADMIN".equalsIgnoreCase(rol)) {
            return tipoProductoRepository.findByPropietarioId(idUsuario);
        } else {
            return List.of(); // o Optional.empty() si querés envolverlo
        }
    }

}
