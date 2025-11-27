package com.es.backendbuddyfinv.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import java.util.NoSuchElementException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.dto.ProductoCrearDTO;
import com.es.backendbuddyfinv.dto.ProductoDTO;
import com.es.backendbuddyfinv.dto.ProductoEdicionDTO;
import com.es.backendbuddyfinv.dto.ProductoReabastecerDTO;
import com.es.backendbuddyfinv.dto.ProductoSelectorDTO;
import com.es.backendbuddyfinv.model.Producto;
import com.es.backendbuddyfinv.repository.InventarioRepository;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.service.impl.ProductoService;

import jakarta.persistence.EntityNotFoundException;




@RestController
@RequestMapping("/productos")

   

public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);


    @Autowired
    private ProductoService productoService ;

    
    @Autowired
    private InventarioRepository inventarioRepository; //creo que aqui no debe ir este repository pero por ahora lo dejare ahi para no romper nada
   
    @Autowired
    private com.es.backendbuddyfinv.repository.TipoProductoRepository tipoProductoRepository;

    @Autowired
    private com.es.backendbuddyfinv.repository.EstadoProductoRepository estadoProductoRepository;
    /*
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
 **/


    
    //OBTENER PRODUCTOS POR USUARIO PROPIETARIO LADY VIDAL. COPIADO DE EGRESOS


    @GetMapping("/test")
    public String test() {
        return "Controlador de productos funcionando correctamente!";
    }
    //se supone que este metodo no debe ir aqui, lo pondr ene comentarios mientras tanto
    @GetMapping("/propietario")
            public ResponseEntity<List<ProductoDTO>> obtenerEgresosDetallados() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
                if (authentication == null || !authentication.isAuthenticated()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
        
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                Long idPropietario = userDetails.getIdUsuario();
        
                List<ProductoDTO> producto = productoService.getProductosPorUsuario(idPropietario);
                                                
                return ResponseEntity.ok(producto);

            }


    //AGREGAR PRODUCTOS O CREAR PRODUCTO  LADY VIDAL 
    @PostMapping("/agregar")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoCrearDTO dto) {
        try {
            // Obtener el usuario autenticado del contexto de seguridad
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Usuario no autenticado");
            }
            
            // Obtener el ID del usuario del CustomUserDetails
            Long propietarioId = null;
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                propietarioId = userDetails.getIdUsuario();
            }
            
            if (propietarioId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("No se pudo obtener el ID del usuario");
            }
            
            Producto nuevo = productoService.createProducto(dto, propietarioId);
            return ResponseEntity.ok(nuevo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Tipo de producto no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al guardar el producto: " + e.getMessage());
        }
    }

  

//////////////////////////INICIO REABASTECER-BUSCAR LADY VIDAL 
@GetMapping("/reabastecer/buscar/{idProducto}")
public ResponseEntity<?> buscarProductoParaReabastecer(@PathVariable Long idProducto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long idUsuario = userDetails.getIdUsuario();
    String rol = userDetails.getRol();
    Long idAdministrador = userDetails.getIdAdministrador();

    Optional<Producto> productoOpt = productoService.getProductoEditablePorCodigo(
        idProducto,
        idUsuario,
        rol,
        idAdministrador
    );

    if (productoOpt.isEmpty()) {
        return ResponseEntity.status(404).body("No existe un producto con ese código o no tienes permiso para reabastecerlo.");
    }

    ProductoReabastecerDTO dto = new ProductoReabastecerDTO(productoOpt.get());
    return ResponseEntity.ok(dto);
}


   //REABASTECER-GUARDAR
@PutMapping("/reabastecer/guardar")
public ResponseEntity<?> reabastecerProducto(@RequestBody ProductoReabastecerDTO dto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long idUsuario = userDetails.getIdUsuario();
    String rol = userDetails.getRol();
    Long idAdministrador = userDetails.getIdAdministrador();

    Optional<Producto> productoOpt = productoService.getProductoEditablePorCodigo(
        dto.getIdProducto(),
        idUsuario,
        rol,
        idAdministrador
    );

    if (productoOpt.isEmpty()) {
        return ResponseEntity.status(404).body("Producto no encontrado o sin permisos.");
    }

    Producto producto = productoOpt.get();

    // Validaciones básicas (pueden complementarse con anotaciones en el DTO)
 
        if (dto.getCantidad() <= 0) {
            return ResponseEntity.badRequest().body("La cantidad a reabastecer debe ser mayor a cero.");
        }

    producto.getDetalleInventarios().forEach(detalle -> {
        detalle.setCantidadDisponible(detalle.getCantidadDisponible() + dto.getCantidad());
    });

    //USE EL MISMO CREATEPRODUCTOMODIFICAR DE SANTIAGO PARA EVITAR DUPLICAR CODIGO
    productoService.createProductoModificar(producto);
    return ResponseEntity.ok("Producto actualizado correctamente.");
}
//////////////////////////FIN REABASTECER LADY VIDAL


////SANTIAGO MONTENEGRO RUALES MODFICAR PRODUCTO INICIO
@GetMapping("/modificar/buscar/{idProducto}")
public ResponseEntity<?> buscarProductoParaModificar(@PathVariable Long idProducto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long idUsuario = userDetails.getIdUsuario();
    String rol = userDetails.getRol();
    Long idAdministrador = userDetails.getIdAdministrador();

    Optional<Producto> productoOpt = productoService.getProductoEditablePorCodigo(
        idProducto,
        idUsuario,
        rol,
        idAdministrador
    );

    if (productoOpt.isEmpty()) {
        return ResponseEntity.status(404).body("No existe un producto con ese código o no tienes permiso para modificarlo.");
    }

    ProductoEdicionDTO dto = new ProductoEdicionDTO(productoOpt.get());
    return ResponseEntity.ok(dto);
}

@PutMapping("/modificar/guardar")
public ResponseEntity<?> guardarModificacionProducto(@RequestBody ProductoEdicionDTO dto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long idUsuario = userDetails.getIdUsuario();
    String rol = userDetails.getRol();
    Long idAdministrador = userDetails.getIdAdministrador();

    Optional<Producto> productoOpt = productoService.getProductoEditablePorCodigo(
        dto.getIdProducto(),
        idUsuario,
        rol,
        idAdministrador
    ); 

    if (productoOpt.isEmpty()) {
        return ResponseEntity.status(404).body("Producto no encontrado o sin permisos.");
    }

    Producto producto = productoOpt.get();

    // Validaciones básicas (pueden complementarse con anotaciones en el DTO)
    if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
        return ResponseEntity.badRequest().body("No puedes dejar el campo 'nombre' vacío.");
    }

    if (dto.getPrecio() <= 0) {
        return ResponseEntity.badRequest().body("El campo 'precio' debe ser mayor a cero.");
    }

    producto.setNombre(dto.getNombre());
    producto.setPrecio(dto.getPrecio());

    producto.setTipoProducto(
        tipoProductoRepository.findById(dto.getIdTipoProducto())
            .orElseThrow(() -> new RuntimeException("Tipo de producto no válido"))
    );

    producto.setEstadoProducto(
        estadoProductoRepository.findById(dto.getIdEstadoProducto())
            .orElseThrow(() -> new RuntimeException("Estado de producto no válido"))
    );

    productoService.createProductoModificar(producto);
    return ResponseEntity.ok("Producto actualizado correctamente.");
}
/////SANTIAGO MONTENEGRO RUALES MODIFICAR FIN
/// 
/// 





    /**
 * Obtener producto por id (para la búsqueda por id en el autocomplete).
 * Devuelve 404 si no existe o no es accesible.
 */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductoPorId(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long requesterId = userDetails.getIdUsuario();
        List<String> roles = List.of(userDetails.getRol()); // si userDetails tiene lista, adapta

        List<com.es.backendbuddyfinv.dto.ProductoSelectorDTO> res =
            productoService.buscarParaSelector(requesterId, roles, id.toString(), 1);

        if (res == null || res.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado o no accesible");
        }
        return ResponseEntity.ok(res.get(0));
    }




    @GetMapping("/search")
    public ResponseEntity<?> searchProductos(@RequestParam("q") String q,
                                        @RequestParam(value = "limit", defaultValue = "12") int limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long requesterId = userDetails.getIdUsuario();
        List<String> roles = List.of(userDetails.getRol()); // si userDetails tiene lista, adapta

        try {
            List<com.es.backendbuddyfinv.dto.ProductoSelectorDTO> resultados =
                productoService.buscarParaSelector(requesterId, roles, q, limit);
            return ResponseEntity.ok(resultados);
        } catch (Exception ex) {
            // proteges el endpoint frente a errores inesperados
            logger.error("Error en searchProductos q='{}': {}", q, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Error buscando productos: " + ex.getMessage());
        }
    }
///Santiago Montenegro Historia 17
@GetMapping("/inventario/search")
public ResponseEntity<?> buscarInventario(
        @RequestParam(required = false) Long idProducto,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) Long idTipoProducto) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long requesterId = userDetails.getIdUsuario();
    List<String> roles = List.of(userDetails.getRol()); // si tu CustomUserDetails maneja lista, adapta

    try {
        // 🔹 Usa ProductoDTO en lugar de ProductoSelectorDTO
        List<ProductoDTO> resultados = productoService.buscarInventario(
                requesterId, roles, idProducto, nombre, idTipoProducto);

        return ResponseEntity.ok(resultados);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Debes completar por lo menos un campo de búsqueda");
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("El producto buscado no está registrado en el inventario");
    } catch (Exception ex) {
        logger.error("Error en buscarInventario: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Error buscando productos: " + ex.getMessage());
    }
}
///Santiago Montenegro Historia 17


}
