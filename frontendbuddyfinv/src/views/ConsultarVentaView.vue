<template>
  <div class="consultar-venta-container">
    <div v-if="cargando">Cargando tus ventas...</div>
    <div v-else-if="ventas.length === 0">No tienes ventas registradas.</div>
    <VentaFilters :ventas="ventas" @update="ventasFiltradas = $event" />
    <RegistroTable :ventas="ventasFiltradas" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { VentaProvider } from '../providers/VentaProvider'
import { useUsuarioStore } from '../stores/usuarioStore'
import RegistroTable from '../components/RegistroTable.vue'
import VentaFilters from '../components/VentaFilters.vue'

// `ventas` guarda la lista cruda recibida desde backend; `ventasFiltradas` es la lista que renderiza la tabla
const ventas = ref([])
const ventasFiltradas = ref([])
const cargando = ref(true)
const usuarioStore = useUsuarioStore()

onMounted(async () => {
  try {
    if (!usuarioStore.id) {
      const token = localStorage.getItem('token')
      if (token) usuarioStore.establecerDatosDesdeToken(token)
    }
    // NOTA: `getDetalladas()` ahora devuelve `empleadoId` en cada venta, entre otros campos
    const ventasCrudas = await VentaProvider.getDetalladas()
    ventas.value = ventasCrudas
    ventasFiltradas.value = ventasCrudas // inicial
  } catch (error) {
    console.error('Error al cargar ventas:', error.message)
  } finally {
    cargando.value = false
  }
})
</script>

<style scoped>


</style>
