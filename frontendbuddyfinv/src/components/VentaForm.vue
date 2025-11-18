<template>
  <div class="venta-form">


    <form @submit.prevent="submit">
      <div class="field">
        <label>Nombre Cliente</label>
        <input type="text" v-model="ventaCrear.cliente" />
      </div>

      <div class="field">
        <label>Método de pago</label>
        <select v-model="ventaCrear.metodoPagoId">
          <option value="" disabled>Selecciona método de pago</option>
          <option v-for="m in metodosPago" :key="m.id" :value="m.id">{{ m.nombre }}</option>
        </select>
      </div>

      <div class="field">
        <label>Estado venta</label>
        <select v-model="ventaCrear.estadoVentaId">
          <option value="" disabled>Selecciona estado</option>
          <option v-for="s in estadosVenta" :key="s.id" :value="s.id">{{ s.nombre }}</option>
        </select>
      </div>

      <!-- Autocomplete / búsqueda de productos -->
      <div class="field">
        <label>Buscar producto</label>
        <input
          type="search"
          v-model="productoQuery"
          @input="onQueryInput"
          placeholder="Por nombre o id..."
          autocomplete="off"
        />
        <ul v-if="showSuggestions" class="suggestions">
          <li v-for="p in suggestions" :key="p.id">
            <button type="button" @click="addProducto(p)">{{ p.nombre }} <small class="muted">— id: {{ p.id }}</small></button>
          </li>
          <li v-if="suggestions.length === 0" class="no-results">No se encontraron productos</li>
        </ul>
      </div>

      <!-- Lista de detalles seleccionados (productos de la venta) -->
      <div class="detalles">
        <h4>Productos en la venta</h4>
        <div v-if="detallesSeleccionados.length === 0" class="muted">No hay productos seleccionados</div>

        <div class="detalle-row" v-for="(d, idx) in detallesSeleccionados" :key="d.productoId">
          <div class="detalle-info">
            <strong>{{ d.nombreProducto || d.productoId }}</strong>
            <div class="muted">{{ d.descripcion || '' }}</div>
          </div>

          <div class="detalle-cantidad">
            <input type="number" min="1" v-model.number="d.cantidad" @change="onUpdateCantidad({ index: idx, cantidad: d.cantidad })" />
          </div>

          <div class="detalle-actions">
            <button type="button" class="btn-link" @click="onRemoveDetalle(idx)">Eliminar</button>
          </div>
        </div>
      </div>

      <div class="actions">
        <button type="submit" :disabled="loading">
          <span v-if="loading">Creando...</span>
          <span v-else>Crear Venta</span>
        </button>
      </div>

      <div v-if="error" class="error">{{ error }}</div>
    </form>

    <venta-summary v-if="ventaCreada" :venta="ventaCreada" />
  </div>
</template>

<script>
import VentaCrearDTO from '@/models/VentaCrearDTO'
import DetalleVentaCrearDTO from '@/models/DetalleVentaCrearDTO'
import VentaResponseDTO from '@/models/VentaResponseDTO'
import { VentaProvider } from '@/providers/VentaProvider'
import { ProductoProvider } from '@/providers/ProductoProvider'
import ProductosSelector from '@/components/ProductosSelector.vue'
import VentaSummary from '@/components/VentaSummary.vue'

export default {
  name: 'VentaForm',
  components: { ProductosSelector, VentaSummary },
  data() {
    return {
      ventaCrear: new VentaCrearDTO(),
      detallesSeleccionados: [],
      productos: [], // catálogo cargado inicialmente
      metodosPago: [], // [{id, nombre}]
      estadosVenta: [], // [{id, nombre}]
      productoQuery: '',
      suggestions: [],
      loading: false,
      error: null,
      ventaCreada: null,
      searchTimer: null
    }
  },
  created() {
    this.loadProductos()
    this.loadMetodosPago()
    this.loadEstadosVenta()
  },
  methods: {
    // Carga inicial de productos (para selector local)
    async loadProductos() {
      try {
        const data = await ProductoProvider.listarParaSelector()
        this.productos = Array.isArray(data) ? data : []
      } catch (e) {
        // fallback: dejar array vacío pero no bloquear la UI
        console.error('Error cargando productos:', e)
        this.productos = []
      }
    },

    // Intenta cargar métodos de pago desde provider, si no existe usa fallback
    async loadMetodosPago() {
      if (typeof ProductoProvider.listarMetodosPago === 'function') {
        try {
          this.metodosPago = await ProductoProvider.listarMetodosPago()
          return
        } catch (e) {
          console.warn('listarMetodosPago falló, usando fallback', e)
        }
      }
      // fallback local (ajusta los ids/nombres según tu sistema)
      this.metodosPago = [
        { id: 1, nombre: 'Efectivo' },
        { id: 2, nombre: 'Tarjeta' },
        { id: 3, nombre: 'Transferencia' }
      ]
    },

    // Carga posibles estados de venta
    async loadEstadosVenta() {
      if (typeof ProductoProvider.listarEstadosVenta === 'function') {
        try {
          this.estadosVenta = await ProductoProvider.listarEstadosVenta()
          return
        } catch (e) {
          console.warn('listarEstadosVenta falló, usando fallback', e)
        }
      }
      this.estadosVenta = [
        { id: 1, nombre: 'Pendiente' },
        { id: 2, nombre: 'Confirmada' },
        { id: 3, nombre: 'Cancelada' }
      ]
    },

    // Debounce simple para no lanzar búsquedas a cada tecla
    onQueryInput() {
      clearTimeout(this.searchTimer)
      const term = this.productoQuery && this.productoQuery.trim()
      if (!term) {
        this.suggestions = []
        return
      }
      this.searchTimer = setTimeout(() => {
        this.searchProductos(term)
      }, 250)
    },

    // Busca productos: intenta provider.buscar(term) si existe, si no filtra localmente
    async searchProductos(term) {
      const byId = /^\d+$/.test(term)
      try {
        if (typeof ProductoProvider.buscar === 'function') {
          const res = await ProductoProvider.buscar(term)
          this.suggestions = Array.isArray(res) ? res : []
          return
        }
      } catch (e) {
        console.warn('ProductoProvider.buscar falló, usando filtrado local', e)
      }

      // Filtrado local: por id exacto o por nombre contiene
      const q = term.toLowerCase()
      this.suggestions = this.productos.filter(p => {
        if (byId) return String(p.id) === term
        return (p.nombre || '').toLowerCase().includes(q) || (String(p.id) || '').includes(q)
      }).slice(0, 12)
    },

    // Añadir producto a detalles (si ya existe incrementa cantidad)
    addProducto(producto) {
      const existe = this.detallesSeleccionados.find(d => d.productoId === producto.id)
      if (existe) {
        existe.cantidad = Number(existe.cantidad) + 1
      } else {
        const nuevo = new DetalleVentaCrearDTO({
          productoId: producto.id,
          cantidad: 1,
          nombreProducto: producto.nombre,
          descripcion: producto.descripcion
        })
        this.detallesSeleccionados.push(nuevo)
      }
      // limpiar sugerencias y query para UX
      this.productoQuery = ''
      this.suggestions = []
    },

    onAddDetalleById(productoId) {
      // compatibilidad con emision desde ProductosSelector
      const producto = this.productos.find(p => p.id === productoId)
      if (producto) this.addProducto(producto)
      else {
        // si no está en catálogo, añadir con id y cantidad 1
        this.detallesSeleccionados.push(new DetalleVentaCrearDTO({ productoId, cantidad: 1 }))
      }
    },

    onRemoveDetalle(index) {
      this.detallesSeleccionados.splice(index, 1)
    },

    onUpdateCantidad({ index, cantidad }) {
      const val = Number(cantidad) || 0
      if (val <= 0) this.onRemoveDetalle(index)
      else this.detallesSeleccionados[index].cantidad = val
    },

    onDetallesChange(canonical) {
      this.detallesSeleccionados = canonical.map(d => new DetalleVentaCrearDTO(d))
    },

    async submit() {
      this.error = null
      this.ventaCrear.detalles = this.detallesSeleccionados.map(d => ({
        productoId: d.productoId,
        cantidad: d.cantidad
      }))

      if (!this.ventaCrear.isValid()) {
        this.error = 'Revisa los campos obligatorios y las cantidades'
        return
      }

      this.loading = true
      try {
        const resp = await VentaProvider.crearVenta(this.ventaCrear)
        this.ventaCreada = VentaResponseDTO.fromApi(resp)
        if (this.ventaCreada && this.ventaCreada.idVenta) {
          this.$emit('created', this.ventaCreada.idVenta)
        } else if (resp && (resp.idVenta || resp.id)) {
          this.$emit('created', resp.idVenta || resp.id)
        }
        this.ventaCrear = new VentaCrearDTO()
        this.detallesSeleccionados = []
      } catch (e) {
        if (e && e.status === 400 && e.body) {
          this.error = typeof e.body === 'string' ? e.body : (e.body.message || JSON.stringify(e.body))
        } else if (e && e.status === 401) {
          this.error = 'No autorizado. Inicia sesión.'
        } else {
          this.error = (e && e.message) || 'Error al crear la venta'
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>

.h3 {

    display: flex;
    justify-content: center;
    align-items: center;

}
/* estilos mínimos para que la UI sea usable; los dejaremos para pulir al final */
.field { margin-bottom: 1rem; }
.field label { display:block; font-weight:600; margin-bottom:0.25rem; }
.field input[type="text"], .field input[type="search"], .field input[type="number"], .field select { width:100%; padding:0.5rem; border:1px solid #ddd; border-radius:6px; }
.suggestions { list-style:none; margin:0.5rem 0 0; padding:0; border:1px solid #eee; border-radius:6px; max-height:220px; overflow:auto; background:#fff; }
.suggestions li { padding:0.35rem 0.5rem; border-bottom:1px solid #f5f5f5; }
.suggestions li button { border:0; background:transparent; width:100%; text-align:left; padding:0; cursor:pointer; }
.no-results { padding:0.5rem; color:#888; }
.detalles { margin-top:1rem; }
.detalle-row { display:flex; gap:0.75rem; align-items:center; padding:0.5rem 0; border-bottom:1px dashed #f2f2f2; }
.detalle-info { flex:1; }
.detalle-cantidad input { width:80px; padding:0.35rem; }
.btn-link { background:transparent; border:0; color:#c33; cursor:pointer; }
.muted { color:#777; font-size:0.9rem; }
.actions { margin-top:1rem; }
.actions button { padding:0.6rem 1rem; border-radius:6px; background:#2b8aef; color:#fff; border:0; cursor:pointer; }
.error { color:#b00020; margin-top:0.75rem; }
</style>

<style scoped>
.venta-form { max-width: 820px; margin: 12px auto; padding: 16px; border: 1px solid #e6e6e6; border-radius: 6px; background: #fff; }
.field { margin-bottom: 10px; display:flex; flex-direction:column; }
.field label { font-size: .85rem; margin-bottom:4px; color:#333; }
.field input { padding:8px; border:1px solid #ccc; border-radius:4px; }
.actions { margin-top:12px; }
button[disabled] { opacity:.6; cursor:not-allowed; }
.error { margin-top:10px; color:#b00020; }
</style>