<template>
  <div class="ingreso-table-container">
    <h2 class="title">Lista de Ingresos</h2>

    <div class="filter-bar">
      <input type="date" v-model="fechaInicio" />
      <input type="date" v-model="fechaFin" />
      <button @click="buscarPorRango">Buscar</button>
      <button @click="limpiarFiltros">Limpiar</button>
    </div>

    <div class="scroll-wrapper">
      <div class="scroll-card">
        <div class="table-header" role="row">
          <span>Fecha</span>
          <span>Total Diario</span>
          <span>Total Facturas</span>
          <span>Empleado</span>
        </div>

        <div
          v-for="(ingreso, index) in ingresos"
          :key="ingreso.idIngreso || index"
          class="table-row"
          role="row"
        >
          <span class="cell">{{ ingreso.fecha }}</span>
          <span class="cell">{{ ingreso.totalDiario }}</span>
          <span class="cell">{{ ingreso.totalFacturas }}</span>
          <span class="cell">{{ ingreso.nombreEmpleado || 'Sin asignar' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { IngresoProvider } from '../providers/IngresoProvider.js'

export default {
  name: 'IngresoTable',

  data() {
    return {
      ingresos: [],
      fechaInicio: '',
      fechaFin: ''
    }
  },

  async mounted() {
    await this.cargarTodos()
  },

  methods: {
    async cargarTodos() {
      try {
        const data = await IngresoProvider.getAll()
        this.ingresos = Array.isArray(data) ? data : []
      } catch (error) {
        console.error('Error al obtener ingresos:', error.message)
      }
    },

    async buscarPorRango() {
      if (!this.fechaInicio || !this.fechaFin) return

      try {
        const data = await IngresoProvider.getByRango(this.fechaInicio, this.fechaFin)
        this.ingresos = Array.isArray(data) ? data : []
      } catch (error) {
        console.error('Error al filtrar ingresos:', error.message)
      }
    },

    async limpiarFiltros() {
      this.fechaInicio = ''
      this.fechaFin = ''
      await this.cargarTodos()
    }
  }
}
</script>

<style scoped>
.ingreso-table-container {
  width: 95%;
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

.filter-bar {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
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
  grid-template-columns: repeat(4, 1fr);
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
</style>