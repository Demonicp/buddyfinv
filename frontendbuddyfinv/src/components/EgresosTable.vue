<template>
  <div class="egreso-wrapper">
    <div class="card-total-egresos">
      <span class="label">Total de Egresos:</span>
      <span class="value">-{{ formatoMoneda(totalEgresos) }}</span>
    </div>

    <div class="card-egreso-table">
      <h2 class="card-title">Lista De Egresos</h2>

      <div class="card-filter-bar">
        <input type="date" v-model="fechaInicio" />
        <input type="date" v-model="fechaFin" />
        <div class="form-group">
          <label>Categoría:</label>
          <select v-model="categoria" class="form-selectFilter">
            <option value="">Seleccione una categoría...</option>
            <option v-for="tipo in tiposEgreso" :key="tipo.idTipoEgreso" :value="tipo.descripcion">
              {{ tipo.descripcion }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>Metodo De pago:</label>
          <select v-model="metodoPago" class="form-selectFilter">
  <option value="">Seleccione un metodo de pago...</option>
  <option v-for="metodo in metodosPago" :key="metodo.idMetodoPago" :value="metodo.descripcion">
    {{ metodo.descripcion }}
  </option>
</select>

</div>


        <button class="btn buscar" @click="filtrar">Buscar</button>
        <button class="btn limpiar" @click="limpiarFiltros">Limpiar</button>
        <button class="btn registrar" @click="abrirModal">Registrar egreso</button>
        
        
      </div>

      <div class="card-scroll">
        <div class="card-table">
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
        </div>
      </div>
    </div>
        <!-- Modal -->
        <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal-card">
        <h3 class="modal-title">Registrar nuevo egreso</h3>

        <div class="form-group">
          <label>Fecha:</label>
          <input v-model="nuevoEgreso.fecha" type="date" class="form-input" />
        </div>

        <div class="form-group">
          <label>Observaciones:</label>
          <input v-model="nuevoEgreso.observacion" type="text" maxlength="300" class="form-input" />
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

        <div class="form-group">
          <label>Valor:</label>
          <input v-model.number="nuevoEgreso.costo" type="number" step="0.01" min="0" class="form-input" />
        </div>

        <div class="form-group">
          <label>Método de Pago:</label>
          <select v-model="nuevoEgreso.metodoPago" class="form-select">
    <option value="">Seleccione...</option>
      <option v-for="metodo in metodosPago" :key="metodo.idMetodoPago" :value="metodo.descripcion">
      {{ metodo.descripcion }}
    </option>
    </select>


        </div>

        <div class="modal-buttons">
          <button ref="btnGuardar" class="btn guardar" :disabled="!formularioValido" @click="guardarEgreso" @mousedown="intentoGuardar">Guardar</button>
          <button class="btn cancelar" @click="cerrarModal">Cancelar</button>
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
      categoria: '', // Categoría o tipo de egreso // Valor monetario del egreso
      metodoPago: '',
      metodosPago:[],
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
    //no borraaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaar despues lo cambio
    //esto viene siendo el provider de tipos de egreso(no lo vi necesario crear un provider y simplemente lo traje aqui)
    async cargarTiposEgreso() {
  try {
    const response = await fetch('http://localhost:8080/tipo-egresos', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}` // ← token desde localStorage
      }
    });
    const data = await response.json();
    this.tiposEgreso = data;
  } catch (error) {
    console.error('Error al cargar tipos de egreso:', error);
  }
},
 //esto viene siendo el provider de tipos de egreso(no lo vi necesario crear un provider y simplemente lo traje aqui)
 //no borraaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaar despues lo cambio
async cargarMetodosPago() {
  try {
    const response = await fetch('http://localhost:8080/metodo-pagos', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}` // ← mismo header
      }
    });
    const data = await response.json();
    this.metodosPago = data;
  } catch (error) {
    console.error('Error al cargar métodos de pago:', error);
  }
}
,


   
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
  this.categoria=''
  this.metodoPago=''
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
},async filtrar() {
  try {
    const data = await EgresoProvider.filtrar(
      this.fechaInicio,
      this.fechaFin,
      this.categoria,
      this.metodoPago  // ← usa el mismo campo que tienes en el select
    );

    this.egresos = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("❌ Error al filtrar egresos:", error.message);
    const mensaje = await error.response?.text?.() || error.message;
    alert(mensaje); // ← esto lanza el popup como el de la imagen
  }
}
,


  },
  async mounted() {
   
    await this.cargarEgresos()
    await this.cargarTiposEgreso()
    await this.cargarTiposEgreso()
    await this.cargarMetodosPago()
  }
}
</script>

<style scoped>
.egreso-wrapper {
  width: 90%;
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', sans-serif;
}

/* Tarjeta de total */
.card-total-egresos {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 16px;
  color: #c0392b;
}

.card-total-egresos .label {
  margin-right: 8px;
}

.card-total-egresos .value {
  color: #c0392b;
}

/* Tarjeta principal */
.card-egreso-table {
  background: #fffaf3;
  padding: 20px;
  border-radius: 16px;
  box-shadow: inset 0 0 0 2px #f8c471;
  border: 1px solid #f5cba7;
}

/* Título */
.card-title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
}

/* Filtros */
.card-filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.card-filter-bar input[type="date"] {
  padding: 6px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  flex: 0 0 auto; /* evita que se estiren */

}



/* Botones base */
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

.btn.buscar {
  background-color: #3498db;
  color: white;
}

.btn.buscar:hover {
  background-color: #2980b9;
}

.btn.limpiar {
  background-color: #e74c3c;
  color: white;
}

.btn.limpiar:hover {
  background-color: #c0392b;
}

.btn.registrar {
  background-color: #27ae60;
  color: white;
}

.btn.registrar:hover {
  background-color: #1e8449;
}

.btn.consultar {
  background-color: #8e44ad;
  color: white;
}

.btn.consultar:hover {
  background-color: #6c3483;
}

/* Scroll */
.card-scroll {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
  scrollbar-color: #f8c471 transparent;
  scrollbar-width: thin;
}

.card-scroll::-webkit-scrollbar {
  width: 8px;
}

.card-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.card-scroll::-webkit-scrollbar-thumb {
  background-color: #f8c471;
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: content-box;
}

/* Tabla */
.card-table .table-header,
.card-table .table-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
}

.card-table .table-header {
  background: #f8c471;
  color: #4d2c0c;
  text-transform: uppercase;
  font-weight: bold;
}

.card-table .table-row {
  background: #fdf6ec;
  margin-top: 8px;
  transition: 0.2s;
}

.card-table .table-row:nth-child(even) {
  background: #faebd7;
}

.card-table .table-row:hover {
  background: #f5cba7;
  transform: scale(1.01);
}

.cell {
  color: #333;
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

.modal-card {
  background: #fffaf3;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.modal-title {
  margin-bottom: 1.5rem;
  color: #e67e22;
  text-align: center;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #333;
}

.form-input,
.form-select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;
}
.form-selectFilter {
  width: auto;
  min-width: 160px;
  max-width: 220px;
  padding: 6px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
}


.modal-buttons {
  display: flex;
  
  justify-content: space-between;
  margin-top: 1.5rem;
}

.btn.guardar {
  background-color: #27ae60;
  color: white;
}

.btn.guardar:hover:not(:disabled) {
  background-color: #1e8449;
}

.btn.guardar:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn.cancelar {
  background-color: #e74c3c;
  color: white;
}

.btn.cancelar:hover {
  background-color: #c0392b;
}

/* Vibración */
@keyframes vibrate-1 {
  0% { transform: translateX(0); }
  20% { transform: translateX(-3px); }
  40% { transform: translateX(3px); }
  60% { transform: translateX(-3px); }
  80% { transform: translateX(3px); }
  100% { transform: translateX(0); }
}
</style>