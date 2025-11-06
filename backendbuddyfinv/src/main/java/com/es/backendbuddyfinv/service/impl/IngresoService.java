package com.es.backendbuddyfinv.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // Crear un nuevo ingreso
    public Ingreso createIngreso(Ingreso ingreso) {
        return ingresoRepository.save(ingreso);
    }

    // Obtener todos los ingresos
    public List<Ingreso> getAllIngresos() {
        return ingresoRepository.findAll();
    }

    // Obtener un ingreso por ID
    public Optional<Ingreso> getIngresoById(Long id) {
        return ingresoRepository.findById(id);
    }

    // Actualizar un ingreso
    public Optional<Ingreso> updateIngreso(Long id, Ingreso ingresoDetails) {
        Optional<Ingreso> optionalIngreso = ingresoRepository.findById(id);
        if (optionalIngreso.isPresent()) {
            Ingreso ingreso = optionalIngreso.get();
            // Actualizar campos específicos si lo necesitás
            // ingreso.setCampo(ingresoDetails.getCampo());
            Ingreso ingresoActualizado = ingresoRepository.save(ingreso);
            return Optional.of(ingresoActualizado);
        }
        return Optional.empty();
    }

    // Eliminar un ingreso
    public boolean deleteIngreso(Long id) {
        if (ingresoRepository.existsById(id)) {
            ingresoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verificar si existe un ingreso
    public boolean existsById(Long id) {
        return ingresoRepository.existsById(id);
    }


    public List<IngresoDTO> listarDTO() {
        List<Ingreso> ingresos = ingresoRepository.findAll();
        return convertirADTOs(ingresos);
    }

    public List<IngresoDTO> filtrarPorRango(String inicio, String fin) {
        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);
    
        List<Ingreso> ingresos = ingresoRepository.buscarPorRangoFechas(fechaInicio, fechaFin);
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
    

    private List<IngresoDTO> convertirADTOs(List<Ingreso> ingresos) {
        List<IngresoDTO> dtos = new ArrayList<>();
    
        for (Ingreso ingreso : ingresos) {
            IngresoDTO dto = new IngresoDTO();
            dto.setIdIngreso(ingreso.getIdIngreso());
            dto.setFecha(ingreso.getFecha());
            dto.setTotalDiario(ingreso.getTotalDiario());
            dto.setTotalFacturas(ingreso.getTotalFacturas());
    
            if (ingreso.getDetalleIngresos() != null) {
                ingreso.getDetalleIngresos().stream()
                    .findFirst()
                    .map(DetalleIngreso::getVenta)
                    .map(Venta::getUsuario)
                    .map(Usuario::getNombre)
                    .ifPresent(dto::setNombreEmpleado);
            }
    
            dtos.add(dto);
        }
    
        return dtos;
    }

}