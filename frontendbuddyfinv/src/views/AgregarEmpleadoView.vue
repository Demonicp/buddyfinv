<template>
    <div class="page-container">
      <div class="box">
        <h2>Añadir Empleado</h2>

        <form @submit.prevent="onSubmit">
          <div class="grid">
            <div>
              <label>NIT *</label>
              <input v-model="form.nitUsuario" placeholder="123456789" />
              <small v-if="errors.nitUsuario" class="error">{{ errors.nitUsuario }}</small>
            </div>

            <div>
              <label>Nombre *</label>
              <input v-model="form.nombre" placeholder="Ej. Juan Pérez" />
              <small v-if="errors.nombre" class="error">{{ errors.nombre }}</small>
            </div>

            <div>
              <label>Email *</label>
              <input v-model="form.email" placeholder="correo@ejemplo.com" />
              <small v-if="errors.email" class="error">{{ errors.email }}</small>
            </div>

            <div>
              <label>Usuario *</label>
              <input v-model="form.usuario" placeholder="usuario123" />
              <small v-if="errors.usuario" class="error">{{ errors.usuario }}</small>
            </div>

            <div>
              <label>Contraseña *</label>
              <input :type="show ? 'text' : 'password'" v-model="form.password" placeholder="Password@123" />
              <small @click="show = !show" class="ver">{{ show ? 'Ocultar' : 'Mostrar' }}</small>
              <small v-if="errors.password" class="error">{{ errors.password }}</small>
            </div>
          </div>

          <div class="botones">
            <button 
              type="submit"
              class="btn-confirmar"
              :disabled="submitting"
            >
              {{ submitting ? 'Creando...' : 'Confirmar' }}
            </button>

            <button 
              type="button"
              class="btn-cancelar"
              @click="cancelar"
            >
              Cancelar
            </button>
          </div>

          <p :class="{'mensaje-success': success, 'mensaje-error': !success && mensaje}" v-if="mensaje">{{ mensaje }}</p>
        </form>
      </div>
    </div>
  </template>

  <script>
  import { UsuarioProvider } from '@/providers/UsuarioProvider.js'
  
  export default {
    name: 'AgregarEmpleadoView',
    data() {
      return {
        form: {
          nitUsuario: '',
          nombre: '',
          email: '',
          usuario: '',
          password: ''
        },
        errors: {},
        submitting: false,
        mensaje: '',
        success: false,
        show: false
      }
    },
    methods: {
      validar() {
        this.errors = {}
        const { nitUsuario, nombre, email, usuario, password } = this.form
  
        if (!/^\d+$/.test(nitUsuario)) this.errors.nitUsuario = 'El NIT debe contener solo números.'
        if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/.test(nombre)) this.errors.nombre = 'Ingrese solo letras y espacios.'
        if (!/^[\w.%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email)) this.errors.email = 'Ingrese un correo válido.'
        if (!/^[A-Za-z0-9]{8,20}$/.test(usuario)) this.errors.usuario = 'El campo usuario debe tener entre 8 y 20 caracteres alfanuméricos, sin espacios en blanco.'
        if (!/^(?=.*[A-Z])(?=.*\d)(?=.*[\W_])[^\s]{8,30}$/.test(password)) this.errors.password = 'La contraseña debe tener 8–30 caracteres, una mayúscula, un número y un símbolo.'
  
        return Object.keys(this.errors).length === 0
      },
  
      async onSubmit() {
        this.mensaje = ''
        this.success = false
  
        if (!this.validar()) {
          this.mensaje = 'Corrige los errores del formulario.'
          return
        }
  
        this.submitting = true
  
        try {
          await UsuarioProvider.crearEmpleado(this.form)
          this.success = true
          this.mensaje = 'Empleado creado correctamente.'
          // limpiar form
          this.form = { nitUsuario: '', nombre: '', email: '', usuario: '', password: '' }
          // opcional: redirigir a lista
          setTimeout(() => this.$router.push({ name: 'ListaEmpleados' }), 1000)
        } catch (err) {
          this.mensaje = err.message || 'Error al crear empleado.'
          this.success = false
        } finally {
          this.submitting = false
        }
      },
      cancelar() {
        // Limpia todos los campos
        this.form = {
          nitUsuario: '',
          nombre: '',
          email: '',
          usuario: '',
          password: ''
        }

        // Limpia errores y mensajes
        this.errors = {}
        this.mensaje = ''
        this.success = false
        this.$router.push({ name: 'ListaEmpleados' });
      }
    }
  }
  </script>

  <style scoped>
  .page-container{
    display:flex;
    justify-content:center;
    padding:30px
}
  .box{
    width:100%;
    max-width:820px;
    background:white;
    padding:28px;
    border-radius:12px;
    box-shadow:0 6px 18px rgba(0,0,0,.06)
}
  .grid{
    display:grid;
    grid-template-columns:1fr 1fr;
    gap:16px
}
  label{
    font-weight:600;
    margin-bottom:6px;
    display:block
}
  input{
    width:100%;
    padding:10px;
    border-radius:8px;
    border:1px solid #ccc
}
  button{
    margin-top:18px;
    width:100%;
    padding:12px;
    background:#ff8800;
    color:white;
    border-radius:8px;
    border:none;
    font-weight: 600;
    transition: .2s;
}
button:hover{
    background:#e56f00;
}
  .error{
    color:#c0392b;
    font-size:.85em;margin-top:6px
}
  .mensaje-success{
    color:#087f5b;
    margin-top:12px
}
  .mensaje-error{
    color:#c0392b;
    margin-top:12px
}
  .ver{
    color:#004aad;
    cursor:pointer;
    font-size:.85em;display:inline-block;
    margin-top:6px
}
  @media (max-width:768px){.grid{grid-template-columns:1fr}}
  label, h2 {
  color: #000 !important;
}
input {
  color: #000 !important;
}
.botones {
  margin-top: 18px;
  display: flex;
  gap: 12px;
}

.btn-confirmar {
  flex: 1;
  padding: 12px;
  background: #ff8800;
  color: white;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  transition: .2s;
}

.btn-confirmar:hover {
  background: #e56f00;
}

.btn-cancelar {
  flex: 1;
  padding: 12px;
  background: #777;
  color: white;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  transition: .2s;
}

.btn-cancelar:hover {
  background: #555;
}

</style>