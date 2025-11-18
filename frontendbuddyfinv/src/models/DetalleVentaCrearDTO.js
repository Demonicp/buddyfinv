export default class DetalleVentaCrearDTO {
  constructor({ productoId = null, cantidad = 1 } = {}) {
    this.productoId = productoId;
    this.cantidad = cantidad;
  }
}