<template>
  <div class="venta-table-container">
    <h2 class="title">Listado de Ventas</h2>

    <div class="acciones-venta">
      <button class="btn limpiar" @click="mostrarAlerta('Limpiar Filtros')">Limpiar Filtros</button>
      <button class="btn consultar" @click="mostrarAlerta('Consultar Venta')">Consultar Venta</button>
    </div>

    <div class="table-header" role="row">
      <span>ID</span>
      <span>Fecha y Hora</span>
      <span>Total</span>
      <span>Empleado</span>
    </div>

    <div
      v-for="venta in ventas"
      :key="venta.idVenta"
      class="table-row"
      role="row"
      @click="toggleDetalle(venta.idVenta)"
    >
      <span class="cell">{{ venta.idVenta }}</span>
      <span class="cell">{{ new Date(venta.fecha).toLocaleString() }}</span>
      <span class="cell">${{ venta.total.toFixed(2) }}</span>
      <span class="cell">{{ venta.usuario?.nombre || venta.usuario }}</span>

      <transition name="fade">
        <div
          v-if="ventaSeleccionada === venta.idVenta"
          class="detalle-expandido"
        >
          <p><strong>Propietario:</strong> {{ venta.propietario?.nombre || venta.propietario }}</p>
          <p><strong>Método de pago:</strong> {{ venta.metodoPago }}</p>
          <p><strong>Estado:</strong> {{ venta.estadoVenta }}</p>

          <table>
            <thead>
              <tr>
                <th>Producto</th>
                <th>Cantidad</th>
                <th>Precio Unitario</th>
                <th>Subtotal</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="producto in venta.productos"
                :key="producto.nombre"
              >
                <td>{{ producto.nombre }}</td>
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
</template>
<script>
import { VentaProvider } from '../providers/VentaProvider.js'



export default {
  name: 'VentaTable',
  data() {
    return {
      ventas: [],
      ventaSeleccionada: null
    }
  },
  async getDetalladasPorUsuario(idUsuario) {
  const token = localStorage.getItem('token')
  const res = await fetch(`${VENTAS_BASE}/detalladas/usuario/${idUsuario}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  const data = await handleResponse(res)
  return Array.isArray(data) ? data.map(crearVentaDTO) : []
},

  methods: {
    toggleDetalle(id) {
      this.ventaSeleccionada = this.ventaSeleccionada === id ? null : id
    }
  }
}
</script>

<style scoped>
.venta-table-container {
  width: 95%;
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: "Segoe UI", sans-serif;
}

.title {
  text-align: center;
  color: #ff8800;
  margin-bottom: 20px;
  font-size: 22px;
  font-weight: bold;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  font-weight: bold;
}

.table-header {
  background: #ff8800;
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
  background: #ffd9b3;
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

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-top: 1rem;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

th {
  background: #ff8800;
  color: white;
  padding: 10px;
  text-align: left;
  font-size: 13px;
}

td {
  padding: 10px;
  font-size: 13px;
  color: #333;
  border-top: 1px solid #eee;
}

tr:last-child td {
  border-bottom: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.acciones-venta {
  display: flex;
  justify-content: flex-end; 
  gap: 1rem; 
  margin-bottom: 1rem;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  background-color: #2c3e50;
  color: white;
  cursor: pointer;
  border-radius: 4px;
}

.btn:hover {
  background-color: #34495e;
}
</style>