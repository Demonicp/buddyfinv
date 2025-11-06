package com.es.backendbuddyfinv.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.dto.DetalleProductoDTO;
import com.es.backendbuddyfinv.dto.VentaDetalladaDTO;
import com.es.backendbuddyfinv.model.Venta;
import com.es.backendbuddyfinv.repository.VentaRepository;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    public List<VentaDetalladaDTO> listarVentasDetalladas(Long idPropietario) {
        List<Venta> ventas = ventaRepository.findVentasDetalladasByPropietarioId(idPropietario);
    
        return ventas.stream().map(v -> {
            List<DetalleProductoDTO> productos = v.getDetalleVentas().stream()
                .map(dv -> new DetalleProductoDTO(
                    dv.getProducto().getNombre(),
                    dv.getCantidad(),
                    dv.getSubtotal(),
                    dv.getProducto().getEstadoProducto().getObservacion(),
                    dv.getProducto().getPrecio()
                ))
                .collect(Collectors.toList());
    
            return new VentaDetalladaDTO(
                v.getIdVenta(),
                v.getFecha(),
                v.getTotal(),
                v.getEstadoVenta().getObservacion(),
                v.getMetodoPago().getDescripcion(),
                v.getUsuario().getNombre(),
                productos
            );
        }).collect(Collectors.toList());
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
}
