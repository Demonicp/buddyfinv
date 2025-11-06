<template>
  <div>
    <h1>Prueba Inventario</h1>
    <ProductoTable />
  </div>
</template>

<script>
import ProductoTable from '@/components/ProductoTable.vue'

export default {
  name: 'TestProducto',
  components: {
    ProductoTable
  }
}
</script>