package com.es.backendbuddyfinv.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.dto.EgresoDTO;
import com.es.backendbuddyfinv.model.Egreso;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.service.impl.EgresoService;
import com.es.backendbuddyfinv.service.impl.UsuarioService;






@RestController
@RequestMapping("/Egresos")
public class EgresoController {
    
    @Autowired
    EgresoService egresoService;

    @Autowired
    UsuarioService usuarioService;



        @GetMapping("/propietario")
        public ResponseEntity<List<EgresoDTO>> obtenerEgresosDetallados() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
    
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long idPropietario = userDetails.getIdUsuario();
    
            List<EgresoDTO> egresos = egresoService.listarDTOsPorUsuario(idPropietario);
            return ResponseEntity.ok(egresos);
        }
    
    

        @GetMapping("/total")
        public ResponseEntity<Double> obtenerTotalEgresosPorUsuario() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
    
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long idPropietario = userDetails.getIdUsuario();
    
            double total = egresoService.costoTotalEgresosXusuario(idPropietario);
            return ResponseEntity.ok(total);
        }


    //yaaaaaaaaaaaaaaaaaaaaaaaaa
    //agrega los egresos


    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Backend funcionando correctamente");
    }


    //yaaaaaaaaaaaaaaaaaaaaaaaa

 
        
        

    //yaaaaaaaaaaaaaaaaaaaaaaaa
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarEgreso(@PathVariable Long id){
        try {
            boolean eliminado=egresoService.deleteEgreso(id);
            if(eliminado){
                return ResponseEntity.ok().body("El egreso ha sido borrado con exito");
            }
            return ResponseEntity.notFound().build();


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el egreso: " + e.getMessage());
        
        
        }

    }

    //este me trae todos los egresos de un usuario en especifico
    //yaaaaaaaaaaaaaaaaaaaaaaaaaa

    
 
    



}
