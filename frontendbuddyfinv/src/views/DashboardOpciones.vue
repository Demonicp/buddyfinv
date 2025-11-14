<template>
  <div class="dashboard-container">
    <div class="dashboard-content">
      <h1 class="dashboard-title">Dashboard Administrador</h1>
      <p class="dashboard-subtitle">
        BIENVENIDO DE NUEVO. AQUÍ TIENES UN RESUMEN DE LA ACTIVIDAD DE TU NEGOCIO.
      </p>

      <div class="cards-container">
        <div class="card card--ingresos" @click="irAIngresos">
          <div class="card-button">
            <span class="card-title">Ingresos</span>
          </div>
          <p class="card-description">Mira el registro de tus ingresos</p>
        </div>

        <div class="card card--egresos" @click="irAEgresos">
          <div class="card-button">
            <span class="card-title">Egresos</span>
          </div>
          <p class="card-description">Mira el registro de tus egresos</p>
        </div>

        <div class="card card--ventas" @click="irAVentas">
          <div class="card-button">
            <span class="card-title">Ventas</span>
          </div>
          <p class="card-description">Mira el registro de tus ventas</p>
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
  background-color: #fff;
  font-family: 'Segoe UI', sans-serif;
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
  border-radius: 16px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  font-family: 'Segoe UI', sans-serif;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-button {
  font-size: 1.2rem;
  text-align: center;
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 0.75rem;
  background-color: rgba(255, 255, 255, 0.6);
  box-shadow: inset 0 0 0 2px rgba(0, 0, 0, 0.05);
  color: #333;
}

.card-title {
  font-weight: bold;
}

.card-description {
  font-size: 0.95rem;
  color: #333;
  text-align: center;
}

/* Colores pastel por acción */
.card--ingresos {
  background-color: #d6f0f5;
}

.card--egresos {
  background-color: #fff6da;
}

.card--ventas {
  background-color: #fbe4f0;
}
</style>