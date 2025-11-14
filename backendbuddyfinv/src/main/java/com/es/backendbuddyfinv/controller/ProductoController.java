package com.es.backendbuddyfinv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

//import com.es.backendbuddyfinv.repository.ProductoRepository;
import com.es.backendbuddyfinv.repository.InventarioRepository;
import com.es.backendbuddyfinv.service.impl.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.es.backendbuddyfinv.security.CustomUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.es.backendbuddyfinv.dto.ProductoDTO;
import com.es.backendbuddyfinv.dto.ProductoEdicionDTO;
//import com.es.backendbuddyfinv.dto.UsuarioDTO;
import com.es.backendbuddyfinv.model.Producto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.es.backendbuddyfinv.security.CustomUserDetails;
import com.es.backendbuddyfinv.security.JwtAuthenticationFilter;
import jakarta.persistence.EntityNotFoundException;


import java.util.List;

import com.es.backendbuddyfinv.dto.EgresoDTO;
import com.es.backendbuddyfinv.dto.ProductoCrearDTO;


@CrossOrigin(origins="http://localhost:5173")
//para aceptar las peticiones desde el frontend
@RestController
@RequestMapping("/productos")

   

public class ProductoController {

    //@Autowired
    //para inyectar el repositorio de productos

    //private ProductoRepository productoRepository;
    //endpoint para obtener todos los productos

     @Autowired
    private ProductoService productoService ;

    
    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
private com.es.backendbuddyfinv.repository.TipoProductoRepository tipoProductoRepository;

@Autowired
private com.es.backendbuddyfinv.repository.EstadoProductoRepository estadoProductoRepository;

    //metodo para obtener todos los productos
    @GetMapping("/all")
    public ResponseEntity<List<ProductoDTO>>obtenerProductos(){

        List<Producto> productos= productoService.getAllProductos();
     
         System.out.println("Productos encontrados: " + productos.size());


        // Convertir a DTOs para evitar referencias circulares, incluyendo cantidadDisponible
        List<ProductoDTO> productosDTO = productos.stream()
            .map(producto -> {
                // Calcular la cantidad disponible sumando todos los inventarios del producto
                Integer cantidadDisponible = inventarioRepository.sumarCantidadDisponiblePorProducto(producto.getIdProducto());
                return new ProductoDTO(producto, cantidadDisponible);
            })
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/test")
    public String test() {
        return "Controlador de productos funcionando correctamente!";
    }

    @GetMapping("/por-usuario/{id}")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorUsuario(@PathVariable Long id) {
        List<Producto> productos = productoService.getProductosPorUsuario(id);
        List<ProductoDTO> productosDTO = productos.stream()
            .map(producto -> {
                Integer cantidadDisponible = inventarioRepository.sumarCantidadDisponiblePorProducto(producto.getIdProducto());
                return new ProductoDTO(producto, cantidadDisponible);
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(productosDTO);
    }
    
////SANTIAGO MONTENEGRO RUALES
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

    productoService.createProducto(producto);
    return ResponseEntity.ok("Producto actualizado correctamente.");
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


}
