<template>
  <div class="login-container">
    <div v-if="mensajeExito" class="success-message">
      {{ mensajeExito }}
    </div>
    <img src="@/assets/logo.svg" alt="Logo del sistema" class="logo" />

    <div class="login-box">
      <h2>Iniciar Sesión</h2>

      <!-- Campo Usuario -->
      <div class="input-group" :class="{ error: mensajeUsuario }">
        <input
          v-model="usuario"
          placeholder="Ingrese su usuario (obligatorio)"
          @input="validarUsuario"
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
          @input="validarPassword"
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

      <p v-if="mensajeError" class="error-text">{{ mensajeError }}</p>

      <div class="links">
        <router-link to="/registro">Registrar negocio</router-link>
        <a href="#">Recuperar usuario</a> |
        <a href="#">Recuperar contraseña</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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
const mensajeExito = ref('') // Nuevo estado para mensaje de éxito

const usuarioStore = useUsuarioStore()
const router = useRouter()

// ✅ Validación usuario
const validarUsuario = () => {
  const regex = /^[a-zA-Z0-9]{8,20}$/
  usuarioValido.value = regex.test(usuario.value)
  mensajeUsuario.value = usuarioValido.value
    ? ''
    : 'El campo usuario debe tener entre 8 y 20 caracteres alfanuméricos, sin espacios en blanco.'
}

// ✅ Validación contraseña
const validarPassword = () => {
  const regex = /^(?=.*[A-Z])(?=.*\d)(?=.*[\W_])[^\s]{8,30}$/
  passwordValida.value = regex.test(password.value)
  mensajePassword.value = passwordValida.value
    ? ''
    : 'El campo contraseña debe tener entre 8 y 30 caracteres, incluir una mayúscula, un número y un carácter especial (sin espacios en blanco).'
}

// ✅ Formulario válido
const formValido = computed(() => usuarioValido.value && passwordValida.value)

// ✅ Lógica login con backend JWT
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

      if (res.status === 404){ 
        mensajeUsuario.value = text
        mensajePassword.value= ''
      }else if (res.status == 401) {
        mensajePassword.value = text
        mensajeUsuario.value = ''
      }else if (res.status == 400){
        mensajePassword.value = text
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

// ✅ Recordar usuario si estaba guardado
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
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  width: 100vw;
  background: #eef2f5;
  font-family: 'Segoe UI', sans-serif;
  overflow: hidden;
}

.logo {
  width: 140px;
  margin-bottom: 15px;
}
.login-box {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 360px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}
h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
.input-group {
  position: relative;
  margin-bottom: 15px;
}
.input-group input {
  width: 100%;
  padding: 10px;
  padding-right: 35px;
  border: 1px solid #ccc;
  border-radius: 8px;
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
  background-color: #004aad;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.3s;
}
button:hover:enabled {
  background-color: #003a8c;
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
