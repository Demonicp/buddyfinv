<template>
  <div class="venta-summary">
    <header class="header">
      <h3>Venta creada</h3>
      <div class="meta">
        <div><strong>ID:</strong> {{ ventaObj.idVenta || '—' }}</div>
        <div><strong>Cliente:</strong> {{ ventaObj.cliente || '—' }}</div>
        <div><strong>Fecha:</strong> {{ formattedDate }}</div>
        <div><strong>Total:</strong> {{ formatMoney(ventaObj.total || 0) }}</div>
      </div>
    </header>

    <section class="detalles" v-if="Array.isArray(ventaObj.detalles) && ventaObj.detalles.length">
      <table>
        <thead>
          <tr>
            <th>Producto</th>
            <th class="right">Cantidad</th>
            <th class="right">Precio unitario</th>
            <th class="right">Subtotal</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in ventaObj.detalles" :key="d.idDetalleVenta || `${d.productoId}-${d.productoNombre}`">
            <td class="nombre">{{ d.productoNombre || ('Producto ' + (d.productoId || '—')) }}</td>
            <td class="right">{{ d.cantidad }}</td>
            <td class="right">{{ formatMoney(d.precioUnitario || 0) }}</td>
            <td class="right">{{ formatMoney(d.subtotal || 0) }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <div v-else class="empty">No hay detalles para mostrar</div>

    <div class="actions">
      <button @click="onCopyLink">Copiar enlace</button>
      <button @click="$emit('done')">Aceptar</button>
    </div>
  </div>
</template>

<script>
import VentaResponseDTO from '@/models/VentaResponseDTO'

export default {
  name: 'VentaSummary',
  props: {
    // acepta tanto instancia de model como objeto raw desde API
    venta: {
      type: [Object],
      required: true
    }
  },
  computed: {
    ventaObj() {
      // si ya es instancia (tiene getFormattedDate) o fue creado con fromApi, úsala;
      // de lo contrario, normalizamos desde el objeto recibido
      try {
        if (this.venta && typeof this.venta.getFormattedDate === 'function') {
          return this.venta
        }
        return VentaResponseDTO.fromApi(this.venta || {})
      } catch (e) {
        return this.venta || {}
      }
    },
    formattedDate() {
      return this.ventaObj.getFormattedDate ? this.ventaObj.getFormattedDate() : (this.ventaObj.fecha ? new Date(this.ventaObj.fecha).toLocaleString() : '—')
    }
  },
  methods: {
    formatMoney(v) {
      return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(Number(v) || 0)
    },
    onCopyLink() {
      const id = this.ventaObj.idVenta
      if (!id) return
      const base = window.location.origin
      const url = `${base}/ventas/${id}`
      navigator.clipboard?.writeText(url).then(() => {
        // opcional: emitir evento para mostrar toast
        this.$emit('copied', url)
      }).catch(() => {
        this.$emit('copy-failed')
      })
    }
  }
}
</script>

<style scoped>
/* Contenedor principal */
.venta-summary {
  width: 78vw;


  padding: 18px;
  background: linear-gradient(180deg, #fffaf3 0%, #fff 100%);
  border-radius: 14px;
  border: 1px solid #f5cba7;
  box-shadow: 0 10px 30px rgba(0,0,0,0.06);
  font-family: 'Segoe UI', system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
  color: #111; /* texto principal en negro */
  box-sizing: border-box;
}

/* Header: título y meta */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 14px;
}
.header h3 {
  margin: 0;
  color: #e67e22; /* naranja */
  font-size: 1.25rem;
  font-weight: 800;
  letter-spacing: 0.2px;
}

/* Meta info (ID, cliente, fecha, total) */
.meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 12px;
  font-size: 0.95rem;
  color: #111;
}
.meta div strong {
  font-weight: 800;
  color: #111;
  margin-right: 6px;
}

/* Detalles: tabla con estilo naranja y bordes redondeados */
.detalles table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-top: 8px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
  font-family: inherit;
}

/* Encabezado de tabla */
.detalles thead th {
  background: linear-gradient(180deg,#f8c471,#f5cba7);
  color: #4d2c0c;
  padding: 12px 14px;
  text-align: left;
  font-size: 13px;
  font-weight: 800;
  border-bottom: 1px solid rgba(0,0,0,0.04);
}

/* Celdas */
.detalles tbody td {
  padding: 12px 14px;
  font-size: 13px;
  color: #111;
  border-top: 1px solid #f0e6d6;
  background: linear-gradient(180deg,#fff,#fffaf3);
}

/* Alineaciones y truncado */
.right { text-align: right; }
.nombre {
  max-width: 420px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 700;
  color: #111;
}

/* Filas alternas y hover */
.detalles tbody tr {
  transition: background .12s ease, transform .06s ease;
}
.detalles tbody tr:nth-child(even) td {
  background: #fbf0dc;
}
.detalles tbody tr:hover td {
  background: #f5cba7;
  transform: translateY(-2px);
}

/* Totales y énfasis */
.detalles tfoot td {
  font-weight: 900;
  color: #e67e22;
}

/* Acciones: botones con estilo naranja y secundario */
.actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
  justify-content: flex-end;
}
.actions button {
  padding: 8px 14px;
  border-radius: 12px;
  border: 1px solid #f5cba7;
  background: #fff;
  color: #111;
  font-weight: 700;
  cursor: pointer;
  transition: background .12s ease, transform .08s ease;
  box-shadow: 0 6px 18px rgba(230,126,34,0.06);
}
.actions button:hover { transform: translateY(-2px); }

/* Botón primario (Aceptar) */
.actions button:last-child {
  background: linear-gradient(180deg,#f39c12,#e67e22);
  color: #fff;
  border: 0;
  box-shadow: 0 10px 24px rgba(230,126,34,0.14);
}

/* Botón secundario (Copiar enlace) */
.actions button:first-child {
  background: transparent;
  color: #e67e22;
  border: 1px solid #f5cba7;
}

/* Empty state */
.empty {
  color: #666;
  padding: 12px 0;
  font-size: 0.95rem;
}

/* Pequeños ajustes responsivos */
@media (max-width: 820px) {
  .venta-summary { width: 94vw; padding: 14px; }
  .meta { grid-template-columns: 1fr; }
  .detalles thead th, .detalles tbody td { padding: 10px; font-size: 13px; }
  .actions { justify-content: center; }
}

/* Quitar flechas de input number si se usan dentro del resumen */
input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>
