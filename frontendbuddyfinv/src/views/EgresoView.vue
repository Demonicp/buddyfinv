<template>
  <div class="egresos-view">
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
