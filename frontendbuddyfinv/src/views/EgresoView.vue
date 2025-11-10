<template>
    <div class="egresos-view">
      <h1>Gestión de Egresos</h1>
      <EgresoTable />
    </div>
  </template>
  
  <script>
  import EgresoTable from '../components/EgresosTable.vue'
  
  export default {
    name: 'EgresosView',
    components: {
      EgresoTable
    }
  }
  </script>
  
  <style scoped>
  .egresos-view {
    padding: 20px;
  }
  </style>