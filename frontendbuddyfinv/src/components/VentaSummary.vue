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
.venta-summary {
  border: 1px solid #e6e6e6;
  padding: 12px;
  border-radius: 6px;
  background: #fff;
  max-width: 820px;
  margin: 12px auto;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}
.meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 12px;
  font-size: 0.95rem;
}
.detalles table {
  width: 100%;
  border-collapse: collapse;
}
.detalles th,
.detalles td {
  padding: 8px;
  border-bottom: 1px solid #f2f2f2;
}
.right {
  text-align: right;
}
.nombre {
  max-width: 420px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}
button {
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #cfcfcf;
  background: #fff;
  cursor: pointer;
}
button:hover {
  background: #f7f7f7;
}
.empty {
  color: #666;
  padding: 8px 0;
}
</style>