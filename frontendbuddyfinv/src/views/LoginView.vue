<template>
  <div class="login-container">
    <div v-if="mensajeExito" class="success-message">
      {{ mensajeExito }}
    </div>

    <h2 class="logo">BUDDYFINV</h2>

    <div class="login-box">
      <h2>Iniciar Sesión</h2>

      <!-- Campo Usuario -->
      <div class="input-group" :class="{ error: mensajeUsuario }">
        <input
          v-model="usuario"
          placeholder="Ingrese su usuario (obligatorio)"
        />
        <span class="icon">
          <span v-if="usuario && usuarioValido">✅</span>
          <span v-else-if="usuario">❌</span>
        </span>
      </div>
      <p v-if="mensajeUsuario" class="error-text">{{ mensajeUsuario }}</p>

      <!-- Campo Contraseña -->
      <div class="input-group" :class="{ error: mensajePassword }">
        <input
          :type="mostrarPassword ? 'text' : 'password'"
          v-model="password"
          placeholder="Ingrese su contraseña (obligatorio)"
        />
        <span class="icon">
          <span v-if="password && passwordValida">✅</span>
          <span v-else-if="password">❌</span>
        </span>
        <span class="ojo" @click="mostrarPassword = !mostrarPassword">
          {{ mostrarPassword ? '🙈' : '👁️' }}
        </span>
      </div>
      <p v-if="mensajePassword" class="error-text">{{ mensajePassword }}</p>

      <!-- Recordar usuario -->
      <div class="recordar">
        <input type="checkbox" v-model="recordar" id="recordar" />
        <label for="recordar" class="recordar-label">Recordar usuario</label>
      </div>

      <!-- Botón -->
      <button :disabled="!formValido || bloqueo" @click="iniciarSesion">
        Iniciar sesión
      </button>

      <p v-if="mensajeError && !mensajeUsuario && !mensajePassword" class="error-text">
        {{ mensajeError }}
      </p>

      <div class="links">
        <router-link to="/registro">Registrar negocio</router-link>
        <a href="#">Recuperar usuario</a> |
        <a href="#">Recuperar contraseña</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useUsuarioStore } from '../stores/usuarioStore'
import { useRouter } from 'vue-router'

const usuario = ref('')
const password = ref('')
const recordar = ref(false)
const mensajeUsuario = ref('')
const mensajePassword = ref('')
const mensajeError = ref('')
const usuarioValido = ref(false)
const passwordValida = ref(false)
const mostrarPassword = ref(false)
const bloqueo = ref(false)
const intentos = ref(0)
const mensajeExito = ref('')

const usuarioStore = useUsuarioStore()
const router = useRouter()

const validarUsuario = () => {
  const regex = /^[a-zA-Z0-9]{8,20}$/
  usuarioValido.value = regex.test(usuario.value)
  mensajeUsuario.value = usuarioValido.value
    ? ''
    : 'El campo usuario debe tener entre 8 y 20 caracteres alfanuméricos, sin espacios en blanco.'
}

const validarPassword = () => {
  const regex = /^(?=.*[A-Z])(?=.*\d)(?=.*[\W_])[^\s]{8,30}$/
  passwordValida.value = regex.test(password.value)
  mensajePassword.value = passwordValida.value
    ? ''
    : 'El campo contraseña debe tener entre 8 y 30 caracteres, incluir una mayúscula, un número y un carácter especial (sin espacios en blanco).'
}

watch(usuario, () => {
  validarUsuario()
  mensajeError.value = ''
})

watch(password, () => {
  validarPassword()
  mensajeError.value = ''
})

const formValido = computed(() => usuarioValido.value && passwordValida.value)

const iniciarSesion = async () => {
  try {
    const res = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: usuario.value.trim(),
        password: password.value.trim()
      })
    })

    if (!res.ok) {
      const text = await res.text()

      mensajeUsuario.value = ''
      mensajePassword.value = ''
      mensajeError.value = ''

      if (res.status === 404) {
        mensajeUsuario.value = text
      } else if (res.status === 401) {
        mensajePassword.value = text
      } else if (res.status === 400) {
        mensajeError.value = text
      }

      throw new Error(text)
    }

    const data = await res.json()
    localStorage.setItem('token', data.token)
    usuarioStore.establecerDatosDesdeToken(data.token)

    if (recordar.value) localStorage.setItem('usuario', usuario.value)
    else localStorage.removeItem('usuario')

    router.push('/dashboard')
  } catch (err) {
    intentos.value++
    if (intentos.value >= 3) {
      bloqueo.value = true
      mensajeError.value = 'Límite de intentos alcanzado. Por favor, intente de nuevo en 30 segundos.'
      setTimeout(() => {
        bloqueo.value = false
        intentos.value = 0
      }, 30000)
    } else {
      mensajeError.value = err.message
    }
  }
}

onMounted(() => {
  const guardado = localStorage.getItem('usuario')
  if (guardado) {
    usuario.value = guardado
    recordar.value = true
  }
  const mensaje = router.currentRoute.value.query.mensaje
  if (mensaje) {
    mensajeExito.value = mensaje
    setTimeout(() => {
      mensajeExito.value = ''
      router.replace({ query: {} })
    }, 2000)
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@100..900&display=swap');

.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  justify-content: center;
  height: 100vh;
  width: 100vw;
  background-image: url('@/assets/FondoLogin.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  font-family: 'Segoe UI', sans-serif;
  overflow: hidden;
}

.logo {
  font-family: 'Outfit', sans-serif;
  font-weight: 900;
  font-size: 3.4rem;
  color: coral;
}

.login-box {
  font-family: 'Outfit', sans-serif;
  background: rgba(255, 255, 255, 0.85); /* fondo blanco translúcido */
  font-weight: 800;
  padding: 20px;
  border-radius: 30px;
  height: 50%;
  width: 360px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

h2 {
  font-family: 'Outfit', sans-serif;
  text-align: center;
  font-weight: 700;
  font-size: 2rem;
  color: #575757;
  margin-bottom: 40px;
}

.input-group {
  position: relative;
  margin-bottom: 25px;
}

.input-group input {
  width: 100%;
  padding: 4%;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f5f5f5;
}

.input-group input::placeholder {
  color: #999;
  opacity: 1;
}

.input-group.error input {
  border-color: red;
}

.icon {
  position: absolute;
  right: 35px;
  top: 10px;
  font-size: 1.1em;
}

.ojo {
  position: absolute;
  right: 10px;
  top: 10px;
  cursor: pointer;
}

.recordar {
  margin: 10px 0;
}

.recordar-label {
  color: #333;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #ff7b00;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.3s;
}

button:hover:enabled {
  background-color: #ff7b00;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.error-text {
  color: red;
  font-size: 0.9em;
}

.links {
  margin-top: 15px;
  font-size: 0.9em;
  text-align: center;
}

.links a {
  color: #004aad;
  text-decoration: none;
}

.success-message {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: #1ad063;
  color: #f3f3f3;
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  z-index: 100;
  font-weight: bold;
}
</style>
