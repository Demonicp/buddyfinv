<template>
  <div class="venta-table-container">
    <h2 class="title">Listado de Ventas</h2>

    <div class="acciones-venta">
      <button class="btn limpiar" @click="mostrarAlerta('Limpiar Filtros')">Limpiar Filtros</button>
      <button class="btn consultar" @click="mostrarAlerta('Consultar Venta')">Consultar Venta</button>
    </div>

    <div class="scroll-wrapper">
      <div class="scroll-card">
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
          <span class="cell">{{ venta.empleado }}</span>

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
  </div>
</template>

<script setup>
defineProps({
  ventas: Array
})

import { ref } from 'vue'
const ventaSeleccionada = ref(null)

function toggleDetalle(id) {
  ventaSeleccionada.value = ventaSeleccionada.value === id ? null : id
}

function mostrarAlerta(mensaje) {
  alert(mensaje)
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
  font-family: 'Segoe UI', sans-serif;
}

.title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
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
  background-color: #e67e22;
  color: white;
  cursor: pointer;
  border-radius: 6px;
  font-weight: bold;
  transition: background-color 0.2s;
}

.btn:hover {
  background-color: #d35400;
}

/* Scroll externo */
.scroll-wrapper {
  max-height: 520px;
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
  padding: 2rem 1rem;
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
  padding: 18px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
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
  background: #f8c471;
  color: #4d2c0c;
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
</style>