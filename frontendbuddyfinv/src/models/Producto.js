export class ProductoDTO {
  constructor({ id, nombre, precio, tipoProducto, estadoProducto, propietario }) {
    this.id = id;
    this.nombre = nombre;
    this.precio = precio;
    this.tipoProducto = tipoProducto;
    this.estadoProducto = estadoProducto;
    this.propietario = propietario;
  }
}