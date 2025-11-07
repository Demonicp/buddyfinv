export function crearVentaDTO(data) {
  return {
    idVenta: data.idVenta,
    fecha: data.fecha,
    total: data.total,
    metodoPago: data.metodoPago,
    estadoVenta: data.estadoVenta,
    usuario: data.usuario.empleado,
    productos: data.productos.map(producto => ({
      nombre: producto.nombre,
      cantidad: producto.cantidad,
      precio: producto.precio,
      subtotal: producto.subtotal,
      estadoProducto: producto.estadoProducto
    }))
  }
}
