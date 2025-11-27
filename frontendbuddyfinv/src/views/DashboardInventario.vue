<template>
  <div class="dashboard-container">
    <section class="dashboard-content">
      <header>
        <h1 class="dashboard-title">Dashboard Productos</h1>
        <p class="dashboard-subtitle">
          Vizualiza, añade, modifica y reabastece el inventario de tu negocio.
        </p>
      </header>

      <div class="cards-container">
        <!-- Ver inventario (visible para todos) -->
        <article 
          class="card card--inventario" 
          role="button" 
          tabindex="0" 
          @click="irAInventario"
        >
          <div class="card-button">Ver inventario</div>
          <p class="card-description">Consulta el estado actual del inventario</p>
        </article>

        <!-- Añadir producto (solo admin) -->
        <article 
          class="card card--agregar" 
          role="button" 
          tabindex="0" 
          v-if="rol === 'ADMIN'"
          @click="irAAñadirProducto"
        >
          <div class="card-button">Añadir producto</div>
          <p class="card-description">Registra un nuevo producto en el sistema</p>
        </article>

        <!-- Modificar producto (solo admin) -->
        <article 
          class="card card--modificar" 
          role="button" 
          tabindex="0" 
          v-if="rol === 'ADMIN'"
          @click="irAModificarProducto"
        >
          <div class="card-button">Modificar producto</div>
          <p class="card-description">Edita la información de un producto existente</p>
        </article>

        <!-- Reabastecer (visible para todos) -->
        <article 
          class="card card--reabastecer" 
          role="button" 
          tabindex="0" 
          @click="irAReabastecerProducto"
        >
          <div class="card-button">Reabastecer producto</div>
          <p class="card-description">Actualiza el stock de productos agotados</p>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { jwtDecode } from 'jwt-decode'

const router = useRouter()

// Obtener rol real desde el token
const token = localStorage.getItem('token')
let rol = ''

if (token) {
  try {
    const decoded = jwtDecode(token)
    rol = decoded.rol || decoded.authorities?.[0] || ''
  } catch (e) {
    console.error('Error decodificando token:', e)
  }
}

// Funciones de navegación
const irAInventario = () => router.push({ name: 'VerInventario' })
const irAAñadirProducto = () => router.push({ name: 'AñadirProducto' })
const irAModificarProducto = () => router.push({ name: 'ModificarProducto' })
const irAReabastecerProducto = () => router.push({ name: 'ReabastecerProducto' })
</script>

<style scoped>

@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@100..900&display=swap');
.cards-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.card {
  padding: 1.25rem 1.5rem;
  border-radius: 10px;
  font-size: 0.95rem;
}

.dashboard-content {
  padding: 2rem;
  font-family: 'Outfit', sans-serif;
  margin: 0 auto;
}

.dashboard-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.5rem;
  font-family: 'Segoe UI', sans-serif;
}

.dashboard-subtitle {
  font-size: 1rem;
  color: #666;
  margin-bottom: 3rem;
  line-height: 1.5;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 2rem;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-button {
  font-size: 1.2rem;
  font-weight: bold;
  text-align: center;
  padding: 0.75rem;
  border-radius: 6px;
  margin-bottom: 0.75rem;
  background-color: rgba(255, 255, 255, 0.4);
  box-shadow: inset 0 0 0 2px rgba(255, 255, 255, 0.3);
  color: #000;
}

.card-description {
  font-size: 0.95rem;
  color: #000;
  text-align: center;
}

/* Colores personalizados */
.card--inventario {
  background-color: #e0f7fa;
}
.card--agregar {
  background-color: #e8f5e9;
}
.card--modificar {
  background-color: #fff3e0;
}
.card--reabastecer {
  background-color: #fce4ec;
}
</style>