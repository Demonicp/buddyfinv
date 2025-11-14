<template>
  <div class="formulario-edicion">
    <!-- 🔍 Campo de búsqueda -->
    <div class="busqueda-producto">
      <input
        v-model="codigoProducto"
        type="text"
        placeholder="Código del producto"
        @input="filtrarNumericos"
      />
      <button @click="buscarProducto">🔍</button>
    </div>

    <!-- ⚠️ Error de búsqueda -->
    <p v-if="errorBusqueda" class="error">{{ errorBusqueda }}</p>

    <!-- 🧾 Tabla editable -->
    <table v-if="producto">
      <tr>
        <td><strong>Código:</strong></td>
        <td>{{ producto.idProducto }}</td>
      </tr>

      <tr>
        <td><strong>Nombre:</strong></td>
        <td>
          <input
            v-model="producto.nombre"
            maxlength="150"
            :class="{ errorInput: errores.nombre }"
          />
          <p v-if="errores.nombre" class="error">{{ errores.nombre }}</p>
        </td>
      </tr>

      <tr>
        <td><strong>Precio:</strong></td>
        <td>
          <input
            v-model="producto.precio"
            type="number"
            :class="{ errorInput: errores.precio }"
          />
          <p v-if="errores.precio" class="error">{{ errores.precio }}</p>
        </td>
      </tr>

      <tr>
        <td><strong>Tipo de producto:</strong></td>
        <td>
          <select v-model="producto.idTipoProducto">
            <option v-for="tipo in tipos" :key="tipo.idTipoProducto" :value="tipo.idTipoProducto">
              {{ tipo.observacion }}
            </option>
          </select>
        </td>
      </tr>

      <tr>
        <td><strong>Estado:</strong></td>
        <td>
          <select v-model="producto.idEstadoProducto">
            <option v-for="estado in estados" :key="estado.idEstadoPro" :value="estado.idEstadoPro">
              {{ estado.observacion }}
            </option>
          </select>
        </td>
      </tr>
    </table>

    <!-- ✅ Botones -->
    <div v-if="producto" class="acciones">
      <button @click="guardar">Completar</button>
      <button @click="cancelar">Cancelar</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ProductoEdicionDTO } from '@/models/ProductoEdicion'
import { ProductoProvider } from '@/providers/ProductoProvider'
import { obtenerTiposProducto } from '@/providers/TipoProductoProvider'
import { obtenerEstadosProducto } from '@/providers/EstadoProductoProvider'

const codigoProducto = ref('')
const producto = ref(null)
const errorBusqueda = ref('')
const tipos = ref([])
const estados = ref([])

const errores = ref({
  nombre: '',
  precio: ''
})

function filtrarNumericos(e) {
  codigoProducto.value = e.target.value.replace(/\D/g, '')
}

async function buscarProducto() {
  errorBusqueda.value = ''
  if (!codigoProducto.value) {
    errorBusqueda.value = 'Debes ingresar un código válido.'
    producto.value = null
    return
  }

  try {
    const resultado = await ProductoProvider.buscarPorCodigo(codigoProducto.value)
    producto.value = new ProductoEdicionDTO(resultado)
  } catch (error) {
    errorBusqueda.value = error.message
    producto.value = null
  }
}

function validarCampos() {
  let valido = true
  errores.value = { nombre: '', precio: '' }

  if (!producto.value.nombre || producto.value.nombre.trim() === '') {
    errores.value.nombre = 'No puedes dejar este campo vacío'
    valido = false
  }

  if (producto.value.nombre.length > 150) {
    errores.value.nombre = 'Máximo 150 caracteres'
    valido = false
  }

  if (!producto.value.precio || producto.value.precio <= 0) {
    errores.value.precio = 'No puedes dejar este campo vacío'
    valido = false
  }

  return valido
}

async function guardar() {
  if (!validarCampos()) return

  try {
    await ProductoProvider.guardarEdicion(producto.value)
    alert('Producto actualizado correctamente')
    cancelar()
  } catch (error) {
    alert('Error al guardar: ' + error.message)
  }
}

function cancelar() {
  producto.value = null
  errorBusqueda.value = ''
  codigoProducto.value = ''
  errores.value = { nombre: '', precio: '' }
}

onMounted(async () => {
  tipos.value = await obtenerTiposProducto()
  estados.value = await obtenerEstadosProducto()
})
</script>

<style scoped>
/* 🎨 Estilos pastel y claros */
.formulario-edicion {
  background-color: #fffaf3;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: inset 0 0 0 2px #f8c471;
  border: 1px solid #f5cba7;
  max-width: 700px;
  margin: 2rem auto;
  font-family: 'Segoe UI', sans-serif;
}

.busqueda-producto {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.busqueda-producto input {
  flex: 1;
  padding: 0.75rem;
  font-size: 1rem;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.busqueda-producto button {
  padding: 0.75rem 1rem;
  font-size: 1.2rem;
  background-color: #ffe0b2;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.busqueda-producto button:hover {
  background-color: #ffcc80;
}

.error {
  color: red;
  font-size: 0.85rem;
  margin-top: 0.25rem;
  display: block;
  font-weight: bold;
}

.errorInput {
  border-color: red;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1.5rem;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

td {
  padding: 0.75rem;
  vertical-align: top;
  font-size: 1rem;
  color: #333;
}

input[type="text"],
input[type="number"],
select {
  width: 100%;
  padding: 0.5rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 1rem;
}

.acciones {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.acciones button {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.acciones button:first-child {
  background-color: #ffe0b2;
  color: #4d2c0c;
  box-shadow: inset 0 0 0 2px #f5cba7;
}

.acciones button:first-child:hover {
  background-color: #ffcc80;
}

.acciones button:last-child {
  background-color: #f5f5f5;
  color: #333;
}

.acciones button:last-child:hover {
  background-color: #e0e0e0;
}
</style>