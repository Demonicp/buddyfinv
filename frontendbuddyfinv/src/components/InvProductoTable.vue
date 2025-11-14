<template>
  <div class="inventario-container">
    <h2 class="title">Visualiza tus productos</h2>

    <div class="scroll-wrapper">
      <div class="scroll-card">
        <table>
          <thead>
            <tr>
              <th @click="ordenar('id')">Código</th>
              <th @click="ordenar('nombre')">Nombre</th>
              <th @click="ordenar('precio')">Precio</th>
              <th @click="ordenar('tipoProducto')">Tipo</th>
              <th @click="ordenar('propietario')">Propietario</th>
              <th @click="ordenar('cantidadDisponible')">Cantidad Disponible</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="producto in productosOrdenados" :key="producto.id">
              <td>{{ producto.id }}</td>
              <td>{{ producto.nombre }}</td>
              <td>{{ producto.precio }}</td>
              <td>{{ producto.tipoProducto }}</td>
              <td>{{ producto.propietario }}</td>
              <td>{{ producto.cantidadDisponible ?? 0 }}</td>
            </tr>
          </tbody>
        </table>
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
.inventario-container {
  width: 95%;
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', sans-serif;
}

.title {
  text-align: center;
  color: #e67e22;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: bold;
}

/* Scroll externo */
.scroll-wrapper {
  max-height: 520px;
  overflow-y: auto;
  padding-right: 8px;
  margin-top: 1rem;
  scrollbar-color: #f8c471 transparent;
  scrollbar-width: thin;
}

.scroll-wrapper::-webkit-scrollbar {
  width: 8px;
}

.scroll-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.scroll-wrapper::-webkit-scrollbar-thumb {
  background-color: #f8c471;
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: content-box;
}

/* Tarjeta con bordes suaves y espacio interno */
.scroll-card {
  padding: 2rem 1rem;
  border-radius: 16px;
  background-color: #fffaf3;
  box-shadow: inset 0 0 0 2px #f8c471;
  border: 1px solid #f5cba7;
}

/* Tabla */
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
  background-color: white;
  color: #100f0d;
  font-family: 'Segoe UI', sans-serif;
}

th {
  background-color: #f8c471;
  color: #4d2c0c;
  cursor: pointer;
  padding: 12px;
  text-align: center;
  font-size: 14px;
  text-transform: uppercase;
  vertical-align: middle;
}

td {
  padding: 12px;
  font-size: 14px;
  color: #333;
  border-top: 1px solid #f8c471;
  text-align: center;
  vertical-align: middle;
}

tr:nth-child(even) {
  background-color: #fdf6ec;
}

th:hover {
  background-color: #e67e22;
}
</style>