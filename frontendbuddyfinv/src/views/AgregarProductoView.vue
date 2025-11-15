<template>
  <div class="agregar-producto-container">
    <div class="tarjeta">
      <h2 class="titulo">Añadir Producto</h2>

      <form @submit.prevent="confirmar">
        <div class="campo">
          <label for="nombre">Nombre:</label>
          <input
            id="nombre"
            type="text"
            v-model="producto.nombre"
            placeholder="Máximo 150 caracteres"
            maxlength="150"
          />
          <span v-if="errores.nombre" class="error">{{ errores.nombre }}</span>
        </div>

        <div class="campo">
          <label for="tipo">Tipo de producto:</label>
          <select id="tipo" v-model="producto.tipoProductoId">
            <option value="">Seleccione...</option>
            <option
              v-for="tipo in tipos"
              :key="tipo.idTipoProducto"
              :value="tipo.idTipoProducto"
            >
              {{ tipo.observacion }}
            </option>
          </select>
          <span v-if="errores.tipo" class="error">{{ errores.tipo }}</span>
        </div>

        <div class="campo">
          <label for="precio">Precio:</label>
          <input
            id="precio"
            type="text"
            v-model="producto.precio"
            placeholder="Ej: 1500.00"
            maxlength="10"
            @input="soloNumeros('precio')"
          />
          <span v-if="errores.precio" class="error">{{ errores.precio }}</span>
        </div>

        <div class="campo">
          <label for="estado">Estado:</label>
          <select id="estado" v-model="producto.estadoProductoId">
            <option value="">Seleccione...</option>
            <option
              v-for="estado in estados"
              :key="estado.idEstadoPro"
              :value="estado.idEstadoPro"
            >
              {{ estado.observacion }}
            </option>
          </select>
          <span v-if="errores.estado" class="error">{{ errores.estado }}</span>
        </div>

        <div class="campo">
          <label for="cantidadDisponible">Cantidad Disponible:</label>
          <input
            id="cantidadDisponible"
            type="text"
            v-model="producto.cantidadDisponible"
            placeholder="Ej: 50"
            maxlength="10"
            @input="soloNumeros('cantidadDisponible')"
          />
          <span v-if="errores.cantidadDisponible" class="error">{{ errores.cantidadDisponible }}</span>
        </div>

        <div class="botones">
          <button type="button" class="btn-cancelar" @click="limpiarCampos">
            Cancelar
          </button>
          <button type="submit" class="btn-confirmar">Confirmar</button>
        </div>
      </form>
    </div>
  </div>
</template>
  
<script>
import axios from 'axios'
import { obtenerTiposProducto } from '@/providers/TipoProductoProvider'
import { obtenerEstadosProducto } from '@/providers/EstadoProductoProvider'

export default {
  name: 'AgregarProductoView',
  data() {
    return {
      producto: {
        nombre: '',
        precio: 0,
        tipoProductoId: '',
        estadoProductoId: '',
        cantidadDisponible: 0
      },
      errores: {},
      tipos: [],
      estados: []
    }
  },
  async mounted() {
    this.tipos = await obtenerTiposProducto()
    this.estados = await obtenerEstadosProducto()
  },
  methods: {
    soloNumeros(campo) {
      this.producto[campo] = this.producto[campo].replace(/[^0-9.]/g, '')
    },
    validarCampos() {
      this.errores = {}

      if (!this.producto.nombre) {
        this.errores.nombre = 'Este campo es obligatorio'
      }

      if (!this.producto.tipoProductoId) {
        this.errores.tipo = 'Este campo es obligatorio'
      }

      const precioNum = parseFloat(this.producto.precio)
      if (
        this.producto.precio === '' ||
        this.producto.precio === null ||
        isNaN(precioNum) ||
        precioNum < 0
      ) {
        this.errores.precio = 'Este campo es obligatorio'
      }

      if (!this.producto.estadoProductoId) {
        this.errores.estado = 'Este campo es obligatorio'
      }

      const cantidadNum = parseInt(this.producto.cantidadDisponible)
      if (
        this.producto.cantidadDisponible === '' ||
        this.producto.cantidadDisponible === null ||
        isNaN(cantidadNum) ||
        cantidadNum < 0
      ) {
        this.errores.cantidadDisponible =
          'Este campo es obligatorio y debe ser un número mayor o igual a 0'
      }

      return Object.keys(this.errores).length === 0
    },
    async guardarProducto() {
      if (!this.validarCampos()) return

      try {
        const token = localStorage.getItem('token')
        const productoEnviar = {
          nombre: this.producto.nombre,
          precio: parseFloat(this.producto.precio),
          tipoProductoId: parseInt(this.producto.tipoProductoId),
          estadoProductoId: parseInt(this.producto.estadoProductoId),
          cantidadDisponible: parseInt(this.producto.cantidadDisponible) || 0
        }

        const response = await axios.post(
          'http://localhost:8080/productos/agregar',
          productoEnviar,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`
            }
          }
        )

        alert('Producto guardado con éxito ✅')
        console.log('Respuesta del backend:', response.data)
        this.limpiarCampos()
      } catch (error) {
        console.error('Error al guardar el producto:', error)
        let mensajeError = 'Hubo un error al guardar el producto ❌'

        if (error.response) {
          mensajeError =
            error.response.data?.message ||
            error.response.data ||
            mensajeError
        } else if (error.request) {
          mensajeError =
            'No se pudo conectar con el servidor. Verifica que el backend esté ejecutándose.'
        } else {
          mensajeError = 'Error al configurar la petición: ' + error.message
        }

        alert(mensajeError)
      }
    },
    confirmar() {
      this.guardarProducto()
    },
    limpiarCampos() {
      this.producto = {
        nombre: '',
        precio: 0,
        tipoProductoId: '',
        estadoProductoId: '',
        cantidadDisponible: 0
      }
      this.errores = {}
    }
  }
}
</script>
  
  
  <style scoped>
  .agregar-producto-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 85vh;
    background: #fffaf3;
    font-family: 'Segoe UI', sans-serif;
  }
  
  .tarjeta {
    background: #fff;
    width: 550px;
    padding: 40px 50px;
    border-radius: 20px;
    box-shadow: inset 0 0 0 2px #f8c471;
    border: 1px solid #f5cba7;
    transition: all 0.3s ease;
  }
  
  .tarjeta:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  }
  
  .titulo {
    text-align: center;
    color: #e67e22;
    margin-bottom: 25px;
    font-size: 1.6rem;
    font-weight: 700;
  }
  
  .campo {
    display: flex;
    flex-direction: column;
    margin-bottom: 18px;
  }
  
  label {
    font-weight: 600;
    color: #4d2c0c;
    margin-bottom: 6px;
    font-size: 0.95rem;
  }
  
  input,
  select {
    padding: 10px 12px;
    border-radius: 8px;
    border: 1.5px solid #ccc;
    font-size: 15px;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
  }
  
  input:focus,
  select:focus {
    border-color: #f8c471;
    outline: none;
    box-shadow: 0 0 0 3px rgba(248, 196, 113, 0.3);
  }
  
  .error {
    font-size: 13px;
    color: #c0392b;
    margin-top: 3px;
    font-weight: bold;
  }
  
  .botones {
    display: flex;
    justify-content: space-between;
    margin-top: 25px;
  }
  
  button {
    flex: 1;
    margin: 0 5px;
    padding: 12px;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    font-size: 15px;
    transition: background-color 0.3s ease, transform 0.2s ease;
    cursor: pointer;
  }
  
  .btn-cancelar {
    background-color: #e74c3c;
    color: white;
  }
  
  .btn-cancelar:hover {
    background-color: #c0392b;
    transform: scale(1.05);
  }
  
  .btn-confirmar {
    background-color: #f8c471;
    color: #4d2c0c;
  }
  
  .btn-confirmar:hover {
    background-color: #f5b041;
    transform: scale(1.05);
  }
  </style>
  