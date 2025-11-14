<template>
    <div class="registro-container">
      <div class="registro-box">
        <h2>Registro de Negocio</h2>
  
        <form @submit.prevent="registrarUsuario">
          <div class="form-grid">
            <!-- Nombre del administrador -->
            <div>
              <label>Nombre del Administrador *</label>
              <input
                v-model="form.nombre"
                type="text"
                placeholder="Ej. David Solarte"
                :class="{ invalido: errores.nombre }"
                @blur="validarCampo('nombre')"
              />
            </div>
  
            <!-- NIT -->
            <div>
              <label>NIT *</label>
              <input
                v-model="form.nitUsuario"
                type="text"
                placeholder="123456789"
                :class="{ invalido: errores.nitUsuario }"
                @blur="validarCampo('nitUsuario')"
              />
            </div>
  
            <!-- Email -->
            <div>
              <label>Email *</label>
              <input
                v-model="form.email"
                type="email"
                placeholder="correo@ejemplo.com"
                :class="{ invalido: errores.email }"
                @blur="validarCampo('email')"
              />
            </div>
  
            <!-- Nombre del negocio -->
            <div>
              <label>Nombre del Negocio *</label>
              <input
                v-model="form.negocio"
                type="text"
                placeholder="MiniMarket"
                :class="{ invalido: errores.negocio }"
                @blur="validarCampo('negocio')"
              />
            </div>
  
            <!-- Nombre de usuario -->
            <div>
              <label>Nombre de Usuario *</label>
              <input
                v-model="form.username"
                type="text"
                placeholder="DavidUser1"
                :class="{ invalido: errores.username }"
                @blur="validarCampo('username')"
              />
            </div>
  
            <!-- Contraseña -->
            <div>
              <label>Contraseña *</label>
              <input
                v-model="form.password"
                :type="mostrarPassword ? 'text' : 'password'"
                placeholder="Password@123"
                :class="{ invalido: errores.password }"
                @blur="validarCampo('password')"
              />
              <small @click="togglePassword" class="ver">
                {{ mostrarPassword ? 'Ocultar' : 'Mostrar' }}
              </small>
            </div>
  
            <!-- Confirmar contraseña -->
            <div class="full">
              <label>Confirmar Contraseña *</label>
              <input
                v-model="form.confirmPassword"
                :type="mostrarPassword ? 'text' : 'password'"
                placeholder="Password@123"
                :class="{ invalido: errores.confirmPassword }"
                @blur="validarCampo('confirmPassword')"
              />
            </div>
          </div>
  
          <button type="submit" :disabled="!formValido || esperandoRespuesta">
            {{ esperandoRespuesta ? "Registrando..." : "Registrarme" }}
          </button>
  
          <p class="mensaje" v-if="mensaje">{{ mensaje }}</p>
  
          <p class="volver">
            <router-link to="/login">Volver al login</router-link>
          </p>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import { UsuarioProvider } from "@/providers/UsuarioProvider.js";
  async function registrar() {
  const resultado = await UsuarioProvider.registrar(form.value)

  if (resultado.success) {
    alert('✅ ' + resultado.message)
    router.push('/login')
  } else {
    alert('⚠️ ' + resultado.message)
  }
}
  export default {
    name: "RegistroView",
    data() {
      return {
        form: {
          nombre: "",
          nitUsuario: "",
          email: "",
          negocio: "",
          username: "",
          password: "",
          confirmPassword: "",
        },
        mensaje: "",
        errores: {},
        mostrarPassword: false,
        esperandoRespuesta: false,
      };
    },
    computed: {
      formValido() {
        // Todos los campos deben estar llenos
        return Object.values(this.form).every((v) => v.trim() !== "");
      },
    },
    methods: {
      togglePassword() {
        this.mostrarPassword = !this.mostrarPassword;
      },
  
      validarCampo(campo) {
  const valor = this.form[campo]?.trim() || "";
  this.errores[campo] = false; // Limpia error previo

  switch (campo) {
    case "nombre":
      if (!valor || !/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/.test(valor)) 
        this.errores.nombre = true;
      break;

    case "nitUsuario":
      if (!valor || !/^\d+$/.test(valor)) 
        this.errores.nitUsuario = true;
      break;

    case "email":
      if (!valor || !/^[\w.%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(valor)) 
        this.errores.email = true;
      break;

    case "negocio":
      if (!valor || !/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/.test(valor)) 
        this.errores.negocio = true;
      break;

    case "username":
      if (!valor || !/^[A-Za-z0-9]+$/.test(valor)) 
        this.errores.username = true;
      break;

    case "password":
      if (!valor || !/^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[^\s]{8,30}$/.test(valor)) 
        this.errores.password = true;
      break;

    case "confirmPassword":
      if (valor !== this.form.password) 
        this.errores.confirmPassword = true;
      break;
  }

  // Si hay algún error, muestra el mensaje general
  this.mensaje = Object.values(this.errores).some((v) => v)
    ? "Existen campos sin diligenciar o con formato inválido."
    : "";
}
,
  
      async registrarUsuario() {
        this.mensaje = "";
  
        if (!this.validarCampos()) {
          this.mensaje = "Existen campos sin diligenciar o con formato inválido.";
          return;
        }
  
        this.esperandoRespuesta = true;
  
        try {
          const response = await UsuarioProvider.registrar(this.form);
  
          if (response.success) {
            this.mensaje = "Negocio registrado exitosamente.";
            setTimeout(() => this.$router.push("/login"), 2000);
          } else {
            this.mensaje = response.message || "Error en el registro.";
          }
        } catch (error) {
          this.mensaje = error.message.includes("429")
            ? "Límite de intentos alcanzado, por favor intente de nuevo más tarde."
            : "Error al conectar con el servidor.";
        } finally {
          this.esperandoRespuesta = false;
        }
      },
    },
  };
  </script>
  
  <style scoped>
  .registro-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    width: 100vw;
    background: #f5f7fa;
    font-family: "Segoe UI", sans-serif;
    padding: 20px;
  }
  
  .registro-box {
    background: white;
    padding: 40px 60px;
    border-radius: 16px;
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 900px;
  }
  
  h2 {
    text-align: center;
    color: #004aad;
    font-weight: 700;
    margin-bottom: 25px;
  }
  
  /* GRID con dos columnas */
  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px 30px;
  }
  
  .form-grid .full {
    grid-column: 1 / 3;
  }
  
  label {
    display: block;
    font-weight: 600;
    color: #333;
    margin-bottom: 5px;
  }
  
  input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 1em;
  }
  
  input:focus {
    outline: none;
    border-color: #004aad;
  }
  
  input.invalido {
    border-color: red;
  }
  
  .ver {
    color: #004aad;
    font-size: 0.9em;
    cursor: pointer;
    display: inline-block;
    margin-top: 5px;
  }
  
  button {
    width: 100%;
    margin-top: 30px;
    padding: 12px;
    background-color: #004aad;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1.1em;
    font-weight: 600;
    cursor: pointer;
    transition: 0.3s;
  }
  
  button:hover {
    background-color: #003a8c;
  }
  
  button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  
  .mensaje {
    text-align: center;
    margin-top: 15px;
    color: red;
    font-weight: 500;
  }
  
  .volver {
    text-align: center;
    margin-top: 10px;
  }
  
  .volver a {
    text-decoration: none;
    color: #008f68;
    font-weight: 600;
  }
  
  /* Modo responsive */
  @media (max-width: 768px) {
    .registro-box {
      padding: 25px;
      max-width: 95%;
    }
  
    .form-grid {
      grid-template-columns: 1fr;
    }
  
    .form-grid .full {
      grid-column: auto;
    }
  
    button {
      margin-top: 20px;
    }
  }
  </style>
  