<template>
  <div class="venta-form">
    <form @submit.prevent="submit" novalidate>
      <!-- Nombre Cliente -->
      <div class="field">
        <label>Nombre Cliente</label>
        <input
          type="text"
          v-model="ventaCrear.cliente"
          @input="onClienteInput; clearFieldError('cliente')"
          :class="{ invalid: fieldErrors.cliente }"
          aria-invalid="fieldErrors.cliente ? 'true' : 'false'"
          aria-describedby="cliente-help"
        />
        <div id="cliente-help" class="helper" v-if="fieldErrors.cliente">
          {{ typeof fieldErrors.cliente === 'string' ? fieldErrors.cliente : 'Valor inválido' }}
        </div>
      </div>

      <!-- Método de pago -->
      <div class="field">
        <label>Método de pago</label>
        <select
          v-model="ventaCrear.metodoPagoId"
          @change="clearFieldError('metodoPagoId')"
          :class="{ invalid: fieldErrors.metodoPagoId }"
          aria-invalid="fieldErrors.metodoPagoId ? 'true' : 'false'"
        >
          <option value="" disabled>Selecciona método de pago</option>
          <option v-for="m in metodosPago" :key="m.id" :value="m.id">{{ m.nombre }}</option>
        </select>
        <div class="helper" v-if="fieldErrors.metodoPagoId">{{ fieldErrors.metodoPagoId }}</div>
      </div>

      <!-- Estado venta -->
      <div class="field">
        <label>Estado venta</label>
        <select
          v-model="ventaCrear.estadoVentaId"
          @change="clearFieldError('estadoVentaId')"
          :class="{ invalid: fieldErrors.estadoVentaId }"
          aria-invalid="fieldErrors.estadoVentaId ? 'true' : 'false'"
        >
          <option value="" disabled>Selecciona estado</option>
          <option v-for="s in estadosVenta" :key="s.id" :value="s.id">{{ s.nombre }}</option>
        </select>
        <div class="helper" v-if="fieldErrors.estadoVentaId">{{ fieldErrors.estadoVentaId }}</div>
      </div>

      <!-- Selector de productos -->
      <div class="field">
        <label>Buscar producto</label>
        <ProductoSelector
          v-model="selectedProducto"
          :placeholder="'Buscar producto por nombre o id...'"
          @select="(p) => { onProductoSelect(p); clearFieldError('detalles') }"
        />
        <div class="helper" v-if="fieldErrors.detalles">{{ fieldErrors.detalles }}</div>
      </div>

      <!-- Lista de productos seleccionados -->
      <div class="detalles">
        <h4>Productos en la venta</h4>
        <div v-if="detallesSeleccionados.length === 0" class="muted">No hay productos seleccionados</div>

        <div
          class="detalle-row"
          v-for="(d, idx) in detallesSeleccionados"
          :key="d.productoId || idx"
        >
          <div class="detalle-info">
            <strong>{{ d.nombreProducto || d.productoId }}</strong>
            <div class="muted">{{ d.descripcion || '' }}</div>
            <div class="helper" v-if="fieldErrors[`producto_${idx}`]">{{ fieldErrors[`producto_${idx}`] }}</div>
          </div>

          <div class="detalle-cantidad">
            <input
              type="number"
              min="1"
              v-model.number="d.cantidad"
              @input="clearFieldError(`cantidad_${idx}`)"
              @change="onUpdateCantidad({ index: idx, cantidad: d.cantidad })"
              :class="{ invalid: fieldErrors[`cantidad_${idx}`] }"
              :aria-invalid="fieldErrors[`cantidad_${idx}`] ? 'true' : 'false'"
            />
            <div class="helper" v-if="fieldErrors[`cantidad_${idx}`]">{{ fieldErrors[`cantidad_${idx}`] }}</div>
          </div>

          <div class="detalle-actions">
            <button type="button" class="btn-link" @click="onRemoveDetalle(idx)">Eliminar</button>
          </div>
        </div>
      </div>

      <!-- Acciones: abrir modal de confirmación (simulación previa) -->
      <div class="actions">
        <button type="button" class="btn-secondary" @click="onCancel" :disabled="loading">Cancelar</button>
        <button type="button" @click="openConfirmModal" :disabled="loading" class="btn-primary">
          <span v-if="loading">Procesando...</span>
          <span v-else>Crear Venta</span>
        </button>
      </div>


      <div v-if="error" class="error" role="alert">{{ error }}</div>
    </form>

    <!-- Modal de confirmación con preview (usa previewItems, previewSubtotal, previewTotal) -->
    <div v-if="showConfirmModal" class="modal-backdrop" role="dialog" aria-modal="true">
      <div class="modal" role="document">
        <header class="modal-header">
          <h3 v-if="!ventaCreada">Confirmar venta</h3>
          <h3 v-else>Venta creada</h3>
        </header>

        <section class="modal-body">
          <!-- PREVIEW: antes de crear -->
          <div v-if="!ventaCreada">
            <p><strong>Cliente:</strong> {{ ventaCrear.cliente }}</p>
            <p><strong>Método de pago:</strong> {{ metodoPagoNombre }}</p>
            <p><strong>Estado:</strong> {{ estadoVentaNombre }}</p>
            <p v-if="attendantId || ventaCrear.atendidoPorId"><strong>Atendido por (ID):</strong> {{ ventaCrear.atendidoPorId || attendantId }}</p>

            <h4></h4>
            <div v-if="previewItems.length === 0" class="muted">No hay productos para previsualizar</div>

            <table v-else class="confirm-table">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Cantidad</th>
                  <th>Precio unitario</th>
                  <th>Subtotal</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(it, idx) in previewItems" :key="it.productoId || idx">
                  <td class="td-name">{{ it.nombre }}</td>
                  <td class="td-center">{{ it.cantidad }}</td>
                  <td class="td-right">{{ formatPrecio(it.precioUnitario ?? 0) }}</td>
                  <td class="td-right">{{ formatPrecio(it.subtotal ?? 0) }}</td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="3" class="td-total-label">Suma subtotales</td>
                  <td class="td-right">{{ formatPrecio(previewSubtotal) }}</td>
                </tr>
                <tr>
                  <td colspan="3" class="td-total-label">Total a pagar</td>
                  <td class="td-right td-total">{{ formatPrecio(previewTotal) }}</td>
                </tr>
              </tfoot>
            </table>

            <p class="muted" style="margin-top:8px">
             
            </p>
          </div>

          <!-- CREATED: después de crear la venta -->
          <div v-else>
            <p><strong>ID Venta:</strong> {{ ventaCreada.idVenta || ventaCreada.id }}</p>
            <p><strong>Cliente:</strong> {{ ventaCreada.cliente || ventaCrear.cliente }}</p>
            <p><strong>Fecha:</strong> {{ ventaCreada.fecha || ventaCreada.createdAt || ventaCreada.fechaCreacion }}</p>
            <p v-if="ventaCreada.atendidoPorId || attendantId"><strong>Atendido por :</strong> {{ ventaCreada.atendidoPorId || attendantId }}</p>
            <p><strong>Total:</strong> {{ formatPrecio(ventaCreada.total || previewTotal) }}</p>

            <h4>Productos</h4>
            <table class="confirm-table">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Cantidad</th>
                  <th>Precio unitario</th>
                  <th>Subtotal</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(d, idx) in ventaCreada.detalles || previewItems" :key="d.productoId || d.id || idx">
                  <td class="td-name">{{ d.nombreProducto || d.nombre || d.productoNombre || d.productoId }}</td>
                  <td class="td-center">{{ d.cantidad }}</td>
                  <td class="td-right">{{ formatPrecio(d.precioUnitario ?? d.precio ?? 0) }}</td>
                  <td class="td-right">{{ formatPrecio((d.precioUnitario ?? d.precio ?? 0) * (d.cantidad || 0)) }}</td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="3" class="td-total-label">Total</td>
                  <td class="td-right td-total">{{ formatPrecio(ventaCreada.total || previewTotal) }}</td>
                </tr>
              </tfoot>
            </table>
          </div>

          <div v-if="confirmError" class="error" style="margin-top:12px">{{ confirmError }}</div>
        </section>

        <footer class="modal-footer">
          <template v-if="!ventaCreada">
            <button type="button" class="btn-secondary" @click="cancelConfirm" :disabled="confirmLoading">Cancelar</button>
            <button type="button" class="btn-primary" @click="confirmCreate" :disabled="confirmLoading">
              <span v-if="confirmLoading">Confirmando...</span>
              <span v-else>Confirmar</span>
            </button>
          </template>

          <template v-else>
            <button type="button" class="btn-primary" @click="closeAfterCreated">Cerrar</button>
          </template>
        </footer>
      </div>
    </div>

    <!-- Resumen final (opcional) -->
    <div class="tableresumen">
    <venta-summary v-if="ventaCreada" :venta="ventaCreada" />
    </div>
  </div>
</template>

<script>
import VentaCrearDTO from '@/models/VentaCrearDTO'
import DetalleVentaCrearDTO from '@/models/DetalleVentaCrearDTO'
import VentaResponseDTO from '@/models/VentaResponseDTO'
import { VentaProvider } from '@/providers/VentaProvider'
import { ProductoProvider } from '@/providers/ProductoProvider'
import { fetchMetodosPago } from '@/providers/MetodoPagoProvider'
import { fetchEstadosVenta } from '@/providers/EstadoVentaProvider'
import ProductoSelector from '@/components/ProductoSelector.vue'
import VentaSummary from '@/components/VentaSummary.vue'

/**
 * Decodifica el payload de un JWT (base64url) y devuelve el id del usuario si existe.
 * Intenta devolver payload.idUsuario, payload.id o null.
 */
function getUserIdFromToken() {
  try {
    const token = localStorage.getItem('token')
    if (!token) return null
    const parts = token.split('.')
    if (parts.length < 2) return null
    const payloadBase64 = parts[1]
    // Normalizar base64url a base64 estándar
    const normalized = payloadBase64.replace(/-/g, '+').replace(/_/g, '/')
    // Añadir padding si hace falta
    const pad = normalized.length % 4
    const padded = pad ? normalized + '='.repeat(4 - pad) : normalized
    const json = atob(padded)
    const payload = JSON.parse(json)
    return payload.idUsuario ?? payload.id ?? null
  } catch (error) {
    // No loguear en producción; útil en desarrollo
    // console.error('Error al decodificar token:', error)
    return null
  }
}

export default {
  name: 'VentaForm',
  components: { ProductoSelector, VentaSummary },
  data() {
    return {
      ventaCrear: new VentaCrearDTO(),
      detallesSeleccionados: [],
      productos: [],
      metodosPago: [],
      estadosVenta: [],
      selectedProducto: null,

      // UI / errores
      loading: false,
      error: null,
      fieldErrors: {},

      // modal / confirmación
      showConfirmModal: false,
      confirmLoading: false,
      confirmError: null,
      ventaCreada: null,

      // preview calculada consultando productos por id
      previewItems: [],        // [{ productoId, nombre, cantidad, precioUnitario, subtotal, error? }]
      previewSubtotal: 0,
      previewTotal: 0,

      // cache local para evitar múltiples fetchs del mismo producto
      _productoCache: {},

      // id del usuario/atendedor actual (se muestra en preview y en la venta creada)
      attendantId: null
    }
  },
  created() {
    this.loadProductos()
    this.loadMetodosPago()
    this.loadEstadosVenta()

    // obtener id del token y asignar al estado y al DTO inicial
    this.attendantId = getUserIdFromToken()
    if (this.attendantId) {
      try { this.ventaCrear.atendidoPorId = this.attendantId } catch (e) { /* ignore */ }
    }
  },
  computed: {
    metodoPagoNombre() {
      const m = this.metodosPago.find(x => x.id === this.ventaCrear.metodoPagoId)
      return m ? m.nombre : ''
    },
    estadoVentaNombre() {
      const s = this.estadosVenta.find(x => x.id === this.ventaCrear.estadoVentaId)
      return s ? s.nombre : ''
    }
  },
  methods: {
    /* ---------- helpers de errores ---------- */
    setFieldError(field, message = true) {
      this.fieldErrors = { ...this.fieldErrors, [field]: message }
    },
    clearFieldError(field) {
      if (!this.fieldErrors || !Object.prototype.hasOwnProperty.call(this.fieldErrors, field)) return
      const { [field]: _, ...rest } = this.fieldErrors
      this.fieldErrors = rest
    },
    clearAllFieldErrors() {
      this.fieldErrors = {}
    },
    onCancel() {
      const hasData =
        (this.ventaCrear && (this.ventaCrear.cliente && String(this.ventaCrear.cliente).trim() !== '')) ||
        (Array.isArray(this.detallesSeleccionados) && this.detallesSeleccionados.length > 0)

      if (hasData) {
        const ok = window.confirm('Hay datos sin guardar. ¿Seguro que quieres cancelar y volver a Acciones?')
        if (!ok) return
      }

      // Intentamos navegar por nombre de ruta 'acciones' (según tu router)
      try {
        if (this.$router && this.$router.push) {
          // preferimos ir a la ruta padre 'acciones'
          this.$router.push({ name: 'acciones' }).catch(() => {
            // si falla (por ejemplo ya estamos ahí), intentamos volver en el historial
            if (this.$router && this.$router.back) this.$router.back()
          })
        } else {
          // fallback: history.back()
          window.history.back()
        }
      } catch (e) {
        // fallback final
        window.history.back()
      }
    },

    /* ---------- cargas iniciales ---------- */
    async loadProductos() {
      try {
        // listarParaSelector espera q; pasamos q vacío para evitar resultados no deseados
        const data = await ProductoProvider.listarParaSelector({ q: '' })
        this.productos = Array.isArray(data) ? data : []
      } catch (e) {
        console.error('Error cargando productos:', e)
        this.productos = []
      }
    },

    async loadMetodosPago() {
      try {
        const token = localStorage.getItem('token') || null
        const data = await fetchMetodosPago(token)
        this.metodosPago = (data || []).map(item => ({
          id: item.id ?? item.idMetodoPago ?? item.id_metodo_pago,
          nombre: item.nombre ?? item.descripcion ?? item.descripcionMetodoPago ?? item.nombreMetodoPago
        }))
      } catch (e) {
        console.error('Error cargando métodos de pago:', e)
        this.metodosPago = [
          { id: 1, nombre: 'Efectivo' },
          { id: 2, nombre: 'Tarjeta' },
          { id: 3, nombre: 'Transferencia' }
        ]
      }
    },

    async loadEstadosVenta() {
      try {
        const token = localStorage.getItem('token') || null
        const data = await fetchEstadosVenta(token)
        this.estadosVenta = (data || []).map(item => ({
          id: item.id ?? item.idEstadoVenta ?? item.id_estado_venta,
          nombre: item.nombre ?? item.observacion ?? item.descripcion
        }))
      } catch (e) {
        console.error('Error cargando estados de venta:', e)
        this.estadosVenta = [
          { id: 1, nombre: 'Pendiente' },
          { id: 2, nombre: 'Confirmada' },
          { id: 3, nombre: 'Cancelada' }
        ]
      }
    },

    /* ---------- selector de productos ---------- */
    onProductoSelect(producto) {
      if (!producto) return
      const existe = this.detallesSeleccionados.find(d => d.productoId === producto.id)
      if (existe) {
        existe.cantidad = Number(existe.cantidad || 0) + 1
      } else {
        const precioFromProducto = producto.precio ?? producto.precioUnitario ?? producto.precio_unitario ?? 0
        const nuevo = new DetalleVentaCrearDTO({
          productoId: producto.id,
          cantidad: 1,
          nombreProducto: producto.nombre,
          descripcion: producto.descripcion ?? '',
          precioUnitario: Number(precioFromProducto || 0)
        })
        this.detallesSeleccionados.push(nuevo)
      }
      this.selectedProducto = null
      this.clearFieldError('detalles')
    },

    addProducto(producto) {
      this.onProductoSelect(producto)
    },

    onAddDetalleById(productoId) {
      const producto = this.productos.find(p => p.id === productoId)
      if (producto) this.onProductoSelect(producto)
      else {
        this.detallesSeleccionados.push(new DetalleVentaCrearDTO({ productoId, cantidad: 1 }))
        this.clearFieldError('detalles')
      }
    },

    onRemoveDetalle(index) {
      this.clearFieldError(`cantidad_${index}`)
      this.detallesSeleccionados.splice(index, 1)
      if (this.detallesSeleccionados.length === 0) {
        this.setFieldError('detalles', 'Agrega al menos un producto a la venta')
      } else {
        const newErrors = {}
        Object.keys(this.fieldErrors).forEach(k => {
          if (!k.startsWith('cantidad_')) newErrors[k] = this.fieldErrors[k]
        })
        this.fieldErrors = newErrors
      }
    },

    onUpdateCantidad({ index, cantidad }) {
      const val = Number(cantidad) || 0
      if (val <= 0) {
        this.onRemoveDetalle(index)
      } else {
        if (this.detallesSeleccionados[index]) {
          this.detallesSeleccionados[index].cantidad = val
        }
        this.clearFieldError(`cantidad_${index}`)
      }
    },

    onDetallesChange(canonical) {
      this.detallesSeleccionados = canonical.map(d => new DetalleVentaCrearDTO(d))
      if (this.detallesSeleccionados.length > 0) this.clearFieldError('detalles')
    },

    /* ---------- input cliente (filtrado) ---------- */
    onClienteInput() {
      if (!this.ventaCrear) return
      const raw = String(this.ventaCrear.cliente || '')
      const filtered = raw.replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñ\s'\-]/g, '')
      const compact = filtered.replace(/\s+/g, ' ')
      if (compact !== raw) this.ventaCrear.cliente = compact
      this.clearFieldError('cliente')
    },

    /* ---------- formato de moneda ---------- */
    formatPrecio(value) {
      const n = Number(value || 0)
      try {
        return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(n)
      } catch {
        return `$${n.toFixed(0)}`
      }
    },

    /* ---------- simulación: consultar productos por id y calcular subtotales ---------- */
    async simulateVentaPreview() {
      this.previewItems = []
      this.previewSubtotal = 0
      this.previewTotal = 0
      this.confirmError = null

      if (!this.detallesSeleccionados || this.detallesSeleccionados.length === 0) {
        return
      }

      const promises = this.detallesSeleccionados.map(async d => {
        const productoId = d.productoId
        let productoData = this._productoCache[productoId]

        if (!productoData) {
          try {
            productoData = await ProductoProvider.buscarPorId(productoId)
            this._productoCache[productoId] = productoData
          } catch (err) {
            return {
              productoId,
              nombre: `Producto ${productoId} (no disponible)`,
              cantidad: Number(d.cantidad || 0),
              precioUnitario: 0,
              subtotal: 0,
              error: true,
              errorObj: err
            }
          }
        }

        const precioUnitario = Number(
          productoData.precio ??
          productoData.precioUnitario ??
          productoData.precio_unitario ??
          productoData.price ??
          0
        ) || 0

        const nombre = productoData.nombre ?? productoData.nombreProducto ?? productoData.title ?? `Producto ${productoId}`
        const cantidad = Number(d.cantidad || 0)
        const subtotal = precioUnitario * cantidad

        return { productoId, nombre, cantidad, precioUnitario, subtotal }
      })

      const items = await Promise.all(promises)

      this.previewItems = items
      this.previewSubtotal = items.reduce((s, it) => s + (Number(it.subtotal || 0)), 0)
      this.previewTotal = this.previewSubtotal

      const anyError = items.some(i => i.error)
      if (anyError) {
        this.confirmError = 'Algunos productos no pudieron consultarse; revisa la lista.'
      } else {
        this.confirmError = null
      }
    },

    /* ---------- flujo modal: simular antes de mostrar (usa id desde token) ---------- */
    async openConfirmModal() {
      this.error = null
      this.clearAllFieldErrors()

      const nombre = String(this.ventaCrear.cliente || '').trim()
      if (!nombre) {
        this.setFieldError('cliente', 'El nombre del cliente es obligatorio')
        return
      }
      if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s'\-]+$/.test(nombre)) {
        this.setFieldError('cliente', 'El nombre del cliente contiene caracteres no válidos')
        return
      }
      if (this.ventaCrear.metodoPagoId == null || this.ventaCrear.metodoPagoId === '') {
        this.setFieldError('metodoPagoId', 'Selecciona un método de pago válido')
        return
      }
      if (this.ventaCrear.estadoVentaId == null || this.ventaCrear.estadoVentaId === '') {
        this.setFieldError('estadoVentaId', 'Selecciona un estado de venta válido')
        return
      }
      if (!this.detallesSeleccionados || this.detallesSeleccionados.length === 0) {
        this.setFieldError('detalles', 'Agrega al menos un producto a la venta')
        return
      }

      // refrescar attendantId desde el token por si cambió
      this.attendantId = getUserIdFromToken() ?? this.attendantId
      if (this.attendantId) {
        try { this.ventaCrear.atendidoPorId = this.attendantId } catch (e) { /* ignore */ }
      }

      try {
        await this.simulateVentaPreview()
        this.showConfirmModal = true
      } catch (err) {
        console.error('Error simulando venta:', err)
        this.confirmError = 'No se pudo calcular la vista previa. Intenta de nuevo.'
      }
    },

    cancelConfirm() {
      this.showConfirmModal = false
      this.confirmError = null
    },

    closeAfterCreated() {
      this.showConfirmModal = false
      this.confirmError = null
    },

    /* ---------- reset del formulario para iniciar nueva venta (mantener attendantId) ---------- */
    resetFormAfterCreate() {
      this.ventaCrear = new VentaCrearDTO()
      // reasignar el id del atendedor a la nueva instancia para la siguiente venta
      if (this.attendantId) {
        try { this.ventaCrear.atendidoPorId = this.attendantId } catch (e) { /* ignore */ }
      }
      this.detallesSeleccionados = []
      this.previewItems = []
      this.previewSubtotal = 0
      this.previewTotal = 0
      this.fieldErrors = {}
      this.selectedProducto = null
    },

    /* ---------- crear venta: antes de enviar, rellenar detalles con precioUnitario desde la preview ---------- */
    async confirmCreate() {
      this.confirmError = null
      this.confirmLoading = true

      try {
        // refrescar y asignar id del atendedor justo antes de enviar
        this.attendantId = getUserIdFromToken() ?? this.attendantId
        if (this.attendantId) {
          try { this.ventaCrear.atendidoPorId = this.attendantId } catch (e) { /* ignore */ }
        }

        // preparar detalles en ventaCrear usando previewItems para asegurar precios
        this.ventaCrear.detalles = (this.previewItems && this.previewItems.length)
          ? this.previewItems.map(pi => ({
              productoId: pi.productoId,
              cantidad: pi.cantidad,
              precioUnitario: Number(pi.precioUnitario || 0),
              nombreProducto: pi.nombre
            }))
          : this.detallesSeleccionados.map(d => ({
              productoId: d.productoId,
              cantidad: d.cantidad,
              precioUnitario: Number(d.precioUnitario ?? d.precio ?? 0),
              nombreProducto: d.nombreProducto
            }))

        // validación final si tu DTO tiene isValid
        if (this.ventaCrear.isValid && typeof this.ventaCrear.isValid === 'function') {
          if (!this.ventaCrear.isValid()) {
            this.confirmError = 'Revisa los campos obligatorios y las cantidades'
            this.confirmLoading = false
            return
          }
        }

        // enviar al backend
        const resp = await VentaProvider.crearVenta(this.ventaCrear)
        this.ventaCreada = VentaResponseDTO.fromApi ? VentaResponseDTO.fromApi(resp) : resp

        // si backend no devuelve detalles, rellenar desde la request
        if (!this.ventaCreada.detalles || !this.ventaCreada.detalles.length) {
          this.ventaCreada.detalles = (this.ventaCrear.detalles || []).map((d, i) => ({
            productoId: d.productoId,
            cantidad: d.cantidad,
            nombreProducto: this.previewItems[i]?.nombre || d.nombreProducto || '',
            precioUnitario: d.precioUnitario
          }))
          this.ventaCreada.total = this.ventaCreada.detalles.reduce((s, it) => s + (Number(it.precioUnitario || 0) * Number(it.cantidad || 0)), 0)
        }

        // incluir id del atendedor en la respuesta mostrada si no viene del backend
        if (!this.ventaCreada.atendidoPorId && this.attendantId) {
          try { this.ventaCreada.atendidoPorId = this.attendantId } catch (e) { /* ignore */ }
        }

        if (this.ventaCreada && (this.ventaCreada.idVenta || this.ventaCreada.id)) {
          this.$emit('created', this.ventaCreada.idVenta || this.ventaCreada.id)
        }

        // limpiar formulario para iniciar nueva venta, pero mantener ventaCreada para mostrar resumen
        this.resetFormAfterCreate()
      } catch (e) {
        try {
          if (e && e.body) {
            if (typeof e.body === 'object' && !Array.isArray(e.body)) {
              Object.keys(e.body).forEach(k => {
                const val = e.body[k]
                this.setFieldError(k, typeof val === 'string' ? val : (val && val.message) || true)
              })
              if (!Object.keys(this.fieldErrors).length) {
                this.confirmError = 'Error al crear la venta'
              }
            } else if (Array.isArray(e.body)) {
              e.body.forEach(it => {
                if (it && it.field) this.setFieldError(it.field, it.message || true)
              })
              if (!Object.keys(this.fieldErrors).length) {
                this.confirmError = 'Error al crear la venta'
              }
            } else if (typeof e.body === 'string') {
              this.confirmError = e.body
            } else {
              this.confirmError = (e && e.message) || 'Error al crear la venta'
            }
          } else if (e && e.status === 401) {
            this.confirmError = 'No autorizado. Inicia sesión.'
          } else {
            this.confirmError = (e && e.message) || 'Error al crear la venta'
          }
        } catch (parseErr) {
          this.confirmError = (e && e.message) || 'Error al crear la venta'
        }
      } finally {
        this.confirmLoading = false
      }
    }
  }
}
</script>

<style scoped>
/* ---------------------------
   Base layout / fuente
   --------------------------- */
.venta-form,
.venta-wrapper {
  width: 100%;
  max-width: 100vw;

  font-family: 'Segoe UI', system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
  color: #111; /* texto principal en negro */
  box-sizing: border-box;
}

/* Card visual (fondo suave, borde naranja claro) */
.venta-card,
.venta-form {
  background: #fffaf3;
  padding: 20px;
  border-radius: 14px;
  box-shadow: inset 0 0 0 2px #f8c471;
  border: 1px solid #f5cba7;
}

/* Título (si lo usas) */
.venta-title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 18px;
  font-size: 1.8rem;
  font-weight: 700;
}

/* ---------------------------
   Botones (estética naranja / contrastes)
   --------------------------- */
.btn,
button.btn,
button.btn-primary,
.btn-primary {
  padding: 8px 16px;
  border: none;
  border-radius: 10px;
  font-weight:bold; /* negrita fuerte */
  font-size: large;
  cursor: pointer;
  transition: background-color 0.18s ease, transform 0.12s ease;
  letter-spacing: 0.3px;

}



/* Primario: naranja degradado */
.btn-primary,
button.btn-primary {
  background: linear-gradient(180deg,#f39c12,#e67e22);
  
  color: #fff;
  box-shadow: 0 6px 18px rgba(230,126,34,0.12);
}
.btn-primary:hover { transform: translateY(-1px); }

/* Secundario: borde naranja claro */
.btn-secondary,
button.btn-secondary {
  padding: 8px 16px;
  background: transparent;
  border: 1px solid #f5cba7;
  color: #4d2c0c;
  border-radius: 10px;
  letter-spacing: 0.3px;
  font-weight:bold; /* negrita fuerte */
  font-size: large;
  cursor: pointer;
  
}

/* Link / eliminar */
.btn-link,
button.btn-link {
  background: transparent;
  border: 0;
  color: #e67e22;
  font-weight: 700;
}

/* Botones de acción alternativos */
.btn.limpiar { background-color: #e74c3c; color: #fff; border-radius: 18px; }
.btn.consultar { background-color: #3498db; color: #fff; border-radius: 18px; }

/* ---------------------------
   Form fields
   --------------------------- */
.field {
  margin-bottom: 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.field label {
  font-weight: 700;
  font-size: 0.95rem;
  color: #111;
}
.field input[type="text"],
.field input[type="number"],
.field select,
.field textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #f5cba7;
  border-radius: 10px;
  background: #fff;
  font-size: 0.95rem;
  transition: border-color .12s ease, box-shadow .12s ease;
  box-sizing: border-box;
  color: #111;
}
.field input:focus,
.field select:focus,
.field textarea:focus {
  outline: none;
  border-color: #e67e22;
  box-shadow: 0 0 0 6px rgba(246, 187, 66, 0.08);
}
.field input.invalid,
.field select.invalid,
.field textarea.invalid {
  border-color: #d32f2f;
  box-shadow: 0 0 0 6px rgba(211,47,47,0.06);
}

/* Helper / errores */
.helper {
  color: #c0392b;
  font-size: 0.88rem;
  margin-top: 6px;
}

/* ---------------------------
   Lista de detalles / filas
   --------------------------- */
.detalles {
  margin-top: 12px;
}
.detalle-row {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border-radius: 10px;
  background: linear-gradient(180deg,#fff,#fffaf3);
  border: 1px solid #f5e0c8;
  margin-bottom: 10px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.03);
}
.detalle-info { flex: 1; min-width: 0; }
.detalle-info strong { display: block; font-weight: 800; color: #111; }
.detalle-info .muted { color: #666; font-size: 0.92rem; }
.detalle-cantidad input {
  width: 96px;
  padding: 8px;
  border-radius: 8px;
  border: 1px solid #f5cba7;
  font-size: 0.95rem;
  color: #111;
}
.detalle-actions { white-space: nowrap; }

/* ---------------------------
   Scroll personalizado
   --------------------------- */
.venta-scroll {
  max-height: 520px;
  overflow-y: auto;
  padding-right: 8px;
  scrollbar-color: #f8c471 transparent;
  scrollbar-width: thin;
}
.venta-scroll::-webkit-scrollbar { width: 8px; }
.venta-scroll::-webkit-scrollbar-track { background: transparent; }
.venta-scroll::-webkit-scrollbar-thumb {
  background-color: #f8c471;
  border-radius: 6px;
  border: 2px solid transparent;
  background-clip: content-box;
}

/* ---------------------------
   Tablas: estilo general inspirado en tu CSS
   --------------------------- */
.detalle-table,
.confirm-table,
.venta-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-top: 12px;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(0,0,0,0.04);
  font-family: inherit;
}

/* Encabezado */
.detalle-table thead th,
.confirm-table thead th,
.venta-table .table-header {
  background: #f8c471;
  color: #4d2c0c;
  padding: 12px 14px;
  text-align: left;
  font-size: 13px;
  font-weight: 800;
}

/* Celdas */
.detalle-table td,
.confirm-table td,
.venta-table .table-row > * {
  padding: 12px 14px;
  font-size: 13px;
  color: #111;
  border-top: 1px solid #f0e6d6;
}

/* Filas alternas y hover */
.venta-table .table-row,
.confirm-table tbody tr {
  background: #fdf6ec;
  transition: background .12s ease, transform .08s ease;
}
.venta-table .table-row:nth-child(even),
.confirm-table tbody tr:nth-child(even) {
  background: #faebd7;
}
.venta-table .table-row:hover,
.confirm-table tbody tr:hover {
  background: #f5cba7;
  transform: translateY(-2px);
}

/* Alineaciones */
.td-right { text-align: right; }
.td-center { text-align: center; }
.td-name {
  max-width: 420px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Totales */
.td-total-label { text-align: right; font-weight: 800; padding-right: 12px; color: #111; }
.td-total { font-weight: 900; color: #e67e22; }

/* ---------------------------
   Modal (encapsulado con bordes naranjas)
   --------------------------- */
.modal-backdrop {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1200;
  background: rgba(0,0,0,0.36);
  -webkit-backdrop-filter: blur(6px);
  backdrop-filter: blur(6px);
  padding: 20px;
}
.modal {
  width: 100%;
  max-width: 860px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  border: 1px solid #f5cba7;
  box-shadow: 0 30px 60px rgba(0,0,0,0.18);
}
.modal-header { padding: 14px 18px; border-bottom: 1px solid #f0e6d6; }
.modal-header h3 { margin: 0; font-size: 1.05rem; color: #111; }
.modal-body { padding: 16px 18px; max-height: 68vh; overflow: auto; }
.modal-footer { padding: 12px 18px; border-top: 1px solid #f0e6d6; display: flex; gap: 10px; justify-content: flex-end; }

/* ---------------------------
   Mensajes y estados
   --------------------------- */
.muted { color: #777; font-size: 0.92rem; }
.error { color: #b00020; margin-top: 10px; font-weight: 700; }

/* ---------------------------
   Animaciones y transiciones
   --------------------------- */
.fade-enter-active, .fade-leave-active { transition: opacity 0.28s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.tableresumen {
  width: 100vw;
  


}



/* ---------------------------
   Responsive
   --------------------------- */
@media (max-width: 720px) {
  .detalle-row { flex-direction: column; align-items: flex-start; gap: 8px; }
  .detalle-cantidad input { width: 100%; }
  .modal { max-width: 96%; }
}

/* Quitar flechas de input number en webkit */
.field input::-webkit-outer-spin-button,
.field input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}





</style>