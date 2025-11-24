package com.es.backendbuddyfinv.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import com.es.backendbuddyfinv.dto.ProductoCrearDTO;
import com.es.backendbuddyfinv.dto.ProductoSelectorDTO;
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

    // Verificar si existe un producto
    public boolean existsById(Long id) {
        return productoRepository.existsById(id);
    }

    // Obtener productos por ID de usuario (propietario)
public List<ProductoDTO> getProductosPorUsuario(Long idPropietario) {
    List<Producto> productos = productoRepository.findByPropietarioConInventario(idPropietario);

    return productos.stream()
        .flatMap(producto -> producto.getDetalleInventarios().stream()
            .map(detalle -> new ProductoDTO(producto, detalle.getCantidadDisponible()))
        )
        .toList();
}
///SANTIAGO MONTENEGRO RUALES MODIFICAR PRODUCTO


public boolean deleteProducto(Long id) {
    if (productoRepository.existsById(id)) {
        productoRepository.deleteById(id);
        return true;
    }
    return false;
}

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

    // Crear un nuevo producto
    public Producto createProductoModificar(Producto producto) {
        return productoRepository.save(producto);
    }
///Santiago Montenegro Historia 17

@Transactional(readOnly = true)
public List<ProductoDTO> buscarInventario(Long requesterId, List<String> roles,
                                          Long idProducto, String nombre, Long idTipoProducto) {
    // Validación: al menos un campo
    if (idProducto == null && (nombre == null || nombre.trim().isEmpty()) && idTipoProducto == null) {
        throw new IllegalArgumentException("Debes completar por lo menos un campo de búsqueda");
    }

    List<Producto> productos = new ArrayList<>();

    if (idProducto != null) {
        productoRepository.findWithRelationsById(idProducto)
            .filter(p -> canAccessProducto(requesterId, roles, p))
            .ifPresent(productos::add);
    } else if (nombre != null && !nombre.trim().isEmpty()) {
        List<Long> ids = productoRepository.searchIdsByQ(nombre.trim(), 20);
        productos.addAll(productoRepository.findWithRelationsByIds(ids));
    } else if (idTipoProducto != null) {
        productos.addAll(productoRepository.findByTipoProductoIdTipoProducto(idTipoProducto));
    }

    if (productos.isEmpty()) {
        throw new NoSuchElementException("El producto buscado no está registrado en el inventario");
    }

    return productos.stream()
            .filter(p -> canAccessProducto(requesterId, roles, p))
            // 🔹 Usamos ProductoDTO en lugar de ProductoSelectorDTO
            .map(p -> new ProductoDTO(p, sumarCantidadInventario(p)))
            .collect(Collectors.toList());
}
///Santiago Montenegro Historia 17
///SANTIAGO MONTENEGRO RUALES MODIFICAR PRODUCTO FIN
/// 


        /**
     * Buscar productos para selector/autocomplete respetando visibilidad.
     *
     * @param requesterId id del usuario que hace la petición (extraer desde Authentication)
     * @param roles lista de roles del requester (ej. "ROLE_ADMIN", "ROLE_EMPLEADO")
     * @param q término de búsqueda (id o texto)
     * @param limit cantidad máxima de resultados
     * @return lista de ProductoSelectorDTO
     */
    @Transactional(readOnly = true)
    public List<com.es.backendbuddyfinv.dto.ProductoSelectorDTO> buscarParaSelector(Long requesterId, List<String> roles, String q, int limit) {
        if (q == null || q.trim().isEmpty()) return Collections.emptyList();
        q = q.trim();
        boolean byId = q.matches("^\\d+$");

        // Búsqueda por id exacto
        if (byId) {
            Long id = Long.valueOf(q);
            // Usa el método que trae relaciones para evitar N+1
            Optional<Producto> opt = productoRepository.findWithRelationsById(id);
            if (opt.isEmpty()) return Collections.emptyList();
            Producto p = opt.get();
            if (!canAccessProducto(requesterId, roles, p)) return Collections.emptyList();
            Integer cantidad = sumarCantidadInventario(p);
            return List.of(mapToSelectorDto(p, cantidad));
        }

        // Búsqueda por texto: obtenemos primero los ids (optimización), luego los productos con relaciones
        List<Long> ids;
        try {
            ids = productoRepository.searchIdsByQ(q, limit);
        } catch (Exception ex) {
            // Si la consulta native falla, devolvemos vacío para no romper la UI
            logger.warn("searchIdsByQ falló para q='{}': {}", q, ex.getMessage());
            return Collections.emptyList();
        }

        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        List<Producto> productos = productoRepository.findWithRelationsByIds(ids);
        if (productos == null || productos.isEmpty()) return Collections.emptyList();

        // Map para acceder por id rápidamente y mantener el orden original de ids
        Map<Long, Producto> productosById = productos.stream()
                .collect(Collectors.toMap(Producto::getIdProducto, p -> p));

        List<com.es.backendbuddyfinv.dto.ProductoSelectorDTO> result = new ArrayList<>();
        for (Long id : ids) {
            Producto p = productosById.get(id);
            if (p == null) continue;
            if (!canAccessProducto(requesterId, roles, p)) continue;
            Integer cantidad = sumarCantidadInventario(p);
            result.add(mapToSelectorDto(p, cantidad));
            if (result.size() >= limit) break;
        }
        return result;
    }

    // Método auxiliar: mapea Producto + cantidad a ProductoSelectorDTO
    private com.es.backendbuddyfinv.dto.ProductoSelectorDTO mapToSelectorDto(Producto p, Integer cantidadDisponible) {
        if (p == null) return null;
        Long id = p.getIdProducto();
        String nombre = p.getNombre();
        Double precio = p.getPrecio(); // si cambias a BigDecimal adapta aquí
        return new com.es.backendbuddyfinv.dto.ProductoSelectorDTO(id, nombre, cantidadDisponible == null ? 0 : cantidadDisponible, precio);
    }

    // Reusa la lógica de sumar inventario que ya tenías en la propuesta anterior
    private Integer sumarCantidadInventario(Producto p) {
        List<com.es.backendbuddyfinv.model.DetalleInventario> detalles = p.getDetalleInventarios();
        if (detalles == null || detalles.isEmpty()) return 0;
        return detalles.stream()
                .map(di -> {
                    try {
                        Integer v = di.getCantidadDisponible(); // intenta ambos nombres por compatibilidad
                        return v == null ? 0 : v;
                    } catch (Exception ex) {
                        return 0;
                    }
                })
                .reduce(0, Integer::sum);
    }

    // Reusa/ajusta la lógica de visibilidad; chequea ROLE_ADMIN, ROLE_EMPLEADO y propietario
    private boolean canAccessProducto(Long requesterId, List<String> roles, Producto p) {
        if (p == null || p.getPropietario() == null) return false;

        Long propietarioId;
        try {
            // intenta getters comunes
            propietarioId = p.getPropietario().getId();
        } catch (Exception ex) {
            try {
                propietarioId = p.getPropietario().getId();
            } catch (Exception e) {
                return false;
            }
        }

        if (roles != null && roles.stream().anyMatch(r -> r.equalsIgnoreCase("ROLE_ADMIN") || r.equalsIgnoreCase("ADMIN"))) {
            return true;
        }

        if (roles != null && roles.stream().anyMatch(r -> r.equalsIgnoreCase("ROLE_EMPLEADO") || r.equalsIgnoreCase("EMPLEADO"))) {
            // Intentamos obtener el admin asociado al empleado desde usuarioRepository.
            // Se asume que Usuario tiene un campo administrador o similar; adaptarlo si tu modelo es distinto.
            try {
                Optional<com.es.backendbuddyfinv.model.Usuario> optEmpleado = usuarioRepository.findById(requesterId);
                if (optEmpleado.isPresent()) {
                    com.es.backendbuddyfinv.model.Usuario empleado = optEmpleado.get();
                    // intenta varios nombres de getter para el admin (getAdministrador, getUsuarioAdmin, getAdmin)
                    Long adminId = null;
                    try { adminId = empleado.getAdministrador().getId(); } catch (Exception ignored) {}
                    if (adminId == null) {
                        try { adminId = empleado.getAdministrador().getId(); } catch (Exception ignored) {}
                    }
                    if (adminId == null) {
                        // si no hay relación explícita, podrías usar un campo como getIdAdministrador si existe
                        try { adminId = (Long) usuarioRepository.getClass().getMethod("findAdminIdByEmpleadoId", Long.class).invoke(usuarioRepository, requesterId); } catch (Exception ignored) {}
                    }
                    return adminId != null && adminId.equals(propietarioId);
                }
            } catch (Exception ex) {
                logger.warn("No se pudo verificar admin del empleado {}: {}", requesterId, ex.getMessage());
                return false;
            }
        }

        return Objects.equals(propietarioId, requesterId);
    }


}