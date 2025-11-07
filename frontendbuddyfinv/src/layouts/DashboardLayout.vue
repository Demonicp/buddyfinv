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
          Dashboard
        </button>
        <button @click="ir('acciones')" :class="{ activo: ruta === 'acciones' }">Acciones</button>
        <button @click="ir('inventario')" :class="{ activo: ruta === 'inventario' }">Inventario</button>
        <button @click="ir('graficas')" :class="{ activo: ruta === 'graficas' }">Gráficas</button>
        <button @click="ir('configuracion')" :class="{ activo: ruta === 'configuracion' }">Configuración</button>
      </nav>
      <button class="cerrar-sesion" @click="cerrarSesion">Cerrar sesión</button>
    </aside>

    <main class="contenido">
      <RouterView />
    </main>
  </div>
</template>

<script>
import { jwtDecode } from 'jwt-decode'

export default {
  data() {
    return {
      ruta: this.$route.name,
      rolUsuario: ''
    }
  },
  mounted() {
    const token = localStorage.getItem('token')
    if (token) {
      const decoded = jwtDecode(token)
      this.rolUsuario = decoded.rol || decoded.authorities?.[0] || ''
    }
  },
  watch: {
    '$route.name'(nueva) {
      this.ruta = nueva
    }
  },
  methods: {
    ir(nombreRuta) {
      if (nombreRuta === 'dashboard') {
        this.$router.push('/dashboard/dashboard') // ✅ redirección correcta
      } else {
        this.$router.push({ name: nombreRuta })
      }
    },
    cerrarSesion() {
      this.$router.push({ name: 'Login' })
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
  }
  
  nav button.activo {
    background-color: #f8e0c0;
    font-weight: bold;
  }
  
  nav button:hover {
    background-color: #ffe6cc;
  }
  
  .cerrar-sesion {
    background-color: #e74c3c;
    color: white;
    border: none;
    padding: 0.8rem;
    border-radius: 6px;
    cursor: pointer;
    font-weight: bold;
  }
  
  .contenido {
    flex: 1;
    padding: 2rem;
  }
  </style>