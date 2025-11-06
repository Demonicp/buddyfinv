<template>
  <div class="inventario">
    <h2>Visualiza, añade y modifica tus productos</h2>

    <table>
      <thead>
        <tr>
          <th @click="ordenar('id')">Código</th>
          <th @click="ordenar('nombre')">Nombre</th>
          <th @click="ordenar('precio')">Precio</th>
          <th @click="ordenar('tipoProducto')">Tipo</th>
          <th @click="ordenar('estadoProducto')">Estado</th>
          <th @click="ordenar('propietario')">Propietario</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="producto in productosOrdenados" :key="producto.id">
          <td>{{ producto.id }}</td>
          <td>{{ producto.nombre }}</td>
          <td>{{ producto.precio }}</td>
          <td>{{ producto.tipoProducto }}</td>
          <td>{{ producto.estadoProducto }}</td>
          <td>{{ producto.propietario }}</td>
        </tr>
      </tbody>
    </table>
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
.inventario {
  padding: 20px;
}

.inventario h2 {
  color: #d354009a; 
  margin-bottom: 1.5rem;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
  background-color:#fff3d4;
  color: #c2a252;
  font-family: Arial, sans-serif;
}

th {
  background-color: #ffa94d; 
  color: white;
  cursor: pointer;
  padding: 10px;
  text-align: left;
}

td {
  border: 1px solid white; /*borde*/
  padding: 10px;
}

tr:nth-child(even) {
  background-color: #fff3e0; /* fila alterna */
}

th:hover {
  background-color: #ff7b00; /* pasar el cursor */
}
</style>