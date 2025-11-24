export class EstadoVentaSummary {
  constructor({ idEstadoVenta = null, observacion = '' } = {}) {
    this.idEstadoVenta = idEstadoVenta;
    this.observacion = observacion;
  }
}
