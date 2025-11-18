<template>
  <div class="crear-venta-wrapper">
    <div class="crear-venta-card">
      <h2 class="crear-venta-title">Registrar Venta</h2>

      <!-- aquí va tu formulario real; si ya tienes componentes, insértalos -->
      <VentaForm /> 

    </div>
  </div>
</template>

<script>
import VentaForm from '@/components/VentaForm.vue' // ajusta path si hace falta

export default {
  name: 'CrearVentaView',
  components: { VentaForm }
}
</script>

<style scoped>
.crear-venta-wrapper {
  padding: 1.5rem;
}
.crear-venta-card {
  background: #fff;

  justify-content: center;
  border-radius: 10px;
  padding: 1.5rem;
  box-shadow: 0 4px 18px rgba(0,0,0,0.06);
}
.crear-venta-title { font-size: 1.4rem; margin-bottom: 1rem; }
</style>