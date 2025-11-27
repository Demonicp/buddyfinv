export class IngresoDTO {
    constructor({
      idIngreso = null,
      fecha = null,
      totalDiario = 0,
      totalFacturas = 0,
      nombreEmpleado = ''
    } = {}) {
      this.idIngreso = idIngreso;
      this.fecha = fecha;
      this.totalDiario = totalDiario;
      this.totalFacturas = totalFacturas;
      this.nombreEmpleado = nombreEmpleado;
    }
  }