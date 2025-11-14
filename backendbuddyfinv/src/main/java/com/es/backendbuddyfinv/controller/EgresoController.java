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


    // ============================================
    // ENDPOINT PARA AGREGAR NUEVO EGRESO
    // ============================================
    /**
     * Endpoint POST para registrar un nuevo egreso
     * Recibe los datos del formulario del frontend y crea el egreso en la base de datos
     * 
     * @param requestDTO Objeto con los datos del egreso (fecha, concepto, categoría, valor, método de pago)
     * @return ResponseEntity con el egreso creado o mensaje de error
     */
    @PostMapping("/agregarEgreso")
    @Transactional
    //cree este dto llamado EgresoRequestDTO que lo que hace es pedirle los campos necesarios al usuario
    public ResponseEntity<?> agregarEgreso(@RequestBody EgresoRequestDTO requestDTO) {
        try {
            //Obtiene la autenticacion
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            //valida si esta autenticado para hacer este metodo(en este caso agregar un egreso)
            if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                System.out.println("❌ Usuario no autenticado para POST /Egresos/agregarEgreso");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("No está autenticado. Por favor, inicie sesión nuevamente.");
            }
                String obs = requestDTO.getObservacion();
            if (obs == null || obs.length() > 300 || !obs.matches("^[a-zA-Z0-9\\s.,áéíóúÁÉÍÓÚñÑ-]*$")) {
                throw new IllegalArgumentException("Observación inválida: debe ser alfanumérica y máximo 300 caracteres");
                }

            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long idPropietario = userDetails.getIdUsuario();
            
            //excepciones desde aqui
            Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(idPropietario);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            Usuario propietario = usuarioOpt.get();
            //cree el metodo de pago para validar de que exista
            Optional<MetodoPago> metodoPagoOpt = metodoPagoService.getMetodoPagoById(requestDTO.getIdMetodoPago());
            if (metodoPagoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Método de pago no encontrado");
            }
            MetodoPago metodoPago = metodoPagoOpt.get();
    
            String categoriaLimpia = requestDTO.getCategoria() != null ? requestDTO.getCategoria().trim() : "";
            if (categoriaLimpia.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La categoría no puede estar vacía");
            }
    
            Optional<TipoEgreso> tipoEgresoOpt = tipoEgresoService.getTipoEgresoByDescripcion(categoriaLimpia);
            TipoEgreso tipoEgreso;
    
            if (tipoEgresoOpt.isEmpty()) {
                tipoEgreso = new TipoEgreso();
                tipoEgreso.setDescripcion(categoriaLimpia);
                
                try {
                    tipoEgreso = tipoEgresoService.createTipoEgreso(tipoEgreso);
                } catch (org.springframework.dao.DataIntegrityViolationException |
                         jakarta.persistence.PersistenceException e) {
                    tipoEgresoOpt = tipoEgresoService.getTipoEgresoByDescripcion(categoriaLimpia);
                    if (tipoEgresoOpt.isPresent()) {
                        tipoEgreso = tipoEgresoOpt.get();
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al crear tipo de egreso. La secuencia de la base de datos puede estar desincronizada. Intente usar una categoría existente o contacte al administrador.");
                    }
                } catch (Exception e) {
                    tipoEgresoOpt = tipoEgresoService.getTipoEgresoByDescripcion(categoriaLimpia);
                    if (tipoEgresoOpt.isPresent()) {
                        tipoEgreso = tipoEgresoOpt.get();
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Error al crear tipo de egreso: " + e.getMessage());
                    }
                }
            } else {
                tipoEgreso = tipoEgresoOpt.get();
            }
            //hasta aqui
            //ese nuevo egreso es como un objeto vacio al momento de nosotros darle unos parametros a la entrada se sobre escriben a este objeto
            Egreso nuevoEgreso = new Egreso();
            nuevoEgreso.setCosto(requestDTO.getCosto());
            nuevoEgreso.setObservacion(requestDTO.getObservacion());
            nuevoEgreso.setFecha(LocalDate.parse(requestDTO.getFecha()));
            nuevoEgreso.setMetodoPago(metodoPago);
            nuevoEgreso.setTipoEgreso(tipoEgreso);
            nuevoEgreso.setPropietario(propietario);
            
            //aqui guarda el egreso con el metodo createEgreso (que es lo mismo que guardar)
            Egreso egresoGuardado = egresoService.createEgreso(nuevoEgreso);
            EgresoDTO dto = new EgresoDTO(egresoGuardado);
    
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permisos para realizar esta acción. Por favor, inicie sesión nuevamente.");
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de autenticación. Por favor, inicie sesión nuevamente.");
        } catch (Exception e) {
            System.err.println("Error al agregar egreso: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al agregar egreso: " + e.getMessage());
        }
    }
    

  


    @GetMapping("/filtrarFechas")
    public ResponseEntity<List<EgresoDTO>> filtrarPorFechas(String  fechaInicio,String  fechaFin ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //verificacion
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long idPropietario = userDetails.getIdUsuario();
        //hasta aqui


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
