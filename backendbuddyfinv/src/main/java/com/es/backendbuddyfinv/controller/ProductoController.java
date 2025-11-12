package com.es.backendbuddyfinv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.es.backendbuddyfinv.repository.InventarioRepository;
import com.es.backendbuddyfinv.service.impl.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.stream.Collectors;
import com.es.backendbuddyfinv.dto.ProductoDTO;
import com.es.backendbuddyfinv.model.Producto;
import java.util.List;

//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.Authentication;
//import com.es.backendbuddyfinv.repository.ProductoRepository;
//import com.es.backendbuddyfinv.dto.UsuarioDTO;


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
                // Calcular la cantidad disponible sumando todos los inventarios del producto
                Integer cantidadDisponible = inventarioRepository.sumarCantidadDisponiblePorProducto(producto.getIdProducto());
                return new ProductoDTO(producto, cantidadDisponible);
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(productosDTO);
    }

    @PostMapping("/agregar")
    public ResponseEntity<String> agregarProducto(@RequestBody Producto producto) {
    try {
        productoService.createProducto(producto);
        return ResponseEntity.ok("Producto agregado correctamente");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error al agregar producto");
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


}
