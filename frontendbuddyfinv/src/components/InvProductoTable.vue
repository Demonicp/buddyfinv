<template>
  <div class="inventario-wrapper">
    <div class="inventario-card">
      <h2 class="inventario-title">Visualiza, añade y modifica tus productos</h2>

      <div class="table-scroll">
        <div class="table-card">
          <div class="table-header" role="row">
            <span @click="ordenar('id')">Código</span>
            <span @click="ordenar('nombre')">Nombre</span>
            <span @click="ordenar('precio')">Precio</span>
            <span @click="ordenar('tipoProducto')">Tipo</span>
            <span @click="ordenar('propietario')">Propietario</span>
            <span @click="ordenar('cantidadDisponible')">Cantidad Disponible</span>
          </div>

          <div
            v-for="producto in productosOrdenados"
            :key="producto.id"
            class="table-row"
            role="row"
          >
            <span class="cell">{{ producto.id }}</span>
            <span class="cell">{{ producto.nombre }}</span>
            <span class="cell">{{ producto.precio }}</span>
            <span class="cell">{{ producto.tipoProducto }}</span>
            <span class="cell">{{ producto.propietario }}</span>
            <span class="cell">{{ producto.cantidadDisponible ?? 0 }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import { useProductoStore } from "@/stores/productoStore";

// usamos la store de productos (Pinia)
const productoStore = useProductoStore();

// estados locales para ordenar columnas
const columnaOrden = ref("nombre");
const ascendente = ref(true);

// cargar productos cuando el componente se monta
onMounted(() => {
  productoStore.cargarProductos();
});

// función para ordenar
function ordenar(columna) {
  if (columnaOrden.value === columna) {
    ascendente.value = !ascendente.value;
  } else {
    columnaOrden.value = columna;
    ascendente.value = true;
  }
}

// productos ordenados dinámicamente
const productosOrdenados = computed(() => {
  return [...productoStore.productos].sort((a, b) => {
    let valA = a[columnaOrden.value];
    let valB = b[columnaOrden.value];

    if (typeof valA === "string") valA = valA.toLowerCase();
    if (typeof valB === "string") valB = valB.toLowerCase();

    if (valA < valB) return ascendente.value ? -1 : 1;
    if (valA > valB) return ascendente.value ? 1 : -1;
    return 0;
  });
});
</script>

<style scoped>
.inventario-wrapper {
  width: 90%;
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  font-family: 'Segoe UI', sans-serif;
}

.inventario-card {
  background: #fffaf3;
  padding: 20px;
  border-radius: 16px;
  box-shadow: inset 0 0 0 2px #f8c471;
  border: 1px solid #f5cba7;
}

.inventario-title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
}

.table-scroll {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
  scrollbar-color: #f8c471 transparent;
  scrollbar-width: thin;
}

.table-scroll::-webkit-scrollbar {
  width: 8px;
}

.table-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.table-scroll::-webkit-scrollbar-thumb {
  background-color: #f8c471;
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: content-box;
}

.table-card .table-header,
.table-card .table-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
}

.table-card .table-header {
  background: #f8c471;
  color: #4d2c0c;
  text-transform: uppercase;
  font-weight: bold;
  cursor: pointer;
}

.table-card .table-row {
  background: #fdf6ec;
  margin-top: 8px;
  transition: 0.2s;
}

.table-card .table-row:nth-child(even) {
  background: #faebd7;
}

.table-card .table-row:hover {
  background: #f5cba7;
  transform: scale(1.01);
}

.cell {
  color: #333;
}
</style>