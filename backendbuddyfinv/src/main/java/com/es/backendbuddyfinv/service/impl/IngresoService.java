package com.es.backendbuddyfinv.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.dto.IngresoDTO;
import com.es.backendbuddyfinv.model.DetalleIngreso;
import com.es.backendbuddyfinv.model.Ingreso;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.model.Venta;
import com.es.backendbuddyfinv.repository.IngresoRepository;

@Service
public class IngresoService {

    @Autowired
    private IngresoRepository ingresoRepository;
    
    
    public List<IngresoDTO> listarDTO(Long idUsuario, String rol) {
    
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            return List.of(); // empleados no ven ingresos
        }
    
        List<Ingreso> ingresos = ingresoRepository.findByPropietarioId(idUsuario);
    
        return convertirADTOs(ingresos);
    }

    public List<IngresoDTO> filtrarPorRango(Long idUsuario, String rol, String inicio, String fin) {
    
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            return List.of();
        }
    
        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);
    
        List<Ingreso> ingresos = ingresoRepository.buscarPorRangoFechas(fechaInicio, fechaFin);
    
        // Filtrar solo los del propietario
        List<Ingreso> filtrados = ingresos.stream()
                .filter(i -> i.getPropietario().getId().equals(idUsuario))
                .toList();
    
        return convertirADTOs(filtrados);
    }

    private List<IngresoDTO> convertirADTOs(List<Ingreso> ingresos) {
        List<IngresoDTO> dtos = new ArrayList<>();

        for (Ingreso ingreso : ingresos) {
            IngresoDTO dto = new IngresoDTO();
            dto.setIdIngreso(ingreso.getIdIngreso());
            dto.setFecha(ingreso.getFecha());
            dto.setTotalDiario(ingreso.getTotalDiario());
            dto.setTotalFacturas(ingreso.getTotalFacturas());

            ingreso.getDetalleIngresos().stream()
                    .findFirst()
                    .map(DetalleIngreso::getVenta)
                    .map(Venta::getUsuario)
                    .map(Usuario::getNombre)
                    .ifPresent(dto::setNombreEmpleado);

            dtos.add(dto);
        }

        return dtos;
    }
}