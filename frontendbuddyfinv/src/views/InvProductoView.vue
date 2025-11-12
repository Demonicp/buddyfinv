<template>
  <div class="inventario-view">
    <h2>Inventario de productos</h2>
    <ProductoTable />
  </div>
</template>

<script>
import ProductoTable from "@/components/InvProductoTable.vue";

export default {
  name: "ProductoView",
  components: {
    ProductoTable
  }
};
</script>

<style scoped>
.inventario-view {
  background-color: #fdf6ec; 
  padding: 2rem;
  min-height: 100%;
  font-family: 'Segoe UI', sans-serif;
}

.inventario-view h2 {
  color: #d35400; 
  margin-bottom: 1.5rem;
}
</style>