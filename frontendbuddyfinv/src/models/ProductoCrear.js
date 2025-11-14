export class ProductoCrearDTO{
    
    constructor({
      nombre = "",
      precio = 0,
      tipoProductoId = "",
      estadoProductoId = "",
      cantidadDisponible = 0
    } = {}) {
      this.nombre = nombre
      this.precio = precio
      this.tipoProductoId = tipoProductoId
      this.estadoProductoId = estadoProductoId
      this.cantidadDisponible = cantidadDisponible
    }
  }
