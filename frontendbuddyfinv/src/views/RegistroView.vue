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
              @input="validarCampo('nombre')"
            />
            <small v-if="errores.nombre" class="error-text">{{ errores.nombre }}</small>
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
              @input="validarCampo('nitUsuario')"
            />
            <small v-if="errores.nitUsuario" class="error-text">{{ errores.nitUsuario }}</small>
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
              @input="validarCampo('email')"
            />
            <small v-if="errores.email" class="error-text">{{ errores.email }}</small>
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
              @input="validarCampo('negocio')"
            />
            <small v-if="errores.negocio" class="error-text">{{ errores.negocio }}</small>
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
              @input="validarCampo('username')"
            />
            <small v-if="errores.username" class="error-text">{{ errores.username }}</small>
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
              @input="validarCampo('password')"
            />
            <small @click="togglePassword" class="ver">
              {{ mostrarPassword ? 'Ocultar' : 'Mostrar' }}
            </small>
            <small v-if="errores.password" class="error-text">{{ errores.password }}</small>
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
              @input="validarCampo('confirmPassword')"
            />
            <small v-if="errores.confirmPassword" class="error-text">{{ errores.confirmPassword }}</small>
          </div>
        </div>

        <button type="submit" :disabled="!formValido || esperandoRespuesta">
          {{ esperandoRespuesta ? "Registrando..." : "Registrarme" }}
        </button>

        <p :class="esExito ? 'mensaje-exito' : 'mensaje-error'" v-if="mensaje">
          {{ mensaje }}
        </p>

        <p class="volver">
          <router-link to="/login">Volver al login</router-link>
        </p>
      </form>
    </div>
  </div>
</template>
  
  <script>
  import { UsuarioProvider } from "@/providers/UsuarioProvider.js";
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
        esExito: false,
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
        this.errores.nombre = "El nombre solo puede tener letras y espacios.";
      break;

    case "nitUsuario":
      if (!valor || !/^\d+$/.test(valor)) 
        this.errores.nitUsuario = "El NIT solo puede contener números.";
      break;

    case "email":
      if (!valor || !/^[\w.%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(valor)) 
        this.errores.email = "Correo electrónico inválido.";
      break;

    case "negocio":
      if (!valor || !/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/.test(valor)) 
        this.errores.negocio = "El nombre del negocio solo puede tener letras y espacios.";
      break;

    case "username":
      if (!valor || !/^[A-Za-z0-9]{8,20}$/.test(valor)) 
        this.errores.username = "El nombre de usuario debe tener entre 8 y 20 caracteres alfanumericos, sin espacios en blanco";
      break;

    case "password":
      if (!valor || !/^(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s])[^\s]{8,30}$/.test(valor)) 
        this.errores.password = "La contraseña debe tener 8-30 caracteres, una mayúscula, un número y un símbolo.";
      break;

    case "confirmPassword":
      if (valor !== this.form.password) 
        this.errores.confirmPassword = "Las contraseñas no coinciden.";
      break;
  }

  // Si hay algún error, muestra el mensaje general
  this.mensaje = Object.values(this.errores).some((v) => v)
    ? "Existen campos sin diligenciar o con formato inválido."
    : "";
},
validarCampos(){
  let todoValido = true;

  for (const campo in this.form){
    this.validarCampo(campo);
    if (this.errores[campo]){
      todoValido=false;
    }
  }
  return todoValido;
},
  
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
            this.esExito = true;
            this.mensaje = "Negocio registrado exitosamente.";
            setTimeout(() => this.$router.push("/login"), 2000);
          } else {
            this.esExito = false;
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
  @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@100..900&display=swap');
  
  .registro-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    width: 100vw;
    background-image: url('@/assets/FondoLogin.png');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    font-family: 'Segoe UI', sans-serif;
    padding: 0 20px;
    box-sizing: border-box;
  }
  
  .registro-box {
    background: rgba(255, 255, 255, 0.85); /* fondo blanco translúcido */
    padding: 30px 40px;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 900px;
    border: 1px solid #f5cba7;
    font-family: 'Outfit', sans-serif;
  }
  
  h2 {
    text-align: center;
    color: #e67e22;
    font-weight: 700;
    margin-bottom: 25px;
    font-family: 'Outfit', sans-serif;
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
    color: #4d2c0c;
    margin-bottom: 5px;
    font-family: 'Outfit', sans-serif;
  }
  
  input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 1em;
    background-color: #f5f5f5;
    font-family: 'Outfit', sans-serif;
    transition: border-color 0.2s, background-color 0.2s;
  }
  
  input:focus {
    outline: none;
    border-color: #f8c471;
    background-color: #fffaf3;
  }
  
  input.invalido {
    border-color: red;
    background-color: #fff0f0;
  }
  
  .ver {
    color: #e67e22;
    font-size: 0.9em;
    cursor: pointer;
    display: inline-block;
    margin-top: 5px;
  }
  
  button {
    width: 100%;
    margin-top: 30px;
    padding: 12px;
    background-color: #e67e22;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1.1em;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s;
    font-family: 'Outfit', sans-serif;
  }
  
  button:hover {
    background-color: #d35400;
  }
  
  button:disabled {
    background-color: #f0d8c0;
    cursor: not-allowed;
  }
  
  .mensaje,
  .mensaje-error {
    color: red;
    text-align: center;
    margin-top: 15px;
    font-weight: 500;
  }
  
  .mensaje-exito {
    color: #27ae60;
    text-align: center;
    margin-top: 15px;
    font-weight: 500;
  }
  
  .volver {
    text-align: center;
    margin-top: 10px;
  }
  
  .volver a {
    text-decoration: none;
    color: #e67e22;
    font-weight: 600;
  }
  
  /* Responsive */
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
  
  .error-text {
    color: red;
    font-size: 0.85em;
    margin-top: 3px;
    display: block;
  }
  </style>
  