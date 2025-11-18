<template>
  <div class="venta-detalle-view">
    <div v-if="loading" class="info">Cargando venta...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <VentaSummary :venta="venta" />
    </div>
  </div>
</template>

<script>
import VentaSummary from '@/components/VentaSummary.vue'
import { VentaProvider } from '@/providers/VentaProvider'
import VentaResponseDTO from '@/models/VentaResponseDTO'

export default {
  name: 'VentaDetalleView',
  components: { VentaSummary },
  data() {
    return {
      venta: null,
      loading: true,
      error: null
    }
  },
  async created() {
    await this.loadVenta()
  },
  watch: {
    // si la ruta cambia mantén la vista reactiva
    '$route.params.id': {
      immediate: false,
      handler() {
        this.loadVenta()
      }
    }
  },
  methods: {
    async loadVenta() {
      this.loading = true
      this.error = null
      this.venta = null
      const id = this.$route.params.id
      if (!id) {
        this.error = 'ID de venta no proporcionado'
        this.loading = false
        return
      }
      try {
        const resp = await VentaProvider.obtenerVenta(id)
        this.venta = VentaResponseDTO.fromApi ? VentaResponseDTO.fromApi(resp) : resp
      } catch (e) {
        if (e.status === 404) {
          this.error = 'Venta no encontrada'
        } else if (e.status === 401) {
          this.error = 'No autorizado. Inicia sesión.'
        } else {
          this.error = e.message || 'Error al cargar la venta'
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.venta-detalle-view {
  max-width: 960px;
  margin: 18px auto;
  padding: 12px;
}
.info {
  color: #333;
}
.error {
  color: #b00020;
}
</style>