export default class ProductoSummary {
  constructor({ idProducto = null, nombre = '', precioUnitario = 0, stock = null } = {}) {
    this.idProducto = idProducto;
    this.nombre = nombre;
    this.precioUnitario = precioUnitario;
    this.stock = stock;
  }

  static fromApi(obj = {}) {
    return new ProductoSummary({
      idProducto: obj.idProducto || obj.id,
      nombre: obj.nombre,
      precioUnitario: obj.precioUnitario || obj.precio,
      stock: obj.stock
    });
  }
}