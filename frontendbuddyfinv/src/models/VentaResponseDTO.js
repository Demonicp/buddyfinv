import DetalleVentaResponseDTO from './DetalleVentaResponseDTO';

export default class VentaResponseDTO {
  constructor({ idVenta = null, cliente = '', fecha = null, total = 0, detalles = [] } = {}) {
    this.idVenta = idVenta;
    this.cliente = cliente;
    this.fecha = fecha; // string ISO por defecto
    this.total = total;
    this.detalles = detalles.map(d => d instanceof DetalleVentaResponseDTO ? d : DetalleVentaResponseDTO.fromApi(d));
  }

  static fromApi(obj = {}) {
    return new VentaResponseDTO({
      idVenta: obj.idVenta,
      cliente: obj.cliente,
      fecha: obj.fecha,
      total: obj.total,
      detalles: (obj.detalles || []).map(DetalleVentaResponseDTO.fromApi)
    });
  }

  // helper para mostrar fecha en UI (puedes adaptar formato)
  getFormattedDate() {
    if (!this.fecha) return '';
    return new Date(this.fecha).toLocaleString();
  }
}