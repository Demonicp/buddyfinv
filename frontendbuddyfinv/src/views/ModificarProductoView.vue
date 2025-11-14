<template>
    <div class="vista-modificar">
      <h2>Modificar un producto</h2>
      <FormularioEdicionTable />
    </div>
  </template>
  
  <script setup>
  import FormularioEdicionTable from '@/components/FormularioEditarTable.vue'
  </script>
  
  <style scoped>
  .vista-modificar {
    padding: 2rem;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 0 12px rgba(0, 0, 0, 0.05);
  }
  h2 {
    text-align: center;
    color: #e67e22;
    margin-bottom: 1.5rem;
  }
  </style>