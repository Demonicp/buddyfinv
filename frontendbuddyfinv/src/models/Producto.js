export class ProductoDTO {
  constructor(data) {
    this.id = data.id
    this.nombre = data.nombre
    this.precio = data.precio
    this.tipoProducto = data.tipoProducto
    this.estadoProducto = data.estadoProducto
    this.propietario = data.propietario
    this.cantidadDisponible = data.cantidadDisponible // ✅ este campo
  }
}