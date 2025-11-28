<template>
  <div class="venta-view">
    <div v-if="cargando">Cargando tus ventas...</div>
    <div v-else-if="ventas.length === 0">No tienes ventas registradas.</div>
    <VentaTable :ventas="ventas" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { VentaProvider } from '../providers/VentaProvider'
import { useUsuarioStore } from '../stores/usuarioStore'
import VentaTable from '../components/VentaTable.vue'

const ventas = ref([])
const cargando = ref(true)
const usuarioStore = useUsuarioStore()

onMounted(async () => {
  try {
    // Verificar si el usuario existe en el store
    if (!usuarioStore.id) {
      const token = localStorage.getItem('token')
      if (token) usuarioStore.establecerDatosDesdeToken(token)
    }

    // 🔥 SOLAMENTE UNA LLAMADA AL BACKEND
    ventas.value = await VentaProvider.getDetalladas()
    console.log('Ventas recibidas:', ventas.value)

  } catch (error) {
    console.error('Error al cargar ventas:', error.message)
  } finally {
    cargando.value = false
  }
})
</script>

<style scoped>
.venta-view {
  padding: 2rem;
}
</style>
