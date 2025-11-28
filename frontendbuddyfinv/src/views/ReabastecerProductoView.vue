<template>
  <div class="vista-reabastecer">
    <h2>Aquí podrás reabastecer la cantidad de tus productos</h2>
    <InvReabastecerForm/>
  </div>
</template>

<script setup>
import InvReabastecerForm from '@/components/InvReabastecerForm.vue'
</script>

<style scoped>
.vista-modificar {
  padding: 4rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.05);
}
h2 {
  text-align: center;
  color: #e67e22;
  margin-bottom: 1.5rem;
  font-weight: 600;
  font: 'Segoe UI', sans-serif;
  font-size: 2rem;
}
</style>