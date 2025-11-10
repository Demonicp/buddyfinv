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
  color: #e6dfda9a; 
  margin-bottom: 1.5rem;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
  background-color: white;
  color: #100f0d;
  font-family: Arial, sans-serif;
}

th {
  background-color: #e89236; 
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
  background-color: #f5f5f5; /* fila alterna */
}

th:hover {
  background-color: #ff7b00; /* pasar el cursor */
}
</style>