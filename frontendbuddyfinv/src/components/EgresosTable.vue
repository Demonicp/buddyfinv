<template>
  <div class="egreso-wrapper">
    <div class="total-egresos">
      <span class="total-label">Total de Egresos:</span>
      <span class="total-value">-{{ formatoMoneda(totalEgresos) }}</span>
    </div>

    <div class="egreso-table-container">
      <h2 class="title">Gestion De Egresos</h2>

      <div class="filter-bar">
        <input type="date" v-model="fechaInicio" />
        <input type="date" v-model="fechaFin" />
        <button @click="filtrarFechas">Buscar</button>
        <button @click="limpiarFiltros">Limpiar</button>
        <button class="btn-Registrar" @click="abrirModal">Registrar egreso</button>
        <button class="btn-consultar" @click="consultarEgreso">Consultar egreso</button>
      </div>

      <!-- Scroll externo con tarjeta interna -->
      <div class="scroll-wrapper">
        <div class="scroll-card">
          <div class="table-header" role="row">
            <span>Fecha</span>
            <span>Concepto</span>
            <span>Categoría</span>
            <span>Valor</span>
            <span>Método de Pago</span>
          </div>

          <div
            v-for="(egreso, index) in egresos"
            :key="egreso.id_egreso || index"
            class="table-row"
            role="row"
          >
            <span class="cell">{{ formatearFecha(egreso.fecha) || 'N/A' }}</span>
            <span class="cell">{{ egreso.observacion || 'N/A' }}</span>
            <span class="cell">{{ egreso.descripcionTipoEgreso || 'N/A' }}</span>
            <span class="cell">{{ formatoMoneda(egreso.costo) }}</span>
            <span class="cell">{{ egreso.descripcionMetodoPago }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL -->
    <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal-content">
        <h3>Registrar nuevo egreso</h3>

        <div class="form-group">
          <label>Fecha:</label>
          <input v-model="nuevoEgreso.fecha" type="date" class="form-input" />
        </div>


        <!-- Campo Concepto: descripción del egreso (ej: Compra de materiales) -->
        <div class="form-group">
          <label>observaciones:</label>
          <input 
            v-model="nuevoEgreso.observacion" 
            type="text" 
            maxlength="300"
            placeholder="Ej: Compra de materiales"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>Categoría:</label>
          <select v-model="nuevoEgreso.categoria" class="form-select">
            <option value="">Seleccione una categoría...</option>
      <option v-for="tipo in tiposEgreso" :key="tipo.idTipoEgreso" :value="tipo.descripcion">
          {{ tipo.descripcion }}
      </option>


          </select>
        </div>

        <div class="form-group">
          <label>Valor:</label>
          <input 
            v-model.number="nuevoEgreso.costo" 
            type="number" 
            step="0.01"
            min="0" 
            placeholder="Ej: 25000"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>Método de Pago:</label>
          <select v-model="nuevoEgreso.metodoPago" class="form-select">
            <option value="">Seleccione...</option>
            <option value="Efectivo">Efectivo</option>
            <option value="Transferencia">Transferencia</option>
            <option value="Tarjeta">Tarjeta</option>
          </select>
        </div>

        <div class="modal-buttons">
          <button ref="btnGuardar" class="btn-guardar" :disabled="!formularioValido" @click="guardarEgreso" @mousedown="intentoGuardar">Guardar</button>
          <button class="btn-cancelar" @click="cerrarModal">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { EgresoProvider } from '../providers/EgresoProvider.js'
export default {
  name: 'EgresoTable',
  data() {
    return {
      tiposEgreso:[],
      egresos: [], // Lista de egresos obtenidos del backend
      totalEgresos: 0, // Suma total de todos los egresos
      mostrarModal: false, // Controla la visibilidad del modal de registro el false es porque mientras no se unda el boton no se abra el formulario
       fechaInicio: '', // ← esta línea es clave
      fechaFin: '',
      nuevoEgreso: {
        fecha: '',
        observacion: '',
        categoria: '',
        costo: null,
        metodoPago: ''
      }
    }
  },
  computed: {
    formularioValido() {
      return (
        this.nuevoEgreso.fecha &&
        this.nuevoEgreso.observacion &&
        this.nuevoEgreso.categoria &&
        this.nuevoEgreso.costo &&
        this.nuevoEgreso.costo > 0 &&
        this.nuevoEgreso.metodoPago
      )
    }
  },
  methods: {
    async cargarTiposEgreso() {
    try {
      const response = await fetch('http://localhost:8080/tipo-egresos');
      const data = await response.json();
      this.tiposEgreso = data;
    } catch (error) {
      console.error('Error al cargar tipos de egreso:', error);
    }
  },mounted() {
  this.cargarTiposEgreso();
},


   
    formatoMoneda(valor) {
      if (!valor && valor !== 0) return '0.00'
      return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
      }).format(valor)
    },
    intentoGuardar() {
      if (!this.formularioValido) {
        const btn = this.$refs.btnGuardar
        btn.style.animation = 'none'
        void btn.offsetWidth
        btn.style.animation = 'vibrate-1 0.4s linear both'
      }
    },
    formatearFecha(fecha) {
      if (!fecha) return null
      try {
        const date = new Date(fecha)
        return date.toLocaleDateString('es-CO', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit'
        })
      } catch (e) {
        return fecha
      }
    },
    consultarEgreso() {
      alert('Funcionalidad pendiente')
    },
    abrirModal() {
      const hoy = new Date().toISOString().split('T')[0]
      this.nuevoEgreso = {
        fecha: hoy,
        observacion: '',
        categoria: '',
        costo: null,
        metodoPago: ''
      }
      this.mostrarModal = true
    },
    cerrarModal() {
      this.mostrarModal = false
      this.nuevoEgreso = {
        fecha: '',
        observacion: '',
        categoria: '',
        costo: null,
        metodoPago: ''
      }
    },
    async guardarEgreso() {
      if (!this.formularioValido) return alert('Complete todos los campos')

      try {
        const metodoPagoMap = {
          Efectivo: 1,
          Transferencia: 2,
          Tarjeta: 3
        }

        const dataEgreso = {
          observacion: this.nuevoEgreso.observacion,
          categoria: this.nuevoEgreso.categoria,
          costo: this.nuevoEgreso.costo,
          fecha: this.nuevoEgreso.fecha,
          idMetodoPago: metodoPagoMap[this.nuevoEgreso.metodoPago]
        }

        await EgresoProvider.registerEgreso(dataEgreso)
        alert('Egreso registrado exitosamente')
        this.cerrarModal()
        await this.cargarEgresos()
      } catch (error) {
        alert('Error al guardar egreso: ' + error.message)
      }
    },
    limpiarFiltros() {
      this.fechaInicio = ''
      this.fechaFin = ''
      this.cargarEgresos()
    },
    async cargarEgresos() {
      try {
        const [data, total] = await Promise.all([
          EgresoProvider.getAllEgresosByUsuario(),
          EgresoProvider.costoTotalEgresosById()
        ])
        this.egresos = Array.isArray(data) ? data : []
        this.totalEgresos = total || 0
      } catch (error) {
        console.error(error)
      }
    },
    async filtrarFechas() {
      if (!this.fechaInicio || !this.fechaFin) return
      try {
        const data = await EgresoProvider.filtrarByFechas(this.fechaInicio, this.fechaFin)
        this.egresos = Array.isArray(data) ? data : []
      } catch (error) {
        console.error(error)
      }
    }
  },
  async mounted() {
    await this.cargarEgresos()
    await this.cargarTiposEgreso()
  }
}
</script>

<style scoped>
.egreso-wrapper {
  width: 90%;
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', sans-serif;
}

.title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
}

.total-egresos {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 16px;
  color: #c0392b;
}

.total-label {
  margin-right: 8px;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  margin-bottom: 20px;
}

.filter-bar input[type="date"] {
  padding: 6px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.filter-bar button {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  background-color: #e67e22;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.filter-bar button:hover {
  background-color: #d35400;
}

/* Scroll externo */
.scroll-wrapper {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
  margin-top: 1rem;
  scrollbar-color: #f8c471 transparent;
  scrollbar-width: thin;
}

.scroll-wrapper::-webkit-scrollbar {
  width: 8px;
}

.scroll-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.scroll-wrapper::-webkit-scrollbar-thumb {
  background-color: #f8c471;
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: content-box;
}

/* Tarjeta con bordes suaves y espacio interno */
.scroll-card {
  padding: 1.5rem 1rem;
  border-radius: 16px;
  background-color: #fffaf3;
  box-shadow: inset 0 0 0 2px #f8c471;
  border: 1px solid #f5cba7;
}

/* Tabla */
.table-header,
.table-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  padding: 16px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.table-header {
  background: #f8c471;
  color: #4d2c0c;
  text-transform: uppercase;
  font-weight: bold;
}

.table-row {
  background: #fdf6ec;
  margin-top: 8px;
  transition: 0.2s;
}

.table-row:nth-child(even) {
  background: #faebd7;
}

.table-row:hover {
  background: #f5cba7;
  transform: scale(1.01);
}

.cell {
  color: #333;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fffaf3;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.modal-content h3 {
  margin-bottom: 1.5rem;
  color: #e67e22;
  text-align: center;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #333;
}

.form-input,
.form-select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
}

.btn-guardar {
  background-color: #27ae60;
  color: white;
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
}

.btn-guardar:hover {
  background-color: #1e8449;
}

.btn-cancelar {
  background-color: #e74c3c;
  color: white;
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
}

.btn-cancelar:hover {
  background-color: #c0392b;
}

/* Vibración para botón inválido */
@keyframes vibrate-1 {
  0% { transform: translateX(0); }
  20% { transform: translateX(-3px); }
  40% { transform: translateX(3px); }
  60% { transform: translateX(-3px); }
  80% { transform: translateX(3px); }
  100% { transform: translateX(0); }
}
</style>