<template>
  <div class="user-table-container">
    <h2 class="title">Lista de Usuarios</h2>

    <div class="venta-scroll">
    <div class="table-header">
      <span>ID</span>
      <span>Numero de documento</span>
      <span>Nombre</span>
      <span>Correo</span>
      <span>Usuario</span>
      <span>Acciones</span>
    </div>

    <div
      v-for="(usuario, index) in usuarios"
      :key="usuario.id || index"
      class="table-row"
    >
      <span class="cell">{{ usuario.id }}</span>
      <span class="cell">{{ usuario.nitUsuario }}</span>
      <span class="cell">{{ usuario.nombre }}</span>
      <span class="cell">{{ usuario.email }}</span>
      <span class="cell">{{ usuario.usuario }}</span>
      <span class="cell">
        <button class="btn-icon" @click="confirmarEliminacion(usuario)">🗑️</button>
      </span>
    </div>

    <!-- Modal de confirmación -->
    <div v-if="mostrarConfirmacion" class="modal-overlay">
      <div class="modal">
        <p>
          ¿Deseas eliminar al usuario <strong>{{ usuarioSeleccionado.nombre }}</strong>
          (ID: {{ usuarioSeleccionado.id }}) del sistema?
          Esta acción no se puede deshacer.
        </p>
        <div class="modal-buttons">
          <button class="btn-confirmar" @click="eliminarUsuarioConfirmado">Confirmar</button>
          <button class="btn-cancelar" @click="cancelarEliminacion">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>


<script>
import { UsuarioProvider } from '@/providers/UsuarioProvider'

export default {
  name: 'UsuarioTable',

  data() {
    return {
      usuarios: [],
      usuarioSeleccionado: null,
      mostrarConfirmacion: false
    }
  },

  async mounted() {
    await this.cargarUsuarios()
  },

  methods: {
    async cargarUsuarios() {
      try {
        const data = await UsuarioProvider.getAllUsuariosByPropietario()
        this.usuarios = Array.isArray(data) ? data : []
      } catch (error) {
        console.error('Error al obtener usuarios:', error.message)
      }
    },

    confirmarEliminacion(usuario) {
      this.usuarioSeleccionado = usuario
      this.mostrarConfirmacion = true
    },

    cancelarEliminacion() {
      this.usuarioSeleccionado = null
      this.mostrarConfirmacion = false
    },

    async eliminarUsuarioConfirmado() {
      try {
        await UsuarioProvider.eliminar(this.usuarioSeleccionado.id)
        this.usuarios = this.usuarios.filter(u => u.nitUsuario !== this.usuarioSeleccionado.nitUsuario)
        alert('Usuario eliminado exitosamente')
      } catch (error) {
        alert('Error al eliminar usuario: ' + error.message)
      } finally {
        this.cancelarEliminacion()
      }
    }
  }
}
</script>

  
  <style scoped>
  .user-table-container {
    width: 95%;
    max-width: 1200px;
    margin: 40px auto;
    padding: 20px;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    font-family: 'Futura', 'Garamond', avenir;
    
  }
  
  .title {
    text-align: center;
    color: #ff8800;
    margin-bottom: 20px;
    font-size: 22px;
    font-weight: bold;
  }
  
  .table-header,
  .table-row {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    padding: 12px 16px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
  }
  
  .table-header {
    background: #ff8800;
    color: white;
    text-transform: uppercase;
    font-weight: bold;
  }
  
  .table-row {
    background: #f1f1f1;
    margin-top: 8px;
    transition: 0.2s;
  }
  
  .table-row:nth-child(even) {
    background: #eaeaea;
  }
  
  .table-row:hover {
    background: #ffd9b3;
    transform: scale(1.01);
  }
  
  .cell {
    color: #333;
  }
  
.btn-icon {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #e74c3c;
}

.btn-icon:hover {
  color: #c0392b;
}

.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  text-align: center;
  font-size: 19px ;
  
}

.modal-buttons {
  margin-top: 20px;
  display: flex;
  justify-content: space-around;
}

.btn-confirmar {
  background-color: #2ecc71;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
}

.btn-cancelar {
  background-color: #e74c3c;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
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

  </style>