<template>
  <div class="registro-wrapper">
    <div class="registro-card">


      <div class="registro-actions">


      </div>

      <div class="registro-scroll">
        <div class="registro-table">
          <div class="table-header" role="row">
            <span>ID</span>
            <span>Fecha y Hora</span>
            <span>Total</span>
            <span>Cliente</span>
            <span>Empleado</span>
            <span>ID Emp</span>
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
            <span class="cell">{{ venta.cliente || venta.propietario?.nombre || venta.propietario }}</span>
            <span class="cell">{{ venta.empleado }}</span>
            <!-- AGREGADO: mostrar empleadoId también en la fila principal -->
            <span class="cell">{{ venta.empleadoId ?? '—' }}</span>
            <span class="cell">
              <RegistroPdfButton :venta="venta" btnClass="btn consultar" />
            </span>

            <transition name="fade">
              <div v-if="ventaSeleccionada === venta.idVenta" class="detalle-expandido">
                <p><strong>Cliente:</strong> {{ venta.cliente || '—' }}</p>

                <!-- AGREGADO: mostrar el id del empleado que realizó la venta (propiedad 'empleadoId' devuelta por el backend) -->
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

                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="producto in venta.productos" :key="producto.productoId || producto.nombreProducto">
                      <td>{{ producto.productoId }}</td>
                      <td>{{ producto.nombreProducto }}</td>
                      <td>{{ producto.cantidad }}</td>
                      <td>${{ producto.precioUnitario?.toFixed(2) || '—' }}</td>
                      <td>${{ producto.subtotal.toFixed(2) }}</td>

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
import RegistroPdfButton from './RegistroPdfButton.vue'

const ventaSeleccionada = ref(null)

function toggleDetalle(id) {
  ventaSeleccionada.value = ventaSeleccionada.value === id ? null : id
}

function mostrarAlerta(mensaje) {
  alert(mensaje)
}
</script>


<style scoped>
.registro-wrapper {
  width: 100%;
  height: 100%;

  margin: 10px auto;
  font-family: 'Segoe UI', sans-serif;
}

.registro-card {
  background: #fffaf3;
  padding: 20px;
  
  border-radius: 16px;
  box-shadow: inset 0 0 0 2px #f8c471;

}

.registro-title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
}

.registro-actions {
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
  background-color: #e67e22;
  color: white;
  border-radius: 8px;
}

.btn.consultar:hover {
  background-color: #d35400;
}

.registro-scroll {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 8px;
  scrollbar-color: #f8c471 transparent;
  scrollbar-width: thin;
}

.registro-scroll::-webkit-scrollbar {
  width: 8px;
}

.registro-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.registro-scroll::-webkit-scrollbar-thumb {
  background-color: #f8c471;
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: content-box;
}

.registro-table .table-header,
.registro-table .table-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  padding: 0.5%;
  align-items: center;
  
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
}

.registro-table .table-header {
  background: #f8c471;
  color: #4d2c0c;
  text-transform: uppercase;
  font-weight: bold;
}

.registro-table .table-row {
  background: #fdf6ec;
  margin-top: 8px;
  transition: 0.2s;
}

.registro-table .table-row:nth-child(even) {
  background: #faebd7;
}

.registro-table .table-row:hover {
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
