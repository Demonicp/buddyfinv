<template>
  <div class="edicion-wrapper">
    <div class="edicion-card">
      <!-- 🔍 Campo de búsqueda -->
      <div class="busqueda-bar">
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
      <div v-if="producto" class="tabla-edicion">
        <table>
          <tbody>
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
                  <option
                    v-for="tipo in tipos"
                    :key="tipo.idTipoProducto"
                    :value="tipo.idTipoProducto"
                  >
                    {{ tipo.observacion }}
                  </option>
                </select>
              </td>
            </tr>

            <tr>
              <td><strong>Estado:</strong></td>
              <td>
                <select v-model="producto.idEstadoProducto">
                  <option
                    v-for="estado in estados"
                    :key="estado.idEstadoPro"
                    :value="estado.idEstadoPro"
                  >
                    {{ estado.observacion }}
                  </option>
                </select>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- ✅ Botones -->
        <div class="botones-edicion">
          <button @click="guardar">Completar</button>
          <button @click="cancelar">Cancelar</button>
        </div>
      </div>
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
  .edicion-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 2rem 1rem;
    background-color: #fffaf3;
    font-family: 'Segoe UI', sans-serif;
  }
  
  .edicion-card {
    background-color: #fff;
    padding: 2rem;
    border-radius: 16px;
    box-shadow: inset 0 0 0 2px #f8c471;
    border: 1px solid #f5cba7;
    width: 100%;
    max-width: 600px;
    transition: box-shadow 0.3s ease;
  }
  
  .edicion-card:hover {
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  }
  
  .busqueda-bar {
    display: flex;
    gap: 0.75rem;
    margin-bottom: 1.2rem;
  }
  
  .busqueda-bar input {
    flex: 1;
    padding: 0.6rem 0.8rem;
    font-size: 0.95rem;
    border-radius: 8px;
    border: 1px solid #ccc;
  }
  
  .busqueda-bar button {
    padding: 0.6rem 1rem;
    font-size: 1.1rem;
    background-color: #f8c471;
    color: #4d2c0c;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }
  
  .busqueda-bar button:hover {
    background-color: #f5b041;
  }
  
  .error {
    color: #c0392b;
    font-size: 0.85rem;
    margin-top: 0.25rem;
    font-weight: bold;
  }
  
  .errorInput {
    border-color: #c0392b;
  }
  
  .tabla-edicion table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 1.2rem;
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  }
  
  .tabla-edicion td {
    padding: 0.6rem;
    vertical-align: top;
    font-size: 0.95rem;
    color: #333;
  }
  
  input[type="text"],
  input[type="number"],
  select {
    width: 100%;
    padding: 0.5rem 0.7rem;
    border-radius: 8px;
    border: 1px solid #ccc;
    font-size: 0.95rem;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
  }
  
  input:focus,
  select:focus {
    border-color: #f8c471;
    outline: none;
    box-shadow: 0 0 0 3px rgba(248, 196, 113, 0.3);
  }
  
  .botones-edicion {
    display: flex;
    justify-content: center;
    gap: 0.75rem;
    margin-top: 1.2rem;
  }
  
  .botones-edicion button {
    padding: 0.6rem 1.2rem;
    font-size: 0.95rem;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s ease, transform 0.2s ease;
  }
  
  .botones-edicion button:first-child {
    background-color: #f8c471;
    color: #4d2c0c;
  }
  
  .botones-edicion button:first-child:hover {
    background-color: #f5b041;
    transform: scale(1.05);
  }
  
  .botones-edicion button:last-child {
    background-color: #e74c3c;
    color: white;
  }
  
  .botones-edicion button:last-child:hover {
    background-color: #c0392b;
    transform: scale(1.05);
  }
  </style>