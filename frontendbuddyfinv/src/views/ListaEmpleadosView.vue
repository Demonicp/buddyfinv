<template> 
    <div class="page-container">
      <div class="box">
        <h2>Empleados</h2>
  
        <!-- 🔹 BOTÓN PARA AGREGAR EMPLEADO -->
        <button class="btn-agregar" @click="irAAgregarEmpleado">
          ➕ Agregar empleado
        </button>
  
        <p v-if="loading">Cargando...</p>
        <p v-if="!loading && empleados.length === 0">No hay empleados.</p>
  
        <!-- Encabezados -->
        <div class="row header-row">
          <div class="col">Documento</div>
          <div class="col">Nombre</div>
          <div class="col">Correo</div>
          <div class="col">Usuario</div>
        </div>
  
        <!-- Lista -->
        <div v-for="u in empleados" :key="u.id" class="row">
          <div class="col">{{ u.nitUsuario }}</div>
          <div class="col">{{ u.nombre }}</div>
          <div class="col">{{ u.email }}</div>
          <div class="col">{{ u.usuario }}</div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { UsuarioProvider } from '@/providers/UsuarioProvider.js'
  export default {
    name: 'ListaEmpleadosView',
    data() {
      return {
        empleados: [],
        loading: false,
        error: ''
      }
    },
    async mounted() {
      await this.cargar()
    },
    methods: {
      async cargar() {
        this.loading = true
        try {
          this.empleados = await UsuarioProvider.listarEmpleados()
        } catch (e) {
          this.error = e.message || 'Error al cargar empleados'
        } finally {
          this.loading = false
        }
      },
      // 🔹 Navega al formulario de agregar empleado
      irAAgregarEmpleado() {
        this.$router.push({ name: 'AgregarEmpleado' })
      }
    }
  }
  </script>
  
  <style scoped>
  .page-container {
    display: flex;
    justify-content: center;
    padding: 30px;
  }
  .box {
    width: 100%;
    max-width: 1000px;
    background: white;
    padding: 24px;
    border-radius: 12px;
    box-shadow: 0 6px 18px rgba(0, 0, 0, .06);
  }
  /* 🔹 Botón de agregar */
  .btn-agregar {
    background: #28a745;
    color: white;
    padding: 10px 16px;
    border-radius: 6px;
    border: none;
    cursor: pointer;
    margin: 12px 0 18px 0;
    font-size: 15px;
  }
  .btn-agregar:hover {
    background: #218838;
  }
  /* Encabezados */
  .header-row {
    background: #ff8800 !important;
    color: white !important;
    font-weight: bold;
    padding: 12px !important;
  }
  .row {
    display: grid;
    grid-template-columns: 1fr 2fr 2fr 1fr;
    padding: 10px;
    border-radius: 6px;
    background: #f6f6f6;
    margin-top: 8px;
  }
  .col {
    padding: 4px 8px;
  }
  @media (max-width: 768px) {
    .row {
      grid-template-columns: 1fr;
      gap: 8px;
    }
  }
  h2{
    color: #000 !important;
  }
  .row .col {
    color: #000 !important;  /* Fuerza negro SOLO en los datos */
    font-weight: 500;         /* Opcional: hace que se vea más nítido */
  }
  </style>