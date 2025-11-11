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
                    <button class="btn-Registrar" @click="RegistrarEgreso">
                        Registrar egreso
                    </button>
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
                
                <span class="cell">{{ egreso.descripcionMetodoPago  }}</span>
            </div>
            
            <div class="table-footer">
                <button class="btn-limpiar" @click="limpiarFiltros">
                    Limpiar Filtros
                </button>
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
      egresos: [],
      totalEgresos: 0
    }
  },
  methods: {
   
    formatoMoneda(valor) {
      
      if (!valor && valor !== 0) return '0.00'
      return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
      }).format(valor)
    },
    
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
    RegistrarEgreso() {
   
   alert('Funcionalidad de consultar egreso - Por implementar')
   console.log('Consultar egreso - Funcionalidad pendiente')
 },
    
    limpiarFiltros() {

      this.cargarEgresos()
      console.log('Filtros limpiados')
    },
    
   
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
    }
  },
  async mounted() {
   
    await this.cargarEgresos()
  }
}
</script>

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
  font-family: 'Share Tech Mono', 'Courier New', monospace;
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
  background-color: #237cdb;
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
  background-color: #6c757d;
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
</style>