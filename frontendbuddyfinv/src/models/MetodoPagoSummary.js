export class MetodoPagoSummary {
  constructor({ idMetodoPago = null, descripcion = '' } = {}) {
    this.idMetodoPago = idMetodoPago;
    this.descripcion = descripcion;
  }
}
