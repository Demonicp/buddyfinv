package com.es.backendbuddyfinv.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.dto.EgresoDTO;
import com.es.backendbuddyfinv.model.Egreso;
import com.es.backendbuddyfinv.repository.EgresoRepository;

@Service
public class EgresoService {


    @Autowired
    private EgresoRepository egresoRepository;

    // Crear un nuevo egreso
    public Egreso createEgreso(Egreso egreso) {
        return egresoRepository.save(egreso);
    }

    // Obtener todos los egresos
    public List<Egreso> getAllEgresos() {
        return egresoRepository.findAll();
    }

    // Obtener un egreso por ID
    public Optional<Egreso> getEgresoById(Long id) {
        return egresoRepository.findById(id);
    }

    // Actualizar un egreso
    public Egreso updateEgreso(Long id, Egreso egresoDetails) {
        Optional<Egreso> optionalEgreso = egresoRepository.findById(id);
        if (optionalEgreso.isPresent()) {
            Egreso egreso = optionalEgreso.get();
            // Aquí puedes actualizar los campos específicos del egreso
            // egreso.setCampo(egresoDetails.getCampo());
            return egresoRepository.save(egreso);
        }
        return null;
    }

    // Eliminar un egreso
    public boolean deleteEgreso(Long id) {
        if (egresoRepository.existsById(id)) {
            egresoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verificar si existe un egreso
    public boolean existsById(Long id) {
        return egresoRepository.existsById(id);
    }

    public List<EgresoDTO> filtrarPorFechas(Long idUsuario, String fechaInicio, String fechaFin) {
        try {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin).plusDays(1);
            List<Egreso> egresos = egresoRepository.filtrarPorFechas(idUsuario, inicio, fin);
            System.out.println("🧪 Rango final aplicado: " + inicio + " hasta " + fin);


        return egresos.stream()
                      .map(EgresoDTO::new)
                      .collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            System.err.println("❌ Error al convertir fechas: " + e.getMessage());
            return List.of(); // Devuelve lista vacía si hay error
        }
    }
    
   
    

    public List<Egreso> ListarEgresosXusuario(Long idPropietario){
        return egresoRepository.findByPropietario(idPropietario);

    }

    public double costoTotalEgresosXusuario(Long idPropietario){

        return egresoRepository.sumEgresosByPropietarioId(idPropietario);


    }

    public List<EgresoDTO> listarDTOsPorUsuario(Long idPropietario) {
        List<Egreso> egresos = egresoRepository.findByPropietario(idPropietario);
        return egresos.stream()
                      .map(EgresoDTO::new)
                      .collect(Collectors.toList());
    }


}
