<template>
    <div class="opciones-dashboard">
      <button @click="$router.push('/ventas')">Ventas</button>
      <button>Ingresos</button>
      <button>Egresos</button>
    </div>
  </template>
  
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