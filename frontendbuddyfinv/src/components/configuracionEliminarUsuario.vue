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
          <button class="btn-icon" @click="abrirFormulario(usuario)">✏️</button>
          <button class="btn-icon" @click="confirmarEliminacion(usuario)">🗑️</button>
        </span>
      </div>

      <!-- Modal de edición -->
      <div v-if="mostrarFormulario" class="modal-overlay">
        <div class="modal">
          <h3>Editar Usuario</h3>
          <form @submit.prevent="guardarCambios">
            <label>Nombre</label>
            <input 
              v-model="usuarioSeleccionado.nombre" 
              type="text" 
              maxlength="100" 
              required 
              placeholder="Nombre completo (máx. 100 caracteres)" 
            />

            <label>Correo</label>
            <input 
              v-model="usuarioSeleccionado.email" 
              type="email" 
              maxlength="100" 
              required 
              placeholder="ejemplo@correo.com" 
            />

            <label>Usuario</label>
            <input 
              v-model="usuarioSeleccionado.usuario" 
              type="text" 
              maxlength="30" 
              pattern="^[a-zA-Z0-9]+$" 
              required 
              placeholder="Usuario alfanumérico (máx. 30 caracteres)" 
            />

            <label>Contraseña (opcional)</label>
            <input 
              v-model="usuarioSeleccionado.password" 
              type="password" 
              minlength="8" 
              placeholder="Mínimo 8 caracteres" 
            />

            <div class="modal-buttons">
              <button type="submit" class="btn-confirmar">Guardar</button>
              <button type="button" class="btn-cancelar" @click="cerrarFormulario">Cancelar</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Modal de confirmación eliminación -->
      <div v-if="mostrarConfirmacion" class="modal-overlay">
        <div class="modal">
          <p>
            ¿Deseas eliminar al usuario <strong>{{ usuarioSeleccionado.nombre }}</strong>
            (ID: {{ usuarioSeleccionado.id }}) del sistema?
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
import UsuarioUpdateDTO from '@/models/UsuarioUpdateDTO.js'

export default {
  name: 'UsuarioTable',

  data() {
    return {
      usuarios: [],
      usuarioSeleccionado: null,
      mostrarConfirmacion: false,
      mostrarFormulario: false
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

    abrirFormulario(usuario) {
      // Clonar el objeto para no modificar directamente la lista
      this.usuarioSeleccionado = { ...usuario }
      this.mostrarFormulario = true
    },

    cerrarFormulario() {
      this.usuarioSeleccionado = null
      this.mostrarFormulario = false
    },

    async guardarCambios() {
      try {
        const dto = new UsuarioUpdateDTO(this.usuarioSeleccionado)
        const actualizado = await UsuarioProvider.updateUsers(this.usuarioSeleccionado.id, dto.toRequest())

        actualizado.nitUsuario = actualizado.nitUsuario || this.usuarioSeleccionado.nitUsuario

        

        // Actualizar la lista local con los cambios
        const index = this.usuarios.findIndex(u => u.id === actualizado.id)
        if (index !== -1) {
          this.usuarios.splice(index, 1, actualizado)
        }

        alert('Usuario actualizado exitosamente')
      } catch (error) {
        alert('Error al actualizar usuario: ' + error.message)
      } finally {
        this.cerrarFormulario()
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
        this.usuarios = this.usuarios.filter(u => u.id !== this.usuarioSeleccionado.id)
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
/* Modal de edición */
.modal {
  background: #fffaf3; /* fondo suave */
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.25);
  text-align: left;
  font-size: 16px;
  color: #222; /* texto negro */
  border: 2px solid #f8c471; /* borde naranja suave */
}

/* Título del formulario */
.modal h3 {
  color: #e67e22; /* naranja más intenso */
  font-size: 1.6rem;
  margin-bottom: 1rem;
  text-align: center;
}

/* Labels */
.modal label {
  display: block;
  margin-top: 12px;
  font-weight: bold;
  color: #333; /* negro */
}

/* Inputs */
.modal input {
  width: 100%;
  padding: 10px;
  margin-top: 6px;
  border: 1px solid #f8c471;
  border-radius: 8px;
  font-size: 14px;
  color: #222;
  background-color: #fff; /* blanco para contraste */
  transition: border-color 0.2s, box-shadow 0.2s;
}

.modal input:focus {
  border-color: #e67e22;
  box-shadow: 0 0 6px rgba(230, 126, 34, 0.4);
  outline: none;
}

/* Botones del formulario */
.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
}

.modal-buttons .btn-confirmar {
  background-color: #f8c471; /* naranja suave */
  color: #fff; /* texto blanco */
  border: none;
  border-radius: 10px;
  padding: 10px 20px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.modal-buttons .btn-confirmar:hover {
  background-color: #e67e22; /* naranja más intenso */
}

.modal-buttons .btn-cancelar {
  background-color: #e74c3c; /* rojo para cancelar */
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 20px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.modal-buttons .btn-cancelar:hover {
  background-color: #c0392b;
}

.user-table-container {
  width: 90%;
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', sans-serif;
}

/* Título */
.title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
}

/* Tabla */
.table-header,
.table-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
}

.table-header {
  background: #f8c471;
  color: #4d2c0c;
  text-transform: uppercase;
  font-weight: bold;
}

.table-row {
  background: #fdf6ec;
  margin-top: 8px;
  transition: 0.2s;
}

.table-row:nth-child(even) {
  background: #faebd7;
}

.table-row:hover {
  background: #f5cba7;
  transform: scale(1.01);
}

.cell {
  color: #333;
}

/* Scroll */
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

/* Botones */
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

.btn-confirmar {
  background-color: #27ae60;
  color: white;
}

.btn-confirmar:hover {
  background-color: #1e8449;
}

.btn-cancelar {
  background-color: #e74c3c;
  color: white;
}

.btn-cancelar:hover {
  background-color: #c0392b;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: #fffaf3;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  text-align: center;
  font-size: 19px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
}

/* Iconos */
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
</style>