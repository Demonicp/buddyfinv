<template>
  <div class="venta-view">
    <h1>Gestión de Ventas</h1>
    <VentaTable/>
  </div>
</template>

<script setup>
import VentaTable from '../components/VentaTable.vue'
import { useUsuarioStore } from '../stores/usuarioStore'


</script>

<style scoped>
.venta-view {
  padding: 2rem;
}
</style>