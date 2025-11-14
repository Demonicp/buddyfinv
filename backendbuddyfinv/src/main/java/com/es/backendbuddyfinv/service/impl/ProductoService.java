package com.es.backendbuddyfinv.service.impl;

import java.util.List;
//import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.backendbuddyfinv.dto.ProductoCrearDTO;
import com.es.backendbuddyfinv.dto.ProductoDTO;
import com.es.backendbuddyfinv.model.Producto;
import com.es.backendbuddyfinv.model.Inventario;
import com.es.backendbuddyfinv.model.DetalleInventario;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.repository.ProductoRepository;
import com.es.backendbuddyfinv.repository.InventarioRepository;
import com.es.backendbuddyfinv.repository.DetalleInventarioRepository;
import com.es.backendbuddyfinv.repository.UsuarioRepository;
import com.es.backendbuddyfinv.repository.EstadoProductoRepository;
import com.es.backendbuddyfinv.repository.TipoProductoRepository;
import com.es.backendbuddyfinv.model.TipoProducto;
import com.es.backendbuddyfinv.model.EstadoProducto;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private TipoProductoRepository tipoProductoRepository;

    @Autowired
    private EstadoProductoRepository estadoProductoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private DetalleInventarioRepository detalleInventarioRepository;

    // Crear un nuevo producto
    @Transactional
    public Producto createProducto(ProductoCrearDTO dto, Long propietarioId) {
        logger.info("Iniciando creación de producto. Nombre: {}, Precio: {}, TipoProductoId: {}, EstadoProductoId: {}, CantidadDisponible: {}, PropietarioId: {}", 
                    dto.getNombre(), dto.getPrecio(), dto.getTipoProductoId(), dto.getEstadoProductoId(), dto.getCantidadDisponible(), propietarioId);
        
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());

        // Busca los objetos relacionados por ID
        // Cambio: usar findById() en lugar de findByObservacion() porque el DTO envía IDs (Long) y no observaciones (String)
        // Validar que los IDs no sean null antes de usarlos (corrige el warning de null safety)
        Long tipoProductoId = dto.getTipoProductoId();
        Long estadoProductoId = dto.getEstadoProductoId();
        
        if (tipoProductoId == null) {
            logger.error("El ID del tipo de producto es null");
            throw new EntityNotFoundException("El ID del tipo de producto no puede ser null");
        }
        if (estadoProductoId == null) {
            logger.error("El ID del estado de producto es null");
            throw new EntityNotFoundException("El ID del estado de producto no puede ser null");
        }
        
        logger.debug("Buscando TipoProducto con ID: {}", tipoProductoId);
        TipoProducto tipo = tipoProductoRepository.findById(tipoProductoId)
                .orElseThrow(() -> {
                    logger.error("Tipo de producto no encontrado con ID: {}", tipoProductoId);
                    return new EntityNotFoundException("Tipo de producto no encontrado con ID: " + tipoProductoId);
                });
        
        logger.debug("Buscando EstadoProducto con ID: {}", estadoProductoId);
        EstadoProducto estado = estadoProductoRepository.findById(estadoProductoId)
                .orElseThrow(() -> {
                    logger.error("Estado de producto no encontrado con ID: {}", estadoProductoId);
                    return new EntityNotFoundException("Estado de producto no encontrado con ID: " + estadoProductoId);
                });

        producto.setTipoProducto(tipo);
        producto.setEstadoProducto(estado);
        
        // Asignar propietario si se proporcionó
        if (propietarioId != null) {
            logger.debug("Buscando Usuario (propietario) con ID: {}", propietarioId);
            Usuario propietario = usuarioRepository.findById(propietarioId)
                    .orElseThrow(() -> {
                        logger.error("Usuario (propietario) no encontrado con ID: {}", propietarioId);
                        return new EntityNotFoundException("Usuario (propietario) no encontrado con ID: " + propietarioId);
                    });
            producto.setPropietario(propietario);
            logger.debug("Propietario asignado al producto: {}", propietario.getNombre());
        }
        
        logger.debug("Guardando producto en la base de datos...");
        Producto productoGuardado = productoRepository.save(producto);
        logger.info("Producto guardado exitosamente con ID: {}", productoGuardado.getIdProducto());
        
        // Crear DetalleInventario si se proporcionó cantidadDisponible
        Integer cantidadDisponible = dto.getCantidadDisponible();
        if (cantidadDisponible != null && cantidadDisponible > 0) {
            logger.debug("Creando DetalleInventario con cantidad: {}", cantidadDisponible);
            
            // Buscar o crear un Inventario para el propietario
            Inventario inventario = null;
            if (propietarioId != null) {
                // Buscar si existe un inventario del propietario
                List<Inventario> inventariosDelPropietario = inventarioRepository.findAll().stream()
                    .filter(inv -> inv.getPropietario() != null && inv.getPropietario().getId().equals(propietarioId))
                    .toList();
                
                if (!inventariosDelPropietario.isEmpty()) {
                    inventario = inventariosDelPropietario.get(0);
                    logger.debug("Usando inventario existente del propietario con ID: {}", inventario.getIdInventario());
                } else {
                    // Crear un nuevo inventario para el propietario
                    inventario = new Inventario();
                    Usuario propietario = usuarioRepository.findById(propietarioId)
                            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
                    inventario.setPropietario(propietario);
                    // Asegurar que cantidadDisponible tenga un valor por defecto (0)
                    
                    inventario = inventarioRepository.save(inventario);
                    logger.debug("Creado nuevo inventario con ID: {}", inventario.getIdInventario());
                }
            } else {
                // Si no hay propietario, crear un inventario sin propietario
                inventario = new Inventario();
                // Asegurar que cantidadDisponible tenga un valor por defecto (0)
                
                inventario = inventarioRepository.save(inventario);
                logger.debug("Creado nuevo inventario sin propietario con ID: {}", inventario.getIdInventario());
            }
            
            // Crear DetalleInventario
            DetalleInventario detalleInventario = new DetalleInventario();
            detalleInventario.setCantidadDisponible(cantidadDisponible);
            detalleInventario.setProducto(productoGuardado);
            detalleInventario.setInventario(inventario);
            if (propietarioId != null) {
                Usuario propietario = usuarioRepository.findById(propietarioId)
                        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
                detalleInventario.setPropietario(propietario);
            }
            
            detalleInventarioRepository.save(detalleInventario);
            logger.info("DetalleInventario creado exitosamente con cantidad: {}", cantidadDisponible);
        }
        
        return productoGuardado;
    }

    // Obtener todos los productos
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    // Obtener productos por usuario (propietario)
    public List<Producto> getProductosPorUsuario(Long idUsuario) {
        return productoRepository.findByPropietarioId(idUsuario);
    }


    // Actualizar un producto
    public Producto updateProducto(Long id, Producto productoDetails) {
        Optional<Producto> optionalProducto = productoRepository.findById(id);
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            // Aquí puedes actualizar los campos específicos del producto
            // producto.setCampo(productoDetails.getCampo());
            return productoRepository.save(producto);
        }
        return null;
    }

    // Eliminar un producto
    public boolean deleteProducto(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

// Verificar si existe un producto
public boolean existsById(Long id) {
    return productoRepository.existsById(id);
}

// Obtener producto editable según rol del usuario
public Optional<Producto> getProductoEditablePorCodigo(Long idProducto, Long idUsuario, String rol, Long idAdministrador) {
    if ("ADMIN".equalsIgnoreCase(rol)) {
        return getProductoSiEsDelPropietario(idProducto, idUsuario);
    } else {
        return Optional.empty();
    }
}

// Obtener producto si pertenece directamente al propietario
public Optional<Producto> getProductoSiEsDelPropietario(Long idProducto, Long idPropietario) {
    return productoRepository.findByIdAndPropietario(idProducto, idPropietario);
}

}


}