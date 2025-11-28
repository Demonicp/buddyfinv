package com.es.backendbuddyfinv.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.es.backendbuddyfinv.dto.DetalleProductoDTO;
import com.es.backendbuddyfinv.dto.DetalleVentaCrearDTO;
import com.es.backendbuddyfinv.dto.DetalleVentaResponseDTO;
import com.es.backendbuddyfinv.dto.VentaCrearDTO;
import com.es.backendbuddyfinv.dto.VentaDetalladaDTO;
import com.es.backendbuddyfinv.dto.VentaResponseDTO;
import com.es.backendbuddyfinv.model.DetalleInventario;
import com.es.backendbuddyfinv.model.DetalleVenta;
import com.es.backendbuddyfinv.model.EstadoVenta;
import com.es.backendbuddyfinv.model.MetodoPago;
import com.es.backendbuddyfinv.model.Producto;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.model.Venta;
import com.es.backendbuddyfinv.repository.DetalleInventarioRepository;
import com.es.backendbuddyfinv.repository.DetalleVentaRepository;
import com.es.backendbuddyfinv.repository.EstadoVentaRepository;
import com.es.backendbuddyfinv.repository.MetodoPagoRepository;
import com.es.backendbuddyfinv.repository.ProductoRepository;
import com.es.backendbuddyfinv.repository.UsuarioRepository;
import com.es.backendbuddyfinv.repository.VentaRepository;
import com.es.backendbuddyfinv.security.CustomUserDetails;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    //repositorios necesarios apra crear venta esteban moreno 

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleInventarioRepository detalleInventarioRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private EstadoVentaRepository estadoVentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;



    public List<VentaDetalladaDTO> listarVentasDetalladas(Long idPropietario) {
        List<Venta> ventas = ventaRepository.findVentasDetalladasByPropietarioId(idPropietario);
    
        return ventas.stream().map(v -> {
            List<DetalleProductoDTO> productos = v.getDetalleVentas().stream()
                .map(dv -> new DetalleProductoDTO(
                    dv.getProducto().getIdProducto(),
                    dv.getProducto().getNombre(),
                    dv.getCantidad(),
                    dv.getSubtotal(),
                    dv.getProducto().getEstadoProducto().getObservacion(),
                    dv.getProducto().getPrecio()
                ))
                .collect(Collectors.toList());
    
            // AGREGADO: incluir id del usuario (empleado) que hizo la venta en el DTO
            return new VentaDetalladaDTO(
                v.getIdVenta(),
                v.getFecha(),
                v.getTotal(),
                v.getEstadoVenta().getObservacion(),
                v.getMetodoPago().getDescripcion(),
                v.getUsuario() != null ? v.getUsuario().getNombre() : null,
                (v.getUsuario() != null && v.getUsuario().getId() != null) ? v.getUsuario().getId() : null, // empleadoId (seguro)
                v.getCliente(),
                productos
            );
        }).collect(Collectors.toList());
    }



    public VentaResponseDTO toResponse(Venta v) {
        VentaResponseDTO r = new VentaResponseDTO();
        r.setIdVenta(v.getIdVenta());
        r.setCliente(v.getCliente());
        r.setFecha(v.getFecha());
        r.setTotal(v.getTotal());

        List<DetalleVenta> detallesEntity = v.getDetalleVentas() == null ? Collections.emptyList() : v.getDetalleVentas();

        List<DetalleVentaResponseDTO> detalles = detallesEntity.stream().map(d -> {
            DetalleVentaResponseDTO dr = new DetalleVentaResponseDTO();
            dr.setIdDetalleVenta(d.getIdDetalleVenta());
            if (d.getProducto() != null) {
                dr.setProductoId(d.getProducto().getIdProducto());
                dr.setProductoNombre(d.getProducto().getNombre());
            }
            dr.setCantidad(d.getCantidad());
            dr.setPrecioUnitario(d.getPrecioUnitario());
            dr.setSubtotal(d.getSubtotal());
            return dr;
        }).collect(Collectors.toList());

        r.setDetalles(detalles);
        return r;
    }










    // Crear una nueva venta
    public Venta createVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    // Obtener todas las ventas
    public List<Venta> getAllVentas() {
        return ventaRepository.findAll();
    }

    // Obtener una venta por ID
    public Optional<Venta> getVentaById(Long id) {
        return ventaRepository.findById(id);
    }

    // Actualizar una venta
    public Venta updateVenta(Long id, Venta ventaDetails) {
        Optional<Venta> optionalVenta = ventaRepository.findById(id);
        if (optionalVenta.isPresent()) {
            Venta venta = optionalVenta.get();
            // Aquí puedes actualizar los campos específicos de la venta
            // venta.setCampo(ventaDetails.getCampo());
            return ventaRepository.save(venta);
        }
        return null;
    }

    // Eliminar una venta
    public boolean deleteVenta(Long id) {
        if (ventaRepository.existsById(id)) {
            ventaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Verificar si existe una venta
    public boolean existsById(Long id) {
        return ventaRepository.existsById(id);
    }


    //implementacion esteban moreno feature crear ventas

    @Transactional
    public Venta registrarVenta(VentaCrearDTO dto, Authentication authentication) {
        // 1. Obtener usuario autenticado
        // --- Verificar autenticación ---
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }

// --- Obtener entidad Usuario de forma robusta ---
        Usuario usuario = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails cud = (CustomUserDetails) principal;
            Long idUsuario = cud.getIdUsuario();
            if (idUsuario != null) {
                usuario = usuarioRepository.findById(idUsuario).orElse(null);
            }
            // fallback por username si no se encontró por id
            if (usuario == null) {
                String username = authentication.getName();
                usuario = usuarioRepository.findByUsuario(username).orElse(null);
            }
        } else {
            // Principal no es CustomUserDetails -> fallback por username
            String username = authentication.getName();
            usuario = usuarioRepository.findByUsuario(username).orElse(null);
        }

        if (usuario == null) {
            throw new IllegalArgumentException("Usuario autenticado no encontrado");
        }

        // 2. Validar campos básicos
        if (dto.getCliente() == null || dto.getCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("Cliente es obligatorio");
        }
        if (dto.getMetodoPagoId() == null) {
            throw new IllegalArgumentException("Método de pago es obligatorio");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(dto.getMetodoPagoId())
                .orElseThrow(() -> new IllegalArgumentException("Método de pago inválido"));

        EstadoVenta estadoVenta = estadoVentaRepository.findById(dto.getEstadoVentaId())
                .orElseThrow(() -> new IllegalArgumentException("Estado de venta inválido"));

        // 3. Preparar Venta
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setCliente(dto.getCliente());
        venta.setMetodoPago(metodoPago);
        venta.setEstadoVenta(estadoVenta);
        venta.setUsuario(usuario);
        venta.setPropietario(usuario.getAdministrador() != null ? usuario.getAdministrador() : usuario);
        venta.setDetalleVentas(new ArrayList<>());

        double total = 0.0;

        // 4. Procesar cada detalle: validar existencia, stock y calcular subtotal
        for (DetalleVentaCrearDTO dDto : dto.getDetalles()) {
            if (dDto.getCantidad() == null || dDto.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para productoId: " + dDto.getProductoId());
            }

            Producto producto = productoRepository.findById(dDto.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + dDto.getProductoId()));

            // Buscar detalle inventario disponible para este producto y propietario
            Optional<DetalleInventario> detalleInvOpt = detalleInventarioRepository
                    .findByProductoIdProductoAndPropietarioId(producto.getIdProducto(), venta.getPropietario().getId());

            // Si el proyecto no trabaja con propietario en inventario, puedes usar findByProductoIdProducto(productoId)
            DetalleInventario detalleInventario = detalleInvOpt.orElseGet(() -> {
                // si no hay registro de inventario para este propietario, intentamos cualquiera con stock
                List<DetalleInventario> lista = detalleInventarioRepository.findByProductoIdProducto(producto.getIdProducto());
                return lista.stream().filter(di -> di.getCantidadDisponible() > 0).findFirst().orElse(null);
            });

            if (detalleInventario == null) {
                throw new IllegalArgumentException("Stock insuficiente. Producto sin inventario disponible: " + producto.getNombre());
            }

            if (detalleInventario.getCantidadDisponible() < dDto.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto '" + producto.getNombre() + "'. Disponibles: " + detalleInventario.getCantidadDisponible());
            }

            double subtotal = producto.getPrecio() * dDto.getCantidad();
            total += subtotal;

            // Crear DetalleVenta (sin persistir aún)
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setCantidad(dDto.getCantidad());
            detalleVenta.setPrecioUnitario(producto.getPrecio());
            detalleVenta.setSubtotal(subtotal);
            detalleVenta.setProducto(producto);
            detalleVenta.setVenta(venta);

            // Añadir a la venta en memoria
            venta.getDetalleVentas().add(detalleVenta);

            // Actualizar inventario en memoria (persistiremos al final por cascada o manual)
            detalleInventario.setCantidadDisponible(detalleInventario.getCantidadDisponible() - dDto.getCantidad());
            detalleInventarioRepository.save(detalleInventario); // guardar cambios de inventario inmediatamente
        }

        venta.setTotal(total);

        // 5. Persistir venta (cascade crea detalles si mapped correctamente)
        Venta ventaGuardada = ventaRepository.save(venta);

        return ventaGuardada;
    }

    /*public List<VentaDetalladaDTO> filtrarVentas(
        Long idPropietario,
        Long idVenta,
        String fechaDesde,
        String fechaHasta,
        Double totalMin,
        Double totalMax,
        String metodoPago
     
    )   { // 1. Convertir fechas
    LocalDateTime desde = null;
    LocalDateTime hasta = null;

    if (fechaDesde != null && !fechaDesde.isEmpty()) {
        desde = LocalDate.parse(fechaDesde).atStartOfDay();
    }
    if (fechaHasta != null && !fechaHasta.isEmpty()) {
        hasta = LocalDate.parse(fechaHasta).atTime(23, 59, 59);
    }

    // 2. Llamar al repository
    List<Venta> ventas = ventaRepository.filtrarVentas(
            idPropietario,
            idVenta,
            desde,
            hasta,
            totalMin,
            totalMax,
            metodoPago
    );

    // 3. Convertir a DTO detallado (igual que tu método listarVentasDetalladas)
    return ventas.stream().map(v -> {

        List<DetalleProductoDTO> productos = v.getDetalleVentas().stream()
                .map(dv -> new DetalleProductoDTO(
                        dv.getProducto().getIdProducto(),
                        dv.getProducto().getNombre(),
                        dv.getCantidad(),
                        dv.getSubtotal(),
                        dv.getProducto().getEstadoProducto().getObservacion(),
                        dv.getProducto().getPrecio()
                )).collect(Collectors.toList());

        return new VentaDetalladaDTO(
                v.getIdVenta(),
                v.getFecha(),
                v.getTotal(),
                v.getEstadoVenta().getObservacion(),
                v.getMetodoPago().getDescripcion(),
                v.getUsuario().getNombre(),
                v.getCliente(),
                productos
        );
    }).collect(Collectors.toList());
}
*/






}
