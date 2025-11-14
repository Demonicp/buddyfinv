<template>
  <div class="egreso-wrapper">
    <div class="total-egresos">
      <span class="total-label">Total de Egresos:</span>
      <span class="total-value">-{{ formatoMoneda(totalEgresos) }}</span>
    </div>

    <div class="egreso-table-container">
      <div class="table-header-section">
        <h2 class="title">Lista De Egresos</h2>
        <div class="buttons-container">
          <input type="date" v-model="fechaInicio" />
        <input type="date" v-model="fechaFin" />
        <button class="btn-buscar" @click="filtrarFechas">Buscar</button>
        <button class="btn-limpiar" @click="limpiarFiltros">Limpiar filtros</button>
          <button class="btn-Registrar" @click="abrirModal">Registrar egreso</button>
          <button class="btn-consultar" @click="consultarEgreso">
            Consultar egreso
          </button>
          
        </div>
      </div>
      
      


    


      <div class="table-header" role="row">
        <span>Fecha</span>
        <span>Concepto</span>
        <span>Categoría</span>
        <span>Valor</span>
        <span>Método de Pago</span>
      </div>

      <div
        v-for="(egreso, index) in egresos"
        :key="egreso.id_egreso || index"
        class="table-row"
        role="row"
      >
        <span class="cell">{{ formatearFecha(egreso.fecha) || 'N/A' }}</span>
        <span class="cell">{{ egreso.observacion || 'N/A' }}</span>
        <span class="cell">{{ egreso.descripcionTipoEgreso || 'N/A' }}</span>
        <span class="cell">{{ formatoMoneda(egreso.costo) }}</span>
        <span class="cell">{{ egreso.descripcionMetodoPago }}</span>
      </div>

      <div class="table-footer">
        <button class="btn-limpiar" @click="limpiarFiltros">Limpiar Filtros</button>
      </div>
    </div>


    <!-- JUAN DAVIIIIIIID ORTIIIIIIIIIIIIIIIIIIIZ-->
    <!-- El div v-if es como un if==true pregunta que si se le unde se activa -->
      
    <!-- desde aqui-->
    
    <!--  Modal para registrar egreso(modal es un formulario que esta en vue js) -->
    <!-- Modal overlay: fondo oscuro con blur que se muestra cuando mostrarModal es true -->
    <!-- @click.self permite cerrar el modal al hacer clic fuera del contenido -->
    <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal-content">
        <h3>Registrar nuevo egreso</h3>

         <!-- JUAN DAVIIIIIIID ORTIIIIIIIIIIIIIIIIIIIZ-->
    <!-- h3 para colocar un texto en el encabezado del formulario -->


        <!-- Campo Fecha: input tipo date con icono de calendario -->
        <div class="form-group">
          <label>Fecha:</label>
          <div class="input-with-icon">
            <input 
              v-model="nuevoEgreso.fecha" 
              type="date" 
              placeholder="dd/mm/aaaa"
              class="form-input"
            />
            
          </div>
        </div>


        <!-- Campo Concepto: descripción del egreso (ej: Compra de materiales) -->
        <div class="form-group">
          <label>observaciones:</label>
          <input 
            v-model="nuevoEgreso.observacion" 
            type="text" 
            maxlength="300"
            placeholder="Ej: Compra de materiales"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>Categoría:</label>
          <select v-model="nuevoEgreso.categoria" class="form-select">
            <option value="">Seleccione una categoría...</option>
      <option v-for="tipo in tiposEgreso" :key="tipo.idTipoEgreso" :value="tipo.descripcion">
          {{ tipo.descripcion }}
      </option>


          </select>
        </div>

        <!-- Campo Valor: monto del egreso en números -->
        <div class="form-group">
          <label>Valor:</label>
          <input 
            v-model.number="nuevoEgreso.costo" 
            type="number" 
            step="0.01"
            min="0" 
            placeholder="Ej: 25000"
            class="form-input"
          />
        </div>

        <!-- Campo Método de Pago: dropdown con 3 opciones (Efectivo, Transferencia, Tarjeta) -->
        <div class="form-group">
          <label>Método de Pago:</label>
          <select v-model="nuevoEgreso.metodoPago" class="form-select">
            <option value="">Seleccione...</option>
            <option value="Efectivo">Efectivo</option>
            <option value="Transferencia">Transferencia</option>
            <option value="Tarjeta">Tarjeta</option>
          </select>
        </div>

        <!-- Botones del modal: Guardar (deshabilitado si el formulario no es válido) y Cancelar -->
        <div class="modal-buttons">
        <div ref="btnGuardar"
        >
        <button
  ref="btnGuardar"
  class="btn-guardar"
  :disabled="!formularioValido"
  @click="guardarEgreso"
  @mousedown="intentoGuardar"
>
  Guardar
</button>

  </div>

          <button class="btn-cancelar" @click="cerrarModal">Cancelar</button>

          <!-- hasta aqui se modela cada uno de los campos y botones-->

        </div>
      </div>
    </div>
  </div>

</template>


<script>

import { EgresoProvider } from '../providers/EgresoProvider.js'
export default {
  name: 'EgresoTable',
  data() {
    return {
      tiposEgreso:[],
      egresos: [], // Lista de egresos obtenidos del backend
      totalEgresos: 0, // Suma total de todos los egresos
      mostrarModal: false, // Controla la visibilidad del modal de registro el false es porque mientras no se unda el boton no se abra el formulario
       fechaInicio: '', // ← esta línea es clave
      fechaFin: '',
      // Objeto que almacena los datos del nuevo egreso a registrar
      nuevoEgreso: {
        fecha: '', // Fecha del egreso (formato YYYY-MM-DD)
        observacion: '', // Concepto/descripción del egreso
        categoria: '', // Categoría o tipo de egreso
        costo: null, // Valor monetario del egreso
        metodoPago: '' // Método de pago seleccionado (Efectivo, Transferencia, Tarjeta)
      }
    }
  },
  computed: {
    // Valida que todos los campos del formulario estén completos y correctos
    // Retorna true solo si todos los campos tienen valores válidos
    formularioValido() {
      return (
        this.nuevoEgreso.fecha &&
        this.nuevoEgreso.observacion &&
        this.nuevoEgreso.categoria &&
        this.nuevoEgreso.costo &&
        this.nuevoEgreso.costo > 0 &&
        this.nuevoEgreso.metodoPago
      )
    }
  },
  methods: {
    async cargarTiposEgreso() {
    try {
      const response = await fetch('http://localhost:8080/tipo-egresos');
      const data = await response.json();
      this.tiposEgreso = data;
    } catch (error) {
      console.error('Error al cargar tipos de egreso:', error);
    }
  },mounted() {
  this.cargarTiposEgreso();
},


   
    formatoMoneda(valor) {
      
      if (!valor && valor !== 0) return '0.00'
      return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
      }).format(valor)
    },
    intentoGuardar() {
  if (!this.formularioValido) {
    const btn = this.$refs.btnGuardar;
    btn.style.animation = 'none'; // Reinicia cualquier animación previa
    void btn.offsetWidth;         // Fuerza reflow
    btn.style.animation = 'vibrate-1 0.4s linear both'; // Aplica la animación
  }
}
,   
    formatearFecha(fecha) {
      if (!fecha) return null
      try {
        const date = new Date(fecha)
        return date.toLocaleDateString('es-CO', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit'
        })
      } catch (error) {
        return fecha
      }
    },
    
   
    consultarEgreso() {
      alert('Funcionalidad de consultar egreso - Por implementar')
      console.log('Consultar egreso - Funcionalidad pendiente')
    },
    
    /**
     * Abre el modal de registro de egreso
     * Inicializa el formulario con la fecha actual como valor por defecto
     */
    abrirModal() {
      // Establecer fecha por defecto como hoy (formato YYYY-MM-DD para input type="date")
      const hoy = new Date()
      const fechaFormateada = hoy.toISOString().split('T')[0]
      // Reinicializar el objeto nuevoEgreso con valores por defecto
      this.nuevoEgreso = {
        fecha: fechaFormateada,
        observacion: '',
        categoria: '',
        costo: null,
        metodoPago: ''
      }
      // Mostrar el modal
      this.mostrarModal = true
    },
    
    /**
     * Cierra el modal y limpia el formulario
     */
    cerrarModal() {
      this.mostrarModal = false
      // Limpiar todos los campos del formulario
      this.nuevoEgreso = {
        fecha: '',
        observacion: '',
        categoria: '',
        costo: null,
        metodoPago: ''
      }
    },
    
    /**
     * Guarda el nuevo egreso en el backend
     * Valida el formulario, mapea el método de pago a su ID y envía los datos
     */
    async guardarEgreso() {
      // Validar que el formulario esté completo antes de enviar
      if (!this.formularioValido) {
        alert('Por favor complete todos los campos correctamente')
        return
      }
      
      try {
        // Mapear método de pago a ID según la base de datos
        // IMPORTANTE: Estos IDs deben coincidir con los IDs en la tabla metodo_pagos
        // 1=Efectivo, 2=Transferencia, 3=Tarjeta
        const metodoPagoMap = {
          'Efectivo': 1,
          'Transferencia': 2,
          'Tarjeta': 3
        }
        
        // Esto es similar como si hicieramos Egreso dataEgreso = New Egreso(dato1,dato2,dato3,dato4,dato5)
        const dataEgreso = {
          observacion: this.nuevoEgreso.observacion, // Concepto del egreso(de que es el egreso)
          categoria: this.nuevoEgreso.categoria, // Categoría (de que tipo es el egreso ejemplo gasto de luz gasto empleados etc)
          costo: this.nuevoEgreso.costo, // Valor del egreso
          fecha: this.nuevoEgreso.fecha, // Fecha en formato YYYY-MM-DD
          idMetodoPago: metodoPagoMap[this.nuevoEgreso.metodoPago] // ID del método de pago
        }
        
        // En el provider hay un metodo que registra los egresos que hace uso del controlador y agrega el egreso
        await EgresoProvider.registerEgreso(dataEgreso)
        alert('Egreso registrado exitosamente')
        // Cerrar el modal y recargar la lista de egresos
        this.cerrarModal()
        await this.cargarEgresos()
      } catch (error) {
        console.error('Error al guardar egreso:', error)
        
        // Manejar diferentes tipos de errores
        let mensajeError = 'Error al guardar el egreso'
        
        
        //estos son excepciones en el caso de que me genere un error debido al token
        if (error.message.includes('403') || error.message.includes('Forbidden')) {
          mensajeError = 'Error de autorización (403). Por favor, verifique que:\n' +
                        '1. Su sesión no haya expirado\n' +
                        '2. Tenga los permisos necesarios\n' +
                        '3. Intente iniciar sesión nuevamente'
        } else if (error.message.includes('401') || error.message.includes('Unauthorized')) {
          mensajeError = 'Error de autenticación (401). Por favor, inicie sesión nuevamente'
        } else if (error.message.includes('400') || error.message.includes('Bad Request')) {
          mensajeError = 'Error en los datos enviados: ' + error.message
        } else {
          mensajeError = 'Error al guardar el egreso: ' + error.message
        }
        
        alert(mensajeError)
      }
    },
    
    limpiarFiltros() {
  this.fechaInicio = ''
  this.fechaFin = ''
  this.cargarEgresos()
  console.log('Filtros limpiados')
}
,
    
    async cargarEgresos() {
      try {
        const [data, total] = await Promise.all([
          EgresoProvider.getAllEgresosByUsuario(),
          EgresoProvider.costoTotalEgresosById()
        ])
        
        this.egresos = Array.isArray(data) ? data : []
        this.totalEgresos = total || 0
      } catch (error) {
        console.error('Error al obtener egresos:', error.message)
      }
    },async filtrarFechas() {
      if (!this.fechaInicio || !this.fechaFin) return
      console.log("📅 Enviando fechas al backend:", this.fechaInicio, this.fechaFin)


  try {
    const data = await EgresoProvider.filtrarByFechas(this.fechaInicio, this.fechaFin)
    this.egresos = Array.isArray(data) ? data : []
    
  } catch (error) {
    console.error('Error al filtrar egresos:', error.message)
  }
},


  },
  async mounted() {
   
    await this.cargarEgresos()
    await this.cargarTiposEgreso()
  }
}
</script>
<style>
@-webkit-keyframes vibrate-1 {
  0% { -webkit-transform: translate(0); transform: translate(0); }
  20% { -webkit-transform: translate(-2px, 2px); transform: translate(-2px, 2px); }
  40% { -webkit-transform: translate(-2px, -2px); transform: translate(-2px, -2px); }
  60% { -webkit-transform: translate(2px, 2px); transform: translate(2px, 2px); }
  80% { -webkit-transform: translate(2px, -2px); transform: translate(2px, -2px); }
  100% { -webkit-transform: translate(0); transform: translate(0); }
}

@keyframes vibrate-1 {
  0% { transform: translate(0); }
  20% { transform: translate(-2px, 2px); }
  40% { transform: translate(-2px, -2px); }
  60% { transform: translate(2px, 2px); }
  80% { transform: translate(2px, -2px); }
  100% { transform: translate(0); }
}

.vibrate-1 {
  animation: vibrate-1 0.4s linear both;
}

</style>

<style scoped>




@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&display=swap');






.egreso-wrapper {
  width: 80%;
  margin: 30px auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  font-family: 'Futura', 'Garamond', avenir;
 
}


.total-egresos {
  display: flex;
  justify-content: center; 
  align-items: center; 
  gap: 15px; 
  padding: 15px 25px; 
  
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
  border-radius: 10px; 
  
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
  margin-bottom: 10px; 
}


.total-label {
  font-size: 1.5rem; 
  font-weight: 600; 
  color: white; 
}


.total-value {
  font-size: 1.5rem; 
  font-weight: bold; 
  color: white; 
  
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
}


.egreso-table-container {
  width: 100%; 
  margin-top: 20px; 
  background: #f9f9f9; 
  font-size: 1.5rem;
  border-radius: 10px; 
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); 
  padding: 20px; 
  font-family: 'Futura', 'Garamond', avenir;
  
}


.table-header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  position: relative;
  
}

.title {
  text-align: left;
  margin: 0;
  text-shadow: 5px 5px 18px;
  color: #333;
  font-size: 2rem;
  font-family: 'Futura', 'Garamond', avenir;
  font-weight: bold;
  letter-spacing: 1px;
}


.btn-consultar {
  background-color: #1380f4;
  color: white;
  border: none beige;
  padding: 20px 20px;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 800;
  font-size: 0.9rem;
  transition: background-color 0.3s ease;
  font-family: 'Futura', 'Garamond', avenir;
  letter-spacing: 1px;
}

.btn-consultar:hover {
  background-color: #1a5fa8;
}

.btn-consultar:active {
  transform: scale(0.98);
}
.buttons-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.btn-Registrar{
  background-color: #5ea666;
  color: white;
  border:  rgba(255, 255, 255, 0.873);
  padding: 20px 20px;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.9rem;
  transition: background-color 0.3s ease;
  font-family: 'Futura', 'Garamond', avenir;
  letter-spacing: 1px;
}

.btn-Registrar:hover {
  background-color: #1a5fa8;
}

.btn-Registrar:active {
  transform: scale(0.98);
}

.table-footer {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px inset #2b2b2b;
}

.btn-limpiar {
  background-color: #e90505;
  color: white;
  border: none beige; 
  padding: 10px 20px;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.9rem;
  transition: background-color 0.3s ease;
  font-family: 'Share Tech Mono', 'Courier New', monospace;
  letter-spacing: 1px;
  
}

.btn-limpiar:hover {
  background-color: #5a6268;
}

.btn-limpiar:active {
  transform: scale(0.98);
}

.btn-buscar {
  background-color: #0b87f4;
  color: white;
  border: none beige; 
  padding: 10px 20px;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.9rem;
  transition: background-color 0.3s ease;
  font-family: 'Share Tech Mono', 'Courier New', monospace;
  letter-spacing: 1px;
  
}

.btn-buscar:hover {
  background-color: #5a6268;
}

.btn-buscar:active {
  transform: scale(0.98);
}






.table-header,
.table-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  padding: 10px;
  text-align: center;
  font-family: 'Futura', 'Garamond', avenir;
  font-weight: bold;
  letter-spacing: 0.5px;
}

.table-header {
  background-color: #237cdb;
  color: rgb(239, 236, 236);
  font-weight: bold;
  border-radius: 5px;
}

.table-row {
  background-color: rgb(240, 228, 228);
  border-bottom: 1px solid #ddd;
}

.table-row:nth-child(even) {
  background-color: #f1f1f1;
}

.table-row:hover {
  background-color: #e9f3ff;
  
}



/* Overlay del modal: fondo oscuro semitransparente con efecto blur */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* Fondo oscuro semitransparente */
  backdrop-filter: blur(4px); /* Efecto de desenfoque del fondo */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* Asegura que el modal esté por encima de otros elementos */
}

/* Contenedor principal del modal: tarjeta blanca con sombra */
.modal-content {
  background: white;
  border-radius: 10px;
  padding: 30px;
  width: 90%;
  max-width: 500px; /* Ancho máximo para mantener el modal centrado */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3); /* Sombra para dar profundidad */
  font-family: 'Futura', 'Garamond', avenir;
}

/* Título del modal */
.modal-content h3 {
  text-align: center;
  margin-bottom: 25px;
  color: #333;
  font-size: 1.5rem;
  font-weight: bold;
}

/* Grupo de campos del formulario: cada campo tiene su label e input */
.form-group {
  margin-bottom: 20px;
}

/* Labels de los campos del formulario */
.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 600;
  font-size: 0.95rem;
}

/* Inputs y selects del formulario: estilos base */
.form-input,
.form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
  font-family: 'Futura', 'Garamond', avenir;
  box-sizing: border-box;
  transition: border-color 0.3s ease; /* Transición suave al cambiar el borde */
}

/* Efecto de foco en los inputs: borde azul y sombra sutil */
.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #237cdb; /* Color azul del tema */
  box-shadow: 0 0 0 2px rgba(35, 124, 219, 0.1); /* Sombra azul sutil */
}

/* Contenedor para input con icono (campo de fecha) */
.input-with-icon {
  position: relative;
  display: flex;
  align-items: center;
}

/* Ajuste de padding para el input con icono */
.input-with-icon .form-input {
  padding-right: 40px; /* Espacio para el icono de calendario */
}

/* Icono de calendario en el campo de fecha */
.calendar-icon {
  position: absolute;
  right: 12px;
  pointer-events: none; /* No interfiere con el clic en el input */
  font-size: 1.2rem;
}

/* Estilos del select: elimina el estilo por defecto y agrega flecha personalizada */
.form-select {
  appearance: none; /* Elimina el estilo nativo del navegador */
  /* Flecha personalizada usando SVG inline */
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23333' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 35px; /* Espacio para la flecha */
  cursor: pointer;
}

/* Contenedor de botones del modal */
.modal-buttons {
  display: flex;
  gap: 15px;
  margin-top: 25px;
  justify-content: flex-end; /* Botones alineados a la derecha */
}

/* Botón Guardar: color azul del tema */
.btn-guardar {
  background-color: #237cdb;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  transition: background-color 0.3s ease;
  font-family: 'Futura', 'Garamond', avenir;
}

/* Efecto hover en el botón guardar (solo si no está deshabilitado) */
.btn-guardar:hover:not(:disabled) {
  background-color: #1a5fa8; /* Azul más oscuro al pasar el mouse */
}

/* Estado deshabilitado del botón guardar (cuando el formulario no es válido) */
.btn-guardar:disabled {
  background-color: #ccc;
  cursor: not-allowed; /* Cursor que indica que no se puede hacer clic */
}

/* Botón Cancelar: color gris */
.btn-cancelar {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  transition: background-color 0.3s ease;
  font-family: 'Futura', 'Garamond', avenir;
}

/* Efecto hover en el botón cancelar */
.btn-cancelar:hover {
  background-color: #5a6268; /* Gris más oscuro al pasar el mouse */
}

.filter-bar {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 20px;
}

</style>