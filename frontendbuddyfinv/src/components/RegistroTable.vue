<template>
  <div class="registro-wrapper">
    <div class="registro-card">
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
                <div class="detallito">
                  <p><strong>Cliente:</strong> {{ venta.cliente || '—' }}</p>

                  <!-- AGREGADO: mostrar el id del empleado que realizó la venta (propiedad 'empleadoId' devuelta por el backend) -->
                  <p><strong>ID Empleado:</strong> {{ venta.empleadoId ?? '—' }}</p>
                  <p><strong>Método de pago:</strong> {{ venta.metodoPago }}</p>
                  <p><strong>Estado:</strong> {{ venta.estadoVenta }}</p>
                </div>



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

@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@100..900&display=swap');

.registro-wrapper {
  width: 100%;
  height: 100%;


  font-family: 'Outfit', sans-serif;
  font-weight: 500;
}

.registro-card {
  background: #fffaf3;
  padding: 1%;

  height: 72%;
  font-weight: bold;
  display: grid;
  overflow: auto;

  border-radius: 16px;
  box-shadow: inset 0 0 0 2px #f8c471;

}

.registro-title {

  text-align: center;
  color: #e67e22;

  font-size: 2rem;
  font-weight: bold;
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
  height: 100%;
  overflow-y: auto;
  overflow-x: auto  ;
  padding-right: 8px;
  scrollbar-color: #f8c471 transparent;
  scrollbar-width: thin;
}







.registro-table .table-header{

  font-weight: bolder;
  display: grid;
  
  height: 100%;
  font-size: medium;
  
  grid-template-columns: repeat(7, 1fr);
  padding: 0.5%;
  border-radius: 10px;
}
.registro-table .table-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  padding: 0.5%;
  align-items: center;
  height: auto;
  
  font-size: 14px;
  font-family: 'Outfit', sans-serif;
  font-weight: bold;
  border-radius: 8px;
  cursor: pointer;
}

.registro-table .table-header {
  background: #f8c471;
  color: #4d2c0c;
  text-transform: uppercase;
  font-weight: bold;
  font-family: 'Outfit', sans-serif;
}

.registro-table .table-row {
  background: #fdf6ec;
  margin-top: 8px;
  font-family: 'Outfit', sans-serif;
  transition: 0.2s;
}

.registro-table .table-row:nth-child(even) {
  background: #faebd7;
  font-family: 'Outfit', sans-serif;
  font-weight: bold;
}

.registro-table .table-row:hover {
  background: #f5cba7;
  transform: scale(1.01);
  font-weight: bold;
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
  font-weight: bold;
  margin-top: 1rem;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  font-family: 'Outfit', sans-serif;
}

.detalle-table th {
  background: #f8c471;
  font-weight: 700;
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


.detallito{
  display: grid;  
  font-family: 'Outfit', sans-serif;
  grid-template-columns: repeat(2, 1fr);
  font-weight: bold;
  border-radius: 8px;

}
</style>
