<template>
  <div class="dashboard-container">
    <!-- Dashboard Content -->
    <div class="dashboard-content">
      <h1 class="dashboard-title">DASHBOARD</h1>
      <p class="dashboard-subtitle">
        BIENVENIDO DE NUEVO. AQUÍ TIENES UN RESUMEN DE LA ACTIVIDAD DE TU NEGOCIO.
      </p>

      <!-- Cards -->
      <div class="cards-container">
        <div class="card ingresos-card" @click="irAIngresos">
          <div class="card-button">
            <span class="card-title">INGRESOS</span>
          </div>
          <p class="card-description">MIRA EL REGISTRO DE TUS INGRESOS</p>
        </div>

        <div class="card egresos-card" @click="irAEgresos">
          <div class="card-button">
            <span class="card-title">EGRESOS</span>
          </div>
          <p class="card-description">MIRA EL REGISTRO DE TUS EGRESOS</p>
        </div>

        <div class="card ventas-card" @click="irAVentas">
          <div class="card-button">
            <span class="card-title">VENTAS</span>
          </div>
          <p class="card-description">MIRA EL REGISTRO DE TUS VENTAS</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const irAVentas = () => {
  router.push({ name: 'Ventas' })
}

const irAIngresos = () => {
  router.push({ name: 'Ingresos' })
}

const irAEgresos = () => {
  router.push({ name: 'Egresos' })
}
</script>

<style scoped>
.dashboard-container {
  width: 100%;
  min-height: 100vh;
  background-color: #ffffff;
  background-image: 
    linear-gradient(rgba(0, 0, 0, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.02) 1px, transparent 1px);
  background-size: 20px 20px;
  padding: 0;
}

.dashboard-content {
  padding: 2rem;
  max-width: 1200px;
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
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}

.card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 3rem;
  padding: 2rem 2.5rem;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.ventas-card {
  background-color: #e3f2fd;
}

.ingresos-card {
  background-color: #e8f5e9;
}

.egresos-card {
  background-color: #ffebee;
  
}

.card-button {
  padding: 2.25rem 3.5rem;
  border-radius: 8px;
  min-width: 240px;
  text-align: center;
  box-shadow: inset 0 0 0 2px rgba(255, 255, 255, 0.4);
}

.ventas-card .card-button {
  background-color: #90caf9;
}

.ingresos-card .card-button {
  background-color: #a5d6a7;
}

.egresos-card .card-button {
  background-color: #ef9a9a;
}

.card-title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  text-decoration: underline;
  display: block;
}

.card-description {
  font-size: 1.1rem;
  color: #555;
  margin: 0;
  flex: 1;
  text-align: left;
}
</style>
