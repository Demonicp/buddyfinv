export default class DetalleVentaCrearDTO {
  constructor(data = {}) {
    // permitir pasar objeto con varios campos y mantener valores por defecto
    const { productoId = null, cantidad = 1, nombreProducto = null, descripcion = '', precioUnitario = 0 } = data
    this.productoId = productoId
    this.cantidad = cantidad
    this.nombreProducto = nombreProducto
    this.descripcion = descripcion
    this.precioUnitario = Number(precioUnitario || 0)
  }
}