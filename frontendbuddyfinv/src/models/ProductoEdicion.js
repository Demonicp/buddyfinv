export class ProductoEdicionDTO {
    constructor(data) {
      this.idProducto = data.idProducto
      this.nombre = data.nombre
      this.precio = data.precio
      this.idTipoProducto = data.idTipoProducto
      this.idEstadoProducto = data.idEstadoProducto || data.idEstadoPro
    }
  }
  
  