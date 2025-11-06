package com.es.backendbuddyfinv.controller;

import com.es.backendbuddyfinv.dto.IngresoDTO;
import com.es.backendbuddyfinv.model.Ingreso;
import com.es.backendbuddyfinv.service.impl.IngresoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingresos")
@CrossOrigin(origins = "*") // Permite peticiones desde tu frontend (Vue)
public class IngresoController {

    @Autowired
    private IngresoService ingresoService;

    @GetMapping("/filtrar")
public ResponseEntity<List<IngresoDTO>> filtrarPorFechas(
    @RequestParam String inicio,
    @RequestParam String fin
) {
    return ResponseEntity.ok(ingresoService.filtrarPorRango(inicio, fin));
}

    // Crear un nuevo ingreso
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarIngreso(@RequestBody Ingreso ingreso) {
        try {
            Ingreso nuevoIngreso = ingresoService.createIngreso(ingreso);
            return ResponseEntity.status(201).body(nuevoIngreso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar ingreso: " + e.getMessage());
        }
    }

    @GetMapping("/all")
public ResponseEntity<List<IngresoDTO>> obtenerTodosIngresosDTO() {
    try {
        List<IngresoDTO> ingresos = ingresoService.listarDTO();
        System.out.println("Ingresos encontrados: " + ingresos.size());
        return ResponseEntity.ok(ingresos);
    } catch (Exception e) {
        System.err.println("Error al obtener ingresos DTO: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body(null);
    }
}

    // Obtener ingreso por ID
    @GetMapping("/{id}")
    public ResponseEntity<Ingreso> obtenerIngresoPorId(@PathVariable Long id) {
        Optional<Ingreso> ingreso = ingresoService.getIngresoById(id);
        return ingreso.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar ingreso
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarIngreso(@PathVariable Long id, @RequestBody Ingreso ingresoDetails) {
        try {
            Optional<Ingreso> ingresoOptional = ingresoService.getIngresoById(id);

            if (ingresoOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ingreso ingreso = ingresoOptional.get();

            // Actualizar campos básicos
            if (ingresoDetails.getFecha() != null) ingreso.setFecha(ingresoDetails.getFecha());
            if (ingresoDetails.getTotalDiario() != 0.0) ingreso.setTotalDiario(ingresoDetails.getTotalDiario());

            Optional<Ingreso> ingresoActualizadoOptional = ingresoService.updateIngreso(id, ingreso);
            return ingresoActualizadoOptional.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar ingreso: " + e.getMessage());
        }
    }

    // Eliminar ingreso
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarIngreso(@PathVariable Long id) {
        try {
            boolean eliminado = ingresoService.deleteIngreso(id);
            if (eliminado) {
                return ResponseEntity.ok().body("Ingreso eliminado exitosamente");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar ingreso: " + e.getMessage());
        }
    }

    // Endpoint de prueba
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Controlador de ingresos funcionando correctamente");
    }

}