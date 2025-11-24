<template>
  <div class="inventario-wrapper">
    <div class="inventario-card">
      <h2 class="inventario-title">Visualiza, añade y modifica tus productos</h2>

      <!-- 🔹 Bloque de filtros -->
      <div class="filtros-card">
        <h3 class="filtros-title">Filtrar productos</h3>
        <div class="filtros-content">
          <input v-model="codigo" placeholder="Código producto" />
          <input v-model="nombre" placeholder="Nombre producto" />
          <select v-model="idTipoSeleccionado">
            <option value="">-- Selecciona tipo --</option>
            <option
              v-for="tipo in tipos"
              :key="tipo.idTipoProducto"
              :value="tipo.idTipoProducto"
            >
              {{ tipo.observacion }}
            </option>
          </select>

          <button @click="buscarProducto">Buscar producto</button>
          <button @click="eliminarFiltro">Eliminar filtro</button>
        </div>

        <!-- 🔹 Mensaje de error -->
        <div v-if="errorBusqueda" class="error-card">
          <p class="error-msg">{{ errorBusqueda }}</p>
        </div>
      </div>

      <!-- 🔹 Tabla -->
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
            <!-- 🔹 Ajuste: mostrar observacion si viene como objeto -->
            <span class="cell">{{ producto.tipoProducto?.observacion || producto.tipoProducto }}</span>
            <!-- 🔹 Ajuste: mostrar nombre si propietario viene como objeto -->
            <span class="cell">{{ producto.propietario?.nombre || producto.propietario }}</span>
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
import { ProductoSelectorProvider } from "@/providers/ProductoSelectorProvider";

const productoStore = useProductoStore();

// 🔹 Estados locales
const columnaOrden = ref("nombre");
const ascendente = ref(true);
const codigo = ref("");
const nombre = ref("");
const idTipoSeleccionado = ref("");
const tipos = ref([]);
const productosFiltrados = ref([]);
const errorBusqueda = ref("");

// 🔹 Cargar datos iniciales
onMounted(async () => {
  productoStore.cargarProductos();
  tipos.value = await ProductoSelectorProvider.obtenerTiposProducto();
});

// 🔹 Función de búsqueda unificada con manejo de errores
async function buscarProducto() {
  errorBusqueda.value = "";

  if (!codigo.value && !nombre.value && !idTipoSeleccionado.value) {
    errorBusqueda.value = "Debes completar por lo menos un campo de búsqueda";
    return;
  }

  try {
    productosFiltrados.value = await ProductoSelectorProvider.buscarInventario({
      idProducto: codigo.value || null,
      nombre: nombre.value || null,
      idTipoProducto: idTipoSeleccionado.value || null,
    });
    console.log("Productos filtrados:", productosFiltrados.value); // 👀 Debug
  } catch (error) {
    errorBusqueda.value = error.message;
    productosFiltrados.value = [];
  }
}

// 🔹 Eliminar filtro y restaurar vista inicial
function eliminarFiltro() {
  codigo.value = "";
  nombre.value = "";
  idTipoSeleccionado.value = "";
  productosFiltrados.value = [];
  errorBusqueda.value = "";
}

// 🔹 Ordenar productos
function ordenar(columna) {
  if (columnaOrden.value === columna) {
    ascendente.value = !ascendente.value;
  } else {
    columnaOrden.value = columna;
    ascendente.value = true;
  }
}

// 🔹 Computed: usa filtrados si existen, si no usa store
const productosOrdenados = computed(() => {
  const base =
    productosFiltrados.value.length > 0
      ? productosFiltrados.value
      : productoStore.productos;

  return [...base].sort((a, b) => {
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

/* 🔹 Tarjeta de filtros */
.filtros-card {
  background: #fdf6ec;
  border: 1px solid #f5cba7;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 6px rgba(230, 126, 34, 0.2);
}

.filtros-title {
  color: #d35400;
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 12px;
  text-align: center;
}

.filtros-content {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: space-between;
}

.filtro-item {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 🔹 Inputs y select */
.filtro-item input,
.filtro-item select {
  padding: 6px 10px;
  border: 1px solid #f5cba7;
  border-radius: 8px;
  background: #fffaf3;
  font-size: 14px;
  color: #333;
}

/* 🔹 Botones */
.filtro-item button {
  background: #e67e22;
  color: #fff;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s, transform 0.2s;
}

.filtro-item button:hover {
  background: #d35400;
  transform: scale(1.05);
}

/* 🔹 Tabla */
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
.error-msg {
  color: red;
  font-weight: bold;
  margin-top: 8px;
}

.filtros-card {
  background: #fdf6ec;
  border: 1px solid #f5cba7;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 6px rgba(230, 126, 34, 0.2);
}

.filtros-title {
  color: #d35400;
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 12px;
  text-align: center;
}

.filtros-content {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: space-between;
}

.filtros-content input,
.filtros-content select {
  padding: 6px 10px;
  border: 1px solid #f5cba7;
  border-radius: 8px;
  background: #fffaf3;
  font-size: 14px;
  color: #333;
}

.filtros-content button {
  background: #e67e22;
  color: #fff;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s, transform 0.2s;
}

.filtros-content button:hover {
  background: #d35400;
  transform: scale(1.05);
}

</style>