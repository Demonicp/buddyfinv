export default class VentaCrearDTO {
  constructor({ cliente = '', metodoPagoId = null, estadoVentaId = null, detalles = [] } = {}) {
    this.cliente = cliente;
    this.metodoPagoId = metodoPagoId;
    this.estadoVentaId = estadoVentaId;
    this.detalles = detalles; // array de DetalleVentaCrearDTO-like: { productoId, cantidad }
  }

  isValid() {
    if (!this.cliente || !this.cliente.trim()) return false;
    if (!this.metodoPagoId) return false;
    if (!this.estadoVentaId) return false;
    if (!Array.isArray(this.detalles) || this.detalles.length === 0) return false;
    for (const d of this.detalles) {
      if (!d.productoId) return false;
      if (!Number.isInteger(d.cantidad) || d.cantidad <= 0) return false;
    }
    return true;
  }
}