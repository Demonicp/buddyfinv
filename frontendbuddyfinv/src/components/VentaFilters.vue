<template>
  <div class="venta-filtros">
    <h3 class="filtros-title">Filtrar Registros</h3>
    <form class="filtros-row" @submit.prevent>
      <div class="field">
        <label>Código de venta</label>
        <input type="number" v-model.number="filtros.idVenta" placeholder="Ej: 123" />
      </div>
      <div class="field">
        <label>Código de empleado</label>
        <input type="number" v-model.number="filtros.empleadoId" placeholder="Ej: 45" />
      </div>
      <div class="field">
        <label>Rango de fechas</label>
        <select v-model="filtros.rango">
          <option value="">Todos</option>
          <option value="hoy">Hoy</option>
          <option value="semana">Semana</option>
          <option value="mes">Mes</option>
          <option value="año">Año</option>
          <option value="personalizado">Personalizado</option>
        </select>
      </div>
      <div v-if="filtros.rango === 'personalizado'" class="field field-dates">
        <label class="sr-only">Desde</label>
        <input type="date" v-model="filtros.fechaInicio" />
        <label class="sr-only">Hasta</label>
        <input type="date" v-model="filtros.fechaFin" />
      </div>
      <div class="actions">
        <button class="btn cancelar" type="button" @click.prevent="resetFilters">Cancelar</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, computed, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  ventas: { type: Array, required: true }
})
const emit = defineEmits(['update'])

const filtros = reactive({
  idVenta: null,
  empleadoId: null,
  rango: '',
  fechaInicio: null,
  fechaFin: null
})

function calcularFechas(rango) {
  const hoy = new Date()
  let inicio = null, fin = null
  if (rango === 'hoy') {
    inicio = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate(), 0, 0, 0, 0)
    fin = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate(), 23, 59, 59, 999)
  } else if (rango === 'semana') {
    const dia = hoy.getDay()
    inicio = new Date(hoy)
    inicio.setDate(hoy.getDate() - dia)
    inicio.setHours(0, 0, 0, 0)
    fin = new Date(hoy)
    fin.setHours(23, 59, 59, 999)
  } else if (rango === 'mes') {
    inicio = new Date(hoy.getFullYear(), hoy.getMonth(), 1, 0, 0, 0, 0)
    fin = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0, 23, 59, 59, 999)
  } else if (rango === 'año') {
    inicio = new Date(hoy.getFullYear(), 0, 1, 0, 0, 0, 0)
    fin = new Date(hoy.getFullYear(), 11, 31, 23, 59, 59, 999)
  } else if (rango === 'personalizado') {
    inicio = filtros.fechaInicio ? new Date(filtros.fechaInicio) : null
    if (inicio) inicio.setHours(0, 0, 0, 0)
    fin = filtros.fechaFin ? new Date(filtros.fechaFin) : null
    if (fin) fin.setHours(23, 59, 59, 999)
  }
  return { inicio, fin }
}

const filtradas = computed(() => {
  return props.ventas.filter(v => {
    if (filtros.idVenta && Number(v.idVenta) !== Number(filtros.idVenta)) return false
    if (filtros.empleadoId && Number(v.empleadoId) !== Number(filtros.empleadoId)) return false
    if (filtros.rango) {
      const { inicio, fin } = calcularFechas(filtros.rango)
      const fechaVenta = new Date(v.fecha)
      if (inicio && fechaVenta < inicio) return false
      if (fin && fechaVenta > fin) return false
    }
    return true
  })
})

watch(filtradas, (nuevaLista) => {
  emit('update', nuevaLista)
}, { immediate: true })

function resetFilters() {
  filtros.idVenta = null
  filtros.empleadoId = null
  filtros.rango = ''
  filtros.fechaInicio = null
  filtros.fechaFin = null
  // navigate back to Acciones view (same behavior as VentaForm cancel)
  try {
    router.push({ name: 'acciones' }).catch(() => router.back())
  } catch (e) {
    window.history.back()
  }
}
</script>

<style scoped>
.venta-filtros {
  width: 100%;
  height: 100%;
  background: #fffaf3;
  padding: 14px 18px;
  border-radius: 12px;
  border: 1px solid #f5cba7;
  box-shadow: inset 0 0 0 2px rgba(248, 196, 113, 0.08);
}

.filtros-title {
  margin: 0 0 8px 0;
  font-size: 1.05rem;
  color: #4d2c0c;
  font-weight: 800;
  letter-spacing: 0.2px;
  text-transform: uppercase;
}

.filtros-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
  align-items: end;
}

.field {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.field-dates {
  grid-column: span 2;
  display: flex;
  gap: 8px;
  align-items: center;
}
.field label {
  font-size: 0.85rem;
  color: #4d2c0c;
  font-weight: 700;
  margin-bottom: 6px;
}
input[type="number"],
input[type="date"],
select {
  width: 100%;
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid #e5d6c7;
  background: #fff;
  color: #333;
  box-sizing: border-box;
}
.actions {
  display: flex;
  gap: 10px;
  grid-column: span 1;
}
.btn {
  padding: 8px 12px;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
  border: none;
}
.btn.buscar {
  background-color: #3498db;
  color: #fff;
}
.btn.cancelar {
  background-color: #ff891b;
  color: #fff;
}
@media (max-width: 1200px) {
  .filtros-row { grid-template-columns: repeat(4, 1fr); }
  .field-dates { grid-column: span 2; }
  .actions { grid-column: span 1; }
}
@media (max-width: 860px) {
  .filtros-row { grid-template-columns: 1rem; gap: 10px; }
  .field-dates { grid-column: auto; flex-direction: column; gap: 6px; }
  .actions { justify-content: flex-start; }
}
.sr-only {
  position: absolute !important;
  height: 1px;
  width: 1px;
  overflow: hidden;
  clip: rect(1px, 1px, 1px, 1px);
  white-space: nowrap;
}
</style>