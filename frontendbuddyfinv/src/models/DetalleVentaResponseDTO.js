export default class DetalleVentaResponseDTO {
  constructor({ idDetalleVenta = null, productoId = null, productoNombre = '', cantidad = 0, precioUnitario = 0, subtotal = 0 } = {}) {
    this.idDetalleVenta = idDetalleVenta;
    this.productoId = productoId;
    this.productoNombre = productoNombre;
    this.cantidad = cantidad;
    this.precioUnitario = precioUnitario;
    this.subtotal = subtotal;
  }

  static fromApi(obj = {}) {
    return new DetalleVentaResponseDTO({
      idDetalleVenta: obj.idDetalleVenta,
      productoId: obj.productoId,
      productoNombre: obj.productoNombre,
      cantidad: obj.cantidad,
      precioUnitario: obj.precioUnitario,
      subtotal: obj.subtotal
    });
  }
}