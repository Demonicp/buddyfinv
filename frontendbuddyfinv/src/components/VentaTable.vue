<template>
  <div class="venta-wrapper">
    <div class="venta-card">
      <h2 class="venta-title">Listado de Ventas</h2>

      <div class="venta-actions">
        <button class="btn limpiar" @click="mostrarAlerta('Limpiar Filtros')">Limpiar Filtros</button>
        <button class="btn consultar" @click="mostrarAlerta('Consultar Venta')">Consultar Venta</button>
      </div>

      <div class="venta-scroll">
        <div class="venta-table">
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
              <div v-if="ventaSeleccionada === venta.idVenta" class="detalle-expandido">
                <p><strong>Propietario:</strong> {{ venta.propietario?.nombre || venta.propietario }}</p>
                <p><strong>Método de pago:</strong> {{ venta.metodoPago }}</p>
                <p><strong>Estado:</strong> {{ venta.estadoVenta }}</p>

                <table class="detalle-table">
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
                    <tr v-for="producto in venta.productos" :key="producto.nombre">
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
  grid-template-columns: repeat(4, 1fr);
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>