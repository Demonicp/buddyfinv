<template>
  <div>
    <h2>Opciones del Dashboard</h2>
    
    <button @click="$router.push({ name: 'Ventas' })">Ventas</button>
    <button @click="$router.push({ name: 'Ingresos' })">Ingresos</button>
    <button @click="$router.push({ name: 'Egresos' })">Egresos</button>
    


    <!-- más botones -->

    <RouterView /> <!-- Aquí se renderiza VentaView o IngresoTable -->
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

  
  <style scoped>
  .opciones-dashboard {
    padding: 2rem;
  }
  button {
    margin: 1rem;
    padding: 1rem 2rem;
    background-color: #3498db;
    color: white;
    border: none;
    border-radius: 4px;
  }
  </style>