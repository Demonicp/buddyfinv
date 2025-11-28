<template>
  <div class="venta-wrapper">
    <div class="venta-card">
      <h2 class="venta-title">Listado de Ventas</h2>

      <div class="venta-actions">
        <button class="btn limpiar" @click="limpiarFiltros">Limpiar Filtros</button>
        <button class="btn consultar" @click="abrirModalFiltros">Consultar Venta</button>
      </div>

      <!-- Modal de Filtros -->
      <transition name="modal">
        <div v-if="mostrarModalFiltros" class="modal-overlay" @click.self="cerrarModalFiltros">
          <div class="modal-content">
            <div class="modal-header">
              <h3>Filtrar Ventas</h3>
              <button class="btn-cerrar" @click="cerrarModalFiltros">×</button>
            </div>

            <div class="modal-body">
              <div class="form-group">
                <label for="idVenta">ID Venta (Opcional)</label>
                <input
                  id="idVenta"
                  v-model="filtros.idVenta"
                  type="number"
                  placeholder="Ingrese ID de venta"
                />
              </div>

              <div class="form-group">
                <label for="fechaDesde">Fecha Desde (Obligatorio)</label>
                <input
                  id="fechaDesde"
                  v-model="filtros.fechaDesde"
                  type="date"
                  required
                />
              </div>

              <div class="form-group">
                <label for="fechaHasta">Fecha Hasta (Obligatorio)</label>
                <input
                  id="fechaHasta"
                  v-model="filtros.fechaHasta"
                  type="date"
                  required
                />
              </div>

              <div class="form-group">
                <label for="totalMin">Total Mínimo (Opcional)</label>
                <input
                  id="totalMin"
                  v-model="filtros.totalMin"
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="0.00"
                />
              </div>

              <div class="form-group">
                <label for="totalMax">Total Máximo (Opcional)</label>
                <input
                  id="totalMax"
                  v-model="filtros.totalMax"
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="0.00"
                />
              </div>

              <div class="form-group">
                <label for="metodoPago">Método de Pago (Opcional)</label>
                 <select id="metodoPago" v-model="filtros.metodoPago">
                  <option value="">-- Seleccione --</option>
                  <option 
                    v-for="(metodo, index) in metodosPago" 
                    :key="index" 
                    :value="metodo.descripcion">
                    {{ metodo.descripcion }}
                  </option>
                </select>
              </div>

              <div v-if="errorValidacion" class="error-mensaje">
                {{ errorValidacion }}
              </div>
            </div>

            <div class="modal-footer">
              <button class="btn btn-cancelar" @click="cerrarModalFiltros">Cancelar</button>
              <button class="btn btn-aplicar" @click="aplicarFiltros">Aplicar Filtros</button>
            </div>
          </div>
        </div>
      </transition>

      <div class="venta-scroll">
        <div class="venta-table">
          <div class="table-header" role="row">
            <span>ID</span>
            <span>Fecha y Hora</span>
            <span>Total</span>
            <span>Cliente</span>
            <span>Empleado</span>
            <span>ID Emp</span>
          </div>

          <div
            v-for="venta in ventasFiltradas"
            :key="venta.idVenta"
            class="table-row"
            role="row"
            @click="toggleDetalle(venta.idVenta)"
          >
            <span class="cell">{{ venta.idVenta }}</span>
            <span class="cell">{{ new Date(venta.fecha).toLocaleString() }}</span>
            <span class="cell">${{ venta.total.toFixed(2) }}</span>
            <span class="cell">{{ venta.cliente || venta.propietario?.nombre || venta.propietario }}</span>
            <span class="cell">{{ venta.empleado }}</span>
            <span class="cell">{{ venta.empleadoId ?? '—' }}</span>

            <transition name="fade">
              <div v-if="ventaSeleccionada === venta.idVenta" class="detalle-expandido">
                <p><strong>Cliente:</strong> {{ venta.cliente || '—' }}</p>
                <p><strong>Propietario:</strong> {{ venta.propietario?.nombre || venta.propietario }}</p>
                <p><strong>ID Empleado:</strong> {{ venta.empleadoId ?? '—' }}</p>
                <p><strong>Método de pago:</strong> {{ venta.metodoPago }}</p>
                <p><strong>Estado:</strong> {{ venta.estadoVenta }}</p>

                <table class="detalle-table">
                  <thead>
                    <tr>
                      <th>ID Prod</th>
                      <th>Producto</th>
                      <th>Cantidad</th>
                      <th>Precio Unitario</th>
                      <th>Subtotal</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="producto in venta.productos" :key="producto.productoId || producto.nombreProducto">
                      <td>{{ producto.productoId }}</td>
                      <td>{{ producto.nombreProducto }}</td>
                      <td>{{ producto.cantidad }}</td>
                      <td>${{ producto.precioUnitario?.toFixed(2) || '—' }}</td>
                      <td>${{ producto.subtotal.toFixed(2) }}</td>
                      <td>{{ producto.estadoProducto }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </transition>
          </div>
        </div>
      </div>

      <div v-if="ventasFiltradas.length === 0" class="sin-resultados">
        No hay ventas que coincidan con los filtros aplicados.
      </div>
    </div>
  </div>
</template>

<script setup>
import {defineProps, ref, computed, watch} from 'vue'

/* defineProps({
  ventas: Array
})
*/
  

const props = defineProps({
  ventas: Array
})

const ventaSeleccionada = ref(null)
const mostrarModalFiltros = ref(false)
const metodosPago = ref([])
const errorValidacion = ref('')

const filtros = ref({
  idVenta: '',
  fechaDesde: '',
  fechaHasta: '',
  totalMin: '',
  totalMax: '',
  metodoPago: ''
})

const filtrosAplicados = ref({
  idVenta: '',
  fechaDesde: '',
  fechaHasta: '',
  totalMin: '',
  totalMax: '',
  metodoPago: ''
})

// Extraer métodos de pago únicos de las ventas cargadas
watch(
  () => props.ventas,
  (nuevasVentas) => {
    if (nuevasVentas && nuevasVentas.length > 0) {
      const metodosUnicos = [...new Set(nuevasVentas.map(v => v.metodoPago).filter(Boolean))]
      metodosPago.value = metodosUnicos.map(descripcion => ({
        descripcion
      }))
      console.log('✅ Métodos de pago actualizados:', metodosPago.value)
      console.log('Total de métodos únicos:', metodosPago.value.length)
    } else {
      console.warn('⚠️ No hay ventas disponibles')
    }
  },
  { deep: true }
)


// Computed para ventas filtradas
const ventasFiltradas = computed(() => {
  if (!props.ventas) return []

  return props.ventas.filter((venta) => {
    // Si no hay filtros aplicados, mostrar todas
    if (!Object.values(filtrosAplicados.value).some((v) => v !== '' && v !== null)) {
      return true
    }

    // Filtrar por ID de venta
    if (filtrosAplicados.value.idVenta) {
      if (Number(venta.idVenta) !== Number(filtrosAplicados.value.idVenta)) return false
    }

    // Filtrar por fecha desde
    if (filtrosAplicados.value.fechaDesde) {
      const fechaVenta = new Date(venta.fecha)
      const fechaDesde = new Date(filtrosAplicados.value.fechaDesde)
      fechaDesde.setHours(0, 0, 0, 0)
      if (fechaVenta < fechaDesde) return false
    }

    // Filtrar por fecha hasta
    if (filtrosAplicados.value.fechaHasta) {
      const fechaVenta = new Date(venta.fecha)
      const fechaHasta = new Date(filtrosAplicados.value.fechaHasta)
      fechaHasta.setHours(23, 59, 59, 999)
      if (fechaVenta > fechaHasta) return false
    }

    // Filtrar por total mínimo
    if (filtrosAplicados.value.totalMin) {
      if (venta.total < Number(filtrosAplicados.value.totalMin)) return false
    }

    // Filtrar por total máximo
    if (filtrosAplicados.value.totalMax) {
      if (venta.total > Number(filtrosAplicados.value.totalMax)) return false
    }

    // Filtrar por método de pago
    if (filtrosAplicados.value.metodoPago) {
      if (venta.metodoPago !== filtrosAplicados.value.metodoPago) return false
    }

    return true
  })
})

function toggleDetalle(id) {
  ventaSeleccionada.value = ventaSeleccionada.value === id ? null : id
}

function abrirModalFiltros() {
  mostrarModalFiltros.value = true
}

function cerrarModalFiltros() {
  mostrarModalFiltros.value = false
  errorValidacion.value = ''
}

function aplicarFiltros() {
  // Validar fechas obligatorias
  if (!filtros.value.fechaDesde || !filtros.value.fechaHasta) {
    errorValidacion.value = 'Las fechas Desde y Hasta son obligatorias'
    return
  }

  // Validar que fechaDesde no sea mayor que fechaHasta
  const fechaDesde = new Date(filtros.value.fechaDesde)
  const fechaHasta = new Date(filtros.value.fechaHasta)

  if (fechaDesde > fechaHasta) {
    errorValidacion.value = 'La fecha Desde no puede ser mayor que la fecha Hasta'
    return
  }

  // Validar que totalMin no sea mayor que totalMax
  if (filtros.value.totalMin && filtros.value.totalMax) {
    if (Number(filtros.value.totalMin) > Number(filtros.value.totalMax)) {
      errorValidacion.value = 'El total mínimo no puede ser mayor que el total máximo'
      return
    }
  }

  // Aplicar filtros
  filtrosAplicados.value = { ...filtros.value }
  cerrarModalFiltros()
  errorValidacion.value = ''
}

function limpiarFiltros() {
  filtros.value = {
    idVenta: '',
    fechaDesde: '',
    fechaHasta: '',
    totalMin: '',
    totalMax: '',
    metodoPago: ''
  }
  filtrosAplicados.value = {
    idVenta: '',
    fechaDesde: '',
    fechaHasta: '',
    totalMin: '',
    totalMax: '',
    metodoPago: ''
  }
  ventaSeleccionada.value = null
}
</script>

<style scoped>
.venta-wrapper {
  width: 90%;
  max-width: 1200px;
  margin: 40px auto;
  font-family: 'Segoe UI', sans-serif;
}

.venta-card {
  background: #fffaf3;
  padding: 20px;
  border-radius: 16px;
  box-shadow: inset 0 0 0 2px #f8c471;
  border: 1px solid #f5cba7;
}

.venta-title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
}

.venta-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-bottom: 1rem;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
  font-family: 'Segoe UI', sans-serif;
  font-size: 0.9rem;
  letter-spacing: 0.5px;
}

.btn.limpiar {
  background-color: #e74c3c;
  color: white;
}

.btn.limpiar:hover {
  background-color: #c0392b;
}

.btn.consultar {
  background-color: #3498db;
  color: white;
}

.btn.consultar:hover {
  background-color: #2980b9;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fffaf3;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  width: 90%;
  max-width: 500px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8c471;
  color: #4d2c0c;
  border-bottom: 2px solid #f5cba7;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.5rem;
}

.btn-cerrar {
  background: none;
  border: none;
  font-size: 2rem;
  color: #4d2c0c;
  cursor: pointer;
  padding: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.btn-cerrar:hover {
  transform: rotate(90deg);
}

.modal-body {
  padding: 20px;
  max-height: 500px;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 600;
  color: #4d2c0c;
  margin-bottom: 5px;
  font-size: 0.9rem;
}

.form-group input,
.form-group select {
  padding: 10px;
  border: 2px solid #f5cba7;
  border-radius: 8px;
  font-family: 'Segoe UI', sans-serif;
  font-size: 0.9rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #e67e22;
  box-shadow: 0 0 5px rgba(230, 126, 34, 0.3);
}

.error-mensaje {
  color: #e74c3c;
  background-color: #fadbd8;
  padding: 10px;
  border-radius: 8px;
  margin-top: 15px;
  border-left: 4px solid #e74c3c;
}

.modal-footer {
  padding: 15px 20px;
  background: #f5f5f5;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  border-top: 1px solid #f5cba7;
}

.btn-cancelar {
  background-color: #95a5a6;
  color: white;
}

.btn-cancelar:hover {
  background-color: #7f8c8d;
}

.btn-aplicar {
  background-color: #27ae60;
  color: white;
}

.btn-aplicar:hover {
  background-color: #229954;
}

/* Modal Transition */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-content,
.modal-leave-active .modal-content {
  transition: transform 0.3s ease;
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: scale(0.9);
}

.venta-scroll {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 8px;
  scrollbar-color: #f8c471 transparent;
  scrollbar-width: thin;
}

.venta-scroll::-webkit-scrollbar {
  width: 8px;
}

.venta-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.venta-scroll::-webkit-scrollbar-thumb {
  background-color: #f8c471;
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: content-box;
}

.venta-table .table-header,
.venta-table .table-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
}

.venta-table .table-header {
  background: #f8c471;
  color: #4d2c0c;
  text-transform: uppercase;
  font-weight: bold;
}

.venta-table .table-row {
  background: #fdf6ec;
  margin-top: 8px;
  transition: 0.2s;
}

.venta-table .table-row:nth-child(even) {
  background: #faebd7;
}

.venta-table .table-row:hover {
  background: #f5cba7;
  transform: scale(1.01);
}

.cell {
  color: #333;
}

.detalle-expandido {
  grid-column: 1 / -1;
  background: #fff3e6;
  padding: 20px;
  margin-top: 10px;
  border-radius: 8px;
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.05);
  font-size: 14px;
  color: #444;
}

.detalle-expandido p {
  margin: 6px 0;
}

.detalle-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-top: 1rem;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  font-family: 'Segoe UI', sans-serif;
}

.detalle-table th {
  background: #f8c471;
  color: #4d2c0c;
  padding: 10px;
  text-align: left;
  font-size: 13px;
}

.detalle-table td {
  padding: 10px;
  font-size: 13px;
  color: #333;
  border-top: 1px solid #eee;
}

.sin-resultados {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 1.1rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>