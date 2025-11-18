package com.es.backendbuddyfinv.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.es.backendbuddyfinv.dto.ProductoCrearDTO;
import com.es.backendbuddyfinv.dto.ProductoDTO;
import com.es.backendbuddyfinv.dto.ProductoEdicionDTO;
import com.es.backendbuddyfinv.model.Producto;
import com.es.backendbuddyfinv.repository.InventarioRepository;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.service.impl.ProductoService;

import jakarta.persistence.EntityNotFoundException;



@CrossOrigin(origins="http://localhost:5173")
//para aceptar las peticiones desde el frontend
@RestController
@RequestMapping("/productos")

   

public class ProductoController {


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

    //funcion para agregar productos
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

    /** 
    @GetMapping("/mine")
    public ResponseEntity<List<ProductoDTO>> obtenerMisProductos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // este es el subject del token
    }**/

         /**List<Producto> productos = ProductoRepository.findByPropietarioUsuario(username);
    List<ProductoDTO> productosDTO = productos.stream().map(p -> {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(p.getIdProducto());
        dto.setNombre(p.getNombre());
        dto.setPrecio(p.getPrecio());
        dto.setTipoProducto(p.getTipoProducto()!=null ? p.getTipoProducto().getNombre() : "Sin tipo");
        dto.setEstadoProducto(p.getEstadoProducto()!=null ? p.getEstadoProducto().getNombre() : "Sin estado");
        dto.setPropietario(p.getPropietario()!=null ? p.getPropietario().getNombre() : "Sin propietario");
        return dto;
    }).collect(Collectors.toList());

    return ResponseEntity.ok(productosDTO);
}**/

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







}
