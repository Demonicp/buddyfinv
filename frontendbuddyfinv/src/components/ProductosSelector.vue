<template>
  <div class="productos-selector">
    <div class="selector-header">
      <div class="search">
        <input
          type="search"
          v-model="q"
          @input="onFilter"
          placeholder="Buscar producto por nombre o id"
        />
      </div>
      <div class="controls">
        <label>
          Mostrar:
          <select v-model.number="perPage" @change="resetPage">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
          </select>
        </label>
      </div>
    </div>

    <table class="productos-table">
      <thead>
        <tr>
          <th>Id</th>
          <th>Nombre</th>
          <th class="right">Precio</th>
          <th class="right">Stock</th>
          <th class="actions">Cantidad</th>
          <th class="actions">Acción</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="paginated.length === 0">
          <td colspan="6" class="empty">No hay productos</td>
        </tr>

        <tr v-for="(p, idx) in paginated" :key="p.idProducto || p.id || idx">
          <td>{{ p.idProducto || p.id }}</td>
          <td class="nombre">{{ p.nombre }}</td>
          <td class="right">{{ formatMoney(p.precioUnitario || p.precio || 0) }}</td>
          <td class="right">{{ p.stock == null ? '—' : p.stock }}</td>

          <td class="actions">
            <input
              type="number"
              min="1"
              :max="p.stock || undefined"
              v-model.number="localQuantities[pKey(p)]"
              @change="onQtyChange(p)"
            />
          </td>

          <td class="actions">
            <button
              :disabled="!canAdd(p)"
              @click="handleAdd(p)"
            >
              Añadir
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="pager" v-if="pages > 1">
      <button @click="prevPage" :disabled="page === 1">«</button>
      <span>Pagina {{ page }} / {{ pages }}</span>
      <button @click="nextPage" :disabled="page === pages">»</button>
    </div>

    <div class="seleccion">
      <h4>Detalles seleccionados</h4>
      <div v-if="detallesLocal.length === 0" class="empty">No hay detalles seleccionados</div>

      <ul v-else class="detalles-list">
        <li v-for="(d, i) in detallesLocal" :key="d.productoId">
          <span class="nombre">
            {{ getProductoNombre(d.productoId) || ('Producto ' + d.productoId) }}
          </span>
          <input type="number" min="1" v-model.number="d.cantidad" @change="emitUpdate(i, d.cantidad)" />
          <button @click="emitRemove(i)">Quitar</button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductosSelector',
  props: {
    // lista de productos (puede ser array de ProductoDTO o raw)
    productos: {
      type: Array,
      default: () => []
    },
    // detalles canonicos mantenidos por el padre [{ productoId, cantidad }]
    detalles: {
      type: Array,
      default: () => []
    }
  },
  emits: ['add', 'remove', 'update', 'change'],
  data() {
    return {
      q: '',
      page: 1,
      perPage: 10,
      localQuantities: {}, // map productoKey -> cantidad (controls inputs)
      detallesLocal: [] // copia local para edición rápida (sin romper el prop)
    }
  },
  computed: {
    filtered() {
      const term = (this.q || '').trim().toLowerCase()
      if (!term) return this.productos
      return this.productos.filter(p => {
        const id = String(p.idProducto || p.id || '')
        const nombre = String(p.nombre || '').toLowerCase()
        return id.includes(term) || nombre.includes(term)
      })
    },
    pages() {
      return Math.max(1, Math.ceil(this.filtered.length / this.perPage))
    },
    paginated() {
      const start = (this.page - 1) * this.perPage
      return this.filtered.slice(start, start + this.perPage)
    }
  },
  watch: {
    productos: { handler: 'syncQuantities', immediate: true },
    detalles: { handler(det) { this.syncDetalles(det) }, immediate: true }
  },
  mounted() {
    this.syncQuantities()
    this.syncDetalles(this.detalles)
  },
  methods: {
    pKey(p) {
      return String(p.idProducto || p.id || '')
    },
    resetPage() {
      this.page = 1
    },
    onFilter() {
      this.resetPage()
    },
    prevPage() {
      if (this.page > 1) this.page--
    },
    nextPage() {
      if (this.page < this.pages) this.page++
    },
    syncQuantities() {
      // initialize localQuantities for inputs using existing detalles or default 1
      const map = {}
      for (const p of this.productos) {
        const key = this.pKey(p)
        const existing = this.detalles.find(d => String(d.productoId) === key)
        map[key] = existing ? existing.cantidad : 1
      }
      this.localQuantities = map
    },
    syncDetalles(detalles) {
      // keep a shallow mutable copy for UI edits; parent remains canonical source
      this.detallesLocal = Array.isArray(detalles) ? detalles.map(d => ({ ...d })) : []
    },
    canAdd(p) {
      const key = this.pKey(p)
      const qty = Number(this.localQuantities[key]) || 0
      // cannot add if qty < 1 or stock insufficient (when stock available)
      if (qty < 1) return false
      if (p.stock != null && qty > p.stock) return false
      return true
    },
    handleAdd(p) {
      const key = this.pKey(p)
      const qty = Number(this.localQuantities[key]) || 1
      // if it's already in detallesLocal/in prop, emit update instead of add
      const idx = this.detallesLocal.findIndex(d => String(d.productoId) === key)
      if (idx >= 0) {
        this.detallesLocal[idx].cantidad = this.detallesLocal[idx].cantidad + qty
        this.$emit('update', { index: idx, cantidad: this.detallesLocal[idx].cantidad })
        this.emitChange()
        return
      }
      const payload = { productoId: Number(key), cantidad: qty }
      this.detallesLocal.push(payload)
      this.$emit('add', payload.productoId)
      // also emit change with full detalles to keep parent synchronized
      this.emitChange()
    },
    onQtyChange(p) {
      const key = this.pKey(p)
      const qty = Number(this.localQuantities[key]) || 1
      // if product is already selected, update that detalle's cantidad
      const idx = this.detallesLocal.findIndex(d => String(d.productoId) === key)
      if (idx >= 0) {
        this.detallesLocal[idx].cantidad = qty
        this.$emit('update', { index: idx, cantidad: qty })
        this.emitChange()
      }
    },
    emitUpdate(index, cantidad) {
      // update detallesLocal and inform parent
      if (index < 0 || index >= this.detallesLocal.length) return
      this.detallesLocal[index].cantidad = Number(cantidad) || 0
      this.$emit('update', { index, cantidad: this.detallesLocal[index].cantidad })
      this.emitChange()
    },
    emitRemove(index) {
      if (index < 0 || index >= this.detallesLocal.length) return
      const removed = this.detallesLocal.splice(index, 1)[0]
      this.$emit('remove', index)
      this.emitChange()
      return removed
    },
    emitChange() {
      // mirror canonical detalles array shape for parent convenience
      const canonical = this.detallesLocal.map(d => ({ productoId: d.productoId, cantidad: d.cantidad }))
      this.$emit('change', canonical)
    },
    formatMoney(v) {
      return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(v)
    },
    getProductoNombre(productoId) {
      const p = this.productos.find(x => String(x.idProducto || x.id || '') === String(productoId))
      return p ? p.nombre : null
    }
  }
}
</script>

<style scoped>
.productos-selector {
  border: 1px solid #e8e8e8;
  padding: 12px;
  border-radius: 6px;
  background: #fff;
}
.selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.selector-header .search input {
  width: 320px;
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.productos-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 8px;
}
.productos-table th,
.productos-table td {
  padding: 8px;
  border-bottom: 1px solid #f0f0f0;
}
.productos-table th.right,
.productos-table td.right {
  text-align: right;
}
.productos-table th.actions,
.productos-table td.actions {
  width: 120px;
  text-align: center;
}
.productos-table .nombre {
  max-width: 380px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.button[disabled],
button[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
}
.pager {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 12px;
}
.seleccion {
  margin-top: 12px;
}
.detalles-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.detalles-list li {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px dashed #f2f2f2;
}
.empty {
  color: #666;
  padding: 12px 0;
}
</style>