<template>
    <div class="agregar-producto-container">
      <div class="tarjeta">
        <h2 class="titulo">Añadir Producto</h2>
  
      <!--  
          <div class="campo">
            <label for="codigo">Código:</label>
            <input
              id="codigo"
              type="text"
              v-model="producto.codigo"
              placeholder="Máximo 6 dígitos"
              maxlength="6"
              @input="soloNumeros('codigo')"
            />
            <span v-if="errores.codigo" class="error">{{ errores.codigo }}</span>
          </div>
          -->
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
              <option value="1">bebidas</option>
              <option value="2">comidas</option>
              <option value="3">Postres</option>
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
              <option value="1">Activo</option>
              <option value="2">Inactivo</option>
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
  import axios from 'axios';
  
  export default {
    name: "AgregarProductoView",
    data() {
      return {
        producto: {
          nombre: "",
          precio: 0,
          tipoProductoId: "",
          estadoProductoId: "",
          cantidadDisponible: 0,
            },
        errores: {},
      };
    },

  
    methods: {
      soloNumeros(campo) {
        // Evita letras o símbolos
        this.producto[campo] = this.producto[campo].replace(/[^0-9.]/g, "");
      },
  
      validarCampos() {
        this.errores = {};
/*        if (!this.producto.codigo)
          this.errores.codigo = "Este campo es obligatorio";
        else if (this.producto.codigo.length > 6)
          this.errores.codigo = "Código demasiado largo";   */
  
        if (!this.producto.nombre)
          this.errores.nombre = "Este campo es obligatorio";
  
        if (!this.producto.tipoProductoId)
          this.errores.tipo = "Este campo es obligatorio";
  
        const precioNum = parseFloat(this.producto.precio);
        if (this.producto.precio === "" || this.producto.precio === null || this.producto.precio === undefined || isNaN(precioNum) || precioNum < 0)
          this.errores.precio = "Este campo es obligatorio";
  
        if (!this.producto.estadoProductoId)
          this.errores.estado = "Este campo es obligatorio";
  
        const cantidadNum = parseInt(this.producto.cantidadDisponible);
        if (this.producto.cantidadDisponible === "" || this.producto.cantidadDisponible === null || this.producto.cantidadDisponible === undefined || isNaN(cantidadNum) || cantidadNum < 0)
          this.errores.cantidadDisponible = "Este campo es obligatorio y debe ser un número mayor o igual a 0";
  
        return Object.keys(this.errores).length === 0;
      },
  
      async guardarProducto() {
        if (this.validarCampos()) {
          try {
          const token = localStorage.getItem("token");
          
          // Convertir los datos al formato correcto para el backend
          const productoEnviar = {
            nombre: this.producto.nombre,
            precio: parseFloat(this.producto.precio),
            tipoProductoId: parseInt(this.producto.tipoProductoId),
            estadoProductoId: parseInt(this.producto.estadoProductoId),
            cantidadDisponible: parseInt(this.producto.cantidadDisponible) || 0
          };
          
          console.log("Datos a enviar:", productoEnviar);
          
          const response = await axios.post(
            "http://localhost:8080/productos/agregar",
            productoEnviar,
            {
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
            }
            );
            alert("Producto guardado con éxito ✅");
            console.log("Respuesta del backend:", response.data);
            this.limpiarCampos();
          } catch (error) {
            console.error("Error al guardar el producto:", error);
            let mensajeError = "Hubo un error al guardar el producto ❌";
            
            if (error.response) {
              // El servidor respondió con un código de estado fuera del rango 2xx
              mensajeError = error.response.data?.message || error.response.data || mensajeError;
              console.error("Detalles del error:", error.response.data);
              console.error("Status:", error.response.status);
            } else if (error.request) {
              // La petición fue hecha pero no se recibió respuesta
              console.error("No se recibió respuesta del servidor");
              mensajeError = "No se pudo conectar con el servidor. Verifica que el backend esté ejecutándose.";
            } else {
              // Algo pasó al configurar la petición
              console.error("Error al configurar la petición:", error.message);
            }
            
            alert(mensajeError);
          }
        }
      },
      
      confirmar() {
        this.guardarProducto();   
      },
  
      limpiarCampos() {
        this.producto = {
          nombre: "",
          precio: 0,
          tipoProductoId: "",
          estadoProductoId: "",
          cantidadDisponible: 0,
        };
        this.errores = {};
      },
    },
  };
  </script>
  
  
  <style scoped>
  .agregar-producto-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 85vh;
    background: #f9fafc;
  }
  
  .tarjeta {
    background: white;
    width: 550px;
    padding: 40px 50px;
    border-radius: 20px;
    box-shadow: 0 6px 25px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
  }
  
  .tarjeta:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 35px rgba(0, 90, 255, 0.2);
  }
  
  .titulo {
    text-align: center;
    color: #1e3a8a;
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
    color: #1e293b;
    margin-bottom: 6px;
  }
  
  input,
  select {
    padding: 10px 12px;
    border-radius: 8px;
    border: 1.5px solid #cbd5e1;
    font-size: 15px;
    transition: all 0.2s;
  }
  
  input:focus,
  select:focus {
    border-color: #3b82f6;
    outline: none;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
  }
  
  .error {
    font-size: 13px;
    color: #dc2626;
    margin-top: 3px;
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
    transition: all 0.3s ease;
    cursor: pointer;
  }
  
  .btn-cancelar {
    background-color: #ef4444;
    color: white;
  }
  
  .btn-cancelar:hover {
    background-color: #dc2626;
    transform: scale(1.05);
  }
  
  .btn-confirmar {
    background-color: #3b82f6;
    color: white;
  }
  
  .btn-confirmar:hover {
    background-color: #2a59bd;
    transform: scale(1.05);
  }
  </style>
  