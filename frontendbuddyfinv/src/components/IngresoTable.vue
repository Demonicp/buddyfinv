<template>
  <div class="ingreso-table-container">
    <h2 class="title">Lista de Ingresos</h2>

    <div class="filter-bar">
      <input type="date" v-model="fechaInicio" />
      <input type="date" v-model="fechaFin" />
      <button @click="buscarPorRango">Buscar</button>
      <button @click="limpiarFiltros">Limpiar</button>
    </div>

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
  font-family: 'Futura', 'Garamond', avenir;
}

.title {
  text-align: center;
  color: #009688;
  margin-bottom: 2px;
  font-size: 2px;
  font-weight: bold;
}

.filter-bar {
  display: flex;
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
  background-color: #009688;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.filter-bar button:hover {
  background-color: #00796b;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.table-header {
  background: #009688;
  color: white;
  text-transform: uppercase;
  font-weight: bold;
}

.table-row {
  background: #f1f1f1;
  margin-top: 8px;
  transition: 0.2s;
}

.table-row:nth-child(even) {
  background: #eaeaea;
}

.table-row:hover {
  background: #b2dfdb;
  transform: scale(1.01);
}

.cell {
  color: #333;
}
</style>