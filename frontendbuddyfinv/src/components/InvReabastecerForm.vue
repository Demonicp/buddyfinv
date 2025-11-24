<template>
  <div class="reabastecer-wrapper">
    <div class="card">
      <h2>Ingresa el codigo de tu producto</h2>

      <!-- BARRA DE BÚSQUEDA -->
      <div class="busqueda">
        <input
          v-model="codigo"
          @input="onInputCodigo"
          @keydown.enter.prevent="buscar"
          maxlength="6"
          placeholder="Código del producto (solo números, máx 6)"
        />
        <button @click="buscar" class="btn-search" aria-label="Buscar">
          🔍
        </button>
      </div>

      <p v-if="errorBusqueda" class="error">{{ errorBusqueda }}</p>

      <!-- RESULTADO (si existe) -->
      <div v-if="productoEncontrado" class="resultado">
        <ul>
          <li><strong>Código:</strong> {{ productoEncontrado.idProducto }}</li>
          <li><strong>Nombre:</strong> {{ productoEncontrado.nombre }}</li>
          <li><strong>Precio:</strong> {{ formatoPrecio(productoEncontrado.precio) }}</li>
          <li><strong>Estado:</strong> {{ productoEncontrado.idEstadoProducto }}</li>
          <li><strong>Cantidad actual:</strong> {{ productoEncontrado.cantidad }}</li>
        </ul>

        <!-- FORMULARIO DE REABASTECER -->
        <div class="form-reabastecer">
          <label>
            Cantidad a sumar
            <input
              v-model="cantidad"
              @input="onInputCantidad"
              maxlength="3"
              placeholder="Ej. 10"
            />
          </label>

          <div class="acciones">
            <button class="btn-confirm" @click="confirmar">Confirmar</button>
            <button class="btn-cancel" @click="cancelar">Cancelar</button>
          </div>
          <p v-if="errorCantidad" class="error">{{ errorCantidad }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

import { ProductoProvider } from '@/providers/ProductoProvider'

const codigo = ref('')
const cantidad = ref('')
const productoEncontrado = ref(null)
const errorBusqueda = ref('')
const errorCantidad = ref('')

function onInputCodigo(e) {
  // dejar solo números y máximo 6
  codigo.value = e.target.value.replace(/\D/g, '').slice(0, 6)
  errorBusqueda.value = ''
  productoEncontrado.value = null
}

function onInputCantidad(e) {
  // sólo números y máximo 3 dígitos
  cantidad.value = e.target.value.replace(/\D/g, '').slice(0, 3)
  errorCantidad.value = ''
}

async function buscar() {
  errorBusqueda.value = ''
  productoEncontrado.value = null

  if (!codigo.value) {
    errorBusqueda.value = 'Debe ingresar un código de producto'
    return
  }

  try {
    const res = await ProductoProvider.buscarPorCodigoReabastecer(codigo.value)
    productoEncontrado.value = res
    errorBusqueda.value = ''
  } catch (err) {
    // mostrar mensaje en rojo según tu criterio
    errorBusqueda.value = err.message || 'Error al buscar el producto'
    productoEncontrado.value = null
  }
}

function validarCantidad() {
  errorCantidad.value = ''
  if (!cantidad.value || parseInt(cantidad.value, 10) <= 0) {
    errorCantidad.value = 'Ingresa una cantidad válida (entero mayor que 0)'
    return false
  }
  return true
}

async function confirmar() {
  if (!productoEncontrado.value) {
    errorBusqueda.value = 'Busca primero un producto válido'
    return
  }
  if (!validarCantidad()) return

  try {
    await ProductoProvider.reabastecer({
      idProducto: productoEncontrado.value.idProducto,
      cantidad: parseInt(cantidad.value, 10)
    })
    // actualizar la vista: sumar localmente (opcional)
    productoEncontrado.value.cantidad += parseInt(cantidad.value, 10)
    // resetear formulario de búsqueda como indican los criterios
    codigo.value = ''
    cantidad.value = ''
    productoEncontrado.value = null
    errorBusqueda.value = ''
    alert('Proceso exitoso')
  } catch (err) {
    errorCantidad.value = err.message || 'Error al reabastecer'
  }
}

function cancelar() {
  // limpiar resultado y dejar solo búsqueda
  productoEncontrado.value = null
  cantidad.value = ''
  errorCantidad.value = ''
  errorBusqueda.value = ''
  codigo.value = ''
}

function formatoPrecio(val) {
  if (val == null) return '-'
  return new Intl.NumberFormat('es-CO').format(val)
}
</script>

<style scoped>
:root{
  --accent: #f8c471;
  --accent-dark: #f5b041;
  --accent-contrast: #4d2c0c;
  --card-border: #f5cba7;
  --bg: #fffaf3;
  --danger: #c0392b;
}

.reabastecer-wrapper {
  display: flex;
    justify-content: center;
    align-items: center;
    padding: 2rem 1rem;
    background-color: #fffaf3;
    font-family: 'Segoe UI', sans-serif;
}
.card {
    background-color: #fff;
    padding: 2rem;
    border-radius: 16px;
    box-shadow: inset 0 0 0 2px #f8c471;
    border: 1px solid #f5cba7;
    width: 100%;
    max-width: 600px;
    transition: box-shadow 0.3s ease;
}

.card h2 {
  margin: 0 0 1rem 0;
  color: rgba(0, 0, 0, 0.74);
  font-weight: 400;
 
}

.busqueda {
  display: flex;
    gap: 0.75rem;
    margin-bottom: 1.2rem;
}
.busqueda input {
    flex: 1;
    padding: 0.9rem 0.99rem;
    font-size: 1rem;
    border-radius: 8px;
    border: 3px solid #ccc;
}
.btn-search {
  padding: .55rem .9rem;
  border-radius: 8px;
  border: none;
  background: rgba(255, 224, 47, 0.822);
  color: salmon;
  font-weight: 700;
  cursor: pointer;
}
.btn-search:hover { background:rgba(255, 241, 47, 0.932) ; }

.error {
  color: rgba(0, 0, 0, 0.76);
  font-weight: 400;
  font-size: 1.1rem;
  margin-top: .4rem;
  background-color: #f5bda7e3;padding: .55rem .9rem;
  border-radius: 8px;
  border: none;
}

.resultado { 
  margin-top: 1rem;
  background: #fff;
  padding: 1.2rem;
  border-radius: 12px;
  border: 1px solid rgba(255, 150, 80, 0.25); /* naranja suave */
  box-shadow: 0 4px 10px rgba(255, 140, 60, 0.08);
  transition: 0.2s ease;
  font-family: "Poppins", sans-serif; /* Fuente bonita */
  font-size: 1rem; /* Un poquitico más grande */
}

.resultado:hover {
  box-shadow: 0 6px 14px rgba(255, 140, 60, 0.15);
}

.resultado ul {
  list-style: none;
  padding: 0;
  margin: 0 0 1rem 0;
}

.resultado li {
  padding: .45rem 0;
  color: #333;
  font-size: 1.02rem; /* letra un poco más grande */
  border-left: 4px solid rgba(255, 150, 80, 0.35);
  padding-left: .65rem;
  background: rgba(255, 170, 90, 0.06);
  border-radius: 6px;
  margin-bottom: .35rem;
}


.form-reabastecer {
  display: flex;
  flex-direction: column;
  gap: .9rem; /* más espacio entre filas */
  font-size: 1rem; 
}

.form-reabastecer label {
  font-weight: 600;
  color: rgba(0, 0, 0, 0.82);
  display: flex;
  flex-direction: column;
  gap: .40rem;
  padding: .55rem 0; /* más espacio */
  font-size: 1.05rem; 
}

.form-reabastecer input {
  padding: .65rem .85rem; 
  border-radius: 10px;
  border: 1px solid rgba(255, 150, 80, 0.40); /* borde naranja suave */
  background: #fff; 
  width: 190px; /* un poquito más ancho */
  font-size: 1rem;
  transition: .2s ease;
 
}


.form-reabastecer input:focus {
  outline: none;
  border-color: rgba(255, 140, 50, 0.8); /* naranja un poco más fuerte */
  box-shadow: 0 0 6px rgba(255, 140, 50, 0.25);
}

.acciones {
  display: flex;
  gap: .6rem;
  margin-top: .4rem;
}
.btn-confirm {
  background: rgba(250, 235, 99, 0.863);
  color: rgba(0, 0, 0, 0.733);
  border: none;
  padding: .55rem 1rem;
  border-radius: 8px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
}
.btn-confirm:hover { background: rgb(241, 227, 92); transform: translateY(-1px); }

.btn-cancel {
  background: #faae70;
  color: rgba(0, 0, 0, 0.733);
  border: none;
  padding: .55rem 1rem;
  border-radius: 8px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
}
.btn-cancel:hover { background: #df5647; transform: translateY(-1px); }

@media (max-width: 720px) {
  .card { padding: 1rem; }
  .form-reabastecer input { width: 100%; }
  .acciones { flex-direction: column; }
}
</style>
