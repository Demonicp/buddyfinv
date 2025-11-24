export class ProductoReabastecerDTO {
    constructor(data) {
      this.idProducto = data.idProducto
      this.nombre = data.nombre
      this.precio= data.precio
      this.idEstadoProducto= data.idEstadoProducto === 1? "activo":"inactivo"
      this.cantidad=data.cantidad
    }               
}

         