<template>
  <div class="layout">
    <aside class="sidebar">
      <h2 class="logo">BUDDYFINV</h2>
      <nav>
        <button
          v-if="rolUsuario === 'ADMIN'"
          @click="ir('dashboard')"
          :class="{ activo: ruta === 'dashboard' }"
        >
          <IconDashboard />
          <span>DASHBOARD</span>
        </button>
        <button @click="ir('acciones')" :class="{ activo: ruta === 'acciones' }">
          <IconAcciones />
          <span>ACCIONES</span>
        </button>
        <button @click="ir('inventario')" :class="{ activo: ruta === 'inventario' }">
          <IconInventario />
          <span>INVENTARIO</span>
        </button>
        <button @click="ir('graficas')" :class="{ activo: ruta === 'graficas' }">
          <IconGraficas />
          <span>GRAFICAS</span>
        </button>
        <div class="separator"></div>
        <button @click="ir('configuracion')" :class="{ activo: ruta === 'configuracion' }">
          <IconConfiguracion />
          <span>CONFIGURACION</span>
        </button>
      </nav>
      <button class="cerrar-sesion" @click="confirmLogout = true ">
        <IconCerrarSesion />
        <span>CERRAR SESIÓN</span>
      </button>
    </aside>

    <main class="contenido">
      <div class="top-bar">
        <div class="fecha-hora">{{ fechaHora }}</div>
        <div class="usuario-info">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="40" fill="currentColor" viewBox="0 0 16 16">
            <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10z"/>
          </svg>
          <span>{{ username }}</span>
        </div>
      </div>

      <RouterView />

      <div v-if="confirmLogout" class="confirm-overlay">
        <div class="confirm-box">
          <p>¿Está seguro que desea cerrar la sesión?</p>
          <div class="confirm-actions">
            <button class="btn-confirm" @click="handleLogout">Confirmar</button>
            <button class="btn-cancel" @click="confirmLogout = false">Cancelar</button>
          </div>
        </div>
      </div>

      <div v-if="showSuccess" class="success-message">
        Sesión cerrada correctamente.
      </div>

    </main>
  </div>
</template>

<script>
import { jwtDecode } from 'jwt-decode'
import IconDashboard from '../components/icons/IconDashboard.vue'
import IconAcciones from '../components/icons/IconAcciones.vue'
import IconInventario from '../components/icons/IconInventario.vue'
import IconGraficas from '../components/icons/IconGraficas.vue'
import IconConfiguracion from '../components/icons/IconConfiguracion.vue'
import IconCerrarSesion from '../components/icons/IconCerrarSesion.vue'
import { useUsuarioStore } from '@/stores/usuarioStore'

export default {
  components: {
    IconDashboard,
    IconAcciones,
    IconInventario,
    IconGraficas,
    IconConfiguracion,
    IconCerrarSesion
  },
  data() {
    return {
      ruta: this.$route.path === '/dashboard/dashboard' ? 'dashboard' : this.$route.name,
      rolUsuario: '',
      fechaHora: '',
      username: '',
      confirmLogout: false,
      showSuccess: false,
      usuarioStore: null,
    }
  },
  mounted() {
    const token = localStorage.getItem('token')
    if (!token) {
      this.$router.push('/login')
      return
    }

    try {
      const decoded = jwtDecode(token)
      this.rolUsuario = decoded.rol || decoded.authorities?.[0] || ''
      this.username = decoded.sub || 'USUARIO'
    } catch (error) {
      console.error('Token inválido:', error)
      this.$router.push('/login')
      return
    }

    this.usuarioStore = useUsuarioStore()
    this.actualizarFechaHora()
    this._intervaloFecha = setInterval(this.actualizarFechaHora, 60000)
  },
  watch: {
    '$route.name'(nueva) {
      this.ruta = nueva
    },
    '$route.path'(nueva) {
      if (nueva === '/dashboard/dashboard') {
        this.ruta = 'dashboard'
      }
    }
  },
  methods: {
    actualizarFechaHora() {
      const ahora = new Date()
      const dias = ['DOMINGO', 'LUNES', 'MARTES', 'MIÉRCOLES', 'JUEVES', 'VIERNES', 'SÁBADO']
      const meses = ['ENERO','FEBRERO','MARZO','ABRIL','MAYO','JUNIO','JULIO','AGOSTO','SEPTIEMBRE','OCTUBRE','NOVIEMBRE','DICIEMBRE']
      const diaSemana = dias[ahora.getDay()]
      const dia = ahora.getDate()
      const mes = meses[ahora.getMonth()]
      const año = ahora.getFullYear()
      const horas = String(ahora.getHours()).padStart(2, '0')
      const minutos = String(ahora.getMinutes()).padStart(2, '0')
      this.fechaHora = `${diaSemana}, ${dia} DE ${mes} DE ${año}, ${horas}:${minutos}`
    },
    ir(nombreRuta) {
      if (nombreRuta === 'dashboard') {
        this.$router.push('/dashboard/dashboard')
      } else {
        this.$router.push({ name: nombreRuta })
      }
    },
    async handleLogout(){
      try{
        await this.usuarioStore.cerrarSesion()
        this.confirmLogout = false
        this.showSuccess = true
        this.$router.replace({ name: 'Login', query: { mensaje: 'Sesión cerrada correctamente' } })
      }catch(error){
        console.error('Error al cerrar sesión:', error)
      }
    }

    
        
  },
  beforeUnmount() {
    if (this._intervaloFecha) {
      clearInterval(this._intervaloFecha)
    }
  },
  beforeUnmount() {
    if (this._intervaloFecha) {
      clearInterval(this._intervaloFecha)
    }
  },
  beforeUnmount() {
    if (this._intervaloFecha) {
      clearInterval(this._intervaloFecha)
    }
  }
}
</script>
  
<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
  font-family: 'Segoe UI', sans-serif;
  background-color: #fdf6ec;
}

.sidebar {
  width: 240px;
  background-color: #fff3d4;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.logo {
  font-size: 1.5rem;
  color: #d35400;
  margin-bottom: 2rem;
}

nav button {
  background: none;
  border: none;
  padding: 0.8rem;
  text-align: left;
  font-size: 1rem;
  color: #a84300;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  transition: background-color 0.2s;
}

nav button svg {
  flex-shrink: 0;
}

nav button.activo {
  background-color: #f8e0c0;
  font-weight: bold;
}

nav button:hover {
  background-color: #ffe6cc;
}

.separator {
  height: 1px;
  background-color: rgba(168, 67, 0, 0.2);
  margin: 0.5rem 0;
}

.cerrar-sesion {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 0.8rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  justify-content: center;
}

.cerrar-sesion svg {
  flex-shrink: 0;
}

.contenido {
  flex: 1;
  padding: 0;
  background-color: #ffffff;
  min-height: 100vh;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background-color: #ffffff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.fecha-hora,
.usuario-info {
  font-size: 1.2rem;
  color: #333;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* Modal de confirmación */
.confirm-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
}

.confirm-box {
  background: #fff;
  color: black;
  padding: 1.5rem 2rem;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.confirm-actions {
  margin-top: 1rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.btn-confirm {
  background: #ffbc21;
  color: rgb(0, 0, 0);
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
}
.btn-confirm:hover {
  background: #ffbc21;
}

.btn-cancel {
  background: #6c757d;
  color: rgb(0, 0, 0);
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
}
.btn-cancel:hover {
  background: #5a6268;
}

/* Mensaje de éxito */
.success-message {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: #a7a328;
  color: rgb(0, 0, 0);
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  z-index: 100;
  animation: fadeInOut 2s ease forwards;
}

@keyframes fadeInOut {
  0% { opacity: 0; transform: translateY(20px); }
  10% { opacity: 1; transform: translateY(0); }
  90% { opacity: 1; }
  100% { opacity: 0; transform: translateY(20px); }
}
</style>