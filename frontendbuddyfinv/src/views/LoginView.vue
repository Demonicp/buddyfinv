<template>
  <div class="login-container">
    <h2>Iniciar Sesión</h2>
    <form @submit.prevent="iniciarSesion">
      <input v-model="usuario" type="text" placeholder="Usuario" required />
      <input v-model="clave" type="password" placeholder="Contraseña" required />
      <button type="submit">Ingresar</button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUsuarioStore } from '../stores/usuarioStore'
import { useRouter } from 'vue-router'

const usuario = ref('')
const clave = ref('')
const error = ref(null)
const usuarioStore = useUsuarioStore()
const router = useRouter()

async function iniciarSesion() {
  try {
    const res = await fetch('https://tu-api.com/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ usuario: usuario.value, clave: clave.value })
    })

    if (!res.ok) throw new Error('Usuario o contraseña incorrectos')

    const { token } = await res.json()
    localStorage.setItem('token', token)
    usuarioStore.establecerDatosDesdeToken(token)
    router.push('/ventas') // redirige a la vista principal
  } catch (e) {
    error.value = e.message
  }
}
</script>


<style scoped>

.login-container {
  max-width: 400px;
  margin: auto;
  padding: 2rem;
}
.error {
  color: red;
  margin-top: 1rem;
}


.login-view {
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #ff8800;
  font-size: 18px;
}

.error {
  text-align: center;
  padding: 2rem;
  color: #d32f2f;
  background: #ffebee;
  border-radius: 8px;
  font-size: 16px;
}
</style>
