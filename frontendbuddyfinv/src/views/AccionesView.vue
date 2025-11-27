<template>
  <div class="dashboard-container">
    <section class="dashboard-content">
      <!-- Si hay una ruta hija activa (registrar-ventas o ventas) mostramos solo el hijo -->
      <router-view v-if="isChildActive" />

      <!-- Si no, mostramos header y cards (comportamiento original) -->
      <template v-else>
        <header>
          <h1 class="dashboard-title">Acciones</h1>
          <p class="dashboard-subtitle">
            Acciones rápidas para tu negocio. Desde aquí puedes registrar ventas y ejecutar tareas operativas.
          </p>
        </header>

        <div class="cards-container">
          <article
            class="card card--registrar"
            role="button"
            tabindex="0"
            @click="$router.push({ name: 'RegistrarVentas' })"
          >
            <div class="card-button">Registrar ventas</div>
            <p class="card-description">Registrar una nueva venta y gestionar los detalles de la transacción</p>
          </article>

          <article
            class="card card--otra"
            role="button"
            tabindex="0"
            @click="otraAccion"
          >
            <div class="card-button">Consultar Recibo de Venta</div>
            <p class="card-description">Aqui puedes descargar recibos de tus ventas generadas</p>
          </article>
        </div>
      </template>

      <!-- mantiene el area de render de hijos en caso de necesitarla (no se mostrará si isChildActive true) -->
      <div class="child-view" v-if="false">
        <router-view />
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'AccionesView',
  computed: {
    // detecta si la ruta activa corresponde a alguna ruta hija de acciones
    isChildActive() {
      const name = this.$route && this.$route.name
      const path = this.$route && this.$route.path
      // nombres de rutas hijas que definimos en router: 'RegistrarVentas', 'Ventas', 'ConsultarVenta'
      if (name === 'RegistrarVentas' || name === 'AccionesVentas' || name === 'Ventas' || name === 'ConsultarVenta') return true
      // también detecta cualquier path bajo /dashboard/acciones/
      if (typeof path === 'string' && path.includes('/dashboard/acciones/')) return true
      return false
    }
  },
  methods: {
    otraAccion() {
      this.$router.push({ name: 'ConsultarVenta' })
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  height: 90% ;
}

.dashboard-content {
  padding: 1rem;
  max-width: 100vw;
  height: 90%;

  font-family: 'Segoe UI', sans-serif;
}

.dashboard-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.5rem;
}

.dashboard-subtitle {
  font-size: 1rem;
  color: #666;
  margin-bottom: 2rem;
  line-height: 1.5;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 1.5rem;
}

.card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 2rem;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.18s, box-shadow 0.18s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  background: linear-gradient(180deg, rgba(255,255,255,0.6), rgba(255,255,255,0.3));
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.08);
}

.card-button {
  font-size: 1.15rem;
  font-weight: 700;
  text-align: center;
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 0.75rem;
  background-color: rgba(255, 255, 255, 0.45);
  color: #111;
  box-shadow: inset 0 0 0 2px rgba(255,255,255,0.2);
}

.card-description {
  font-size: 0.95rem;
  color: #222;
  text-align: center;
}

/* colores */
.card--registrar {
  background-color: #e8f5e9;
}
.card--otra {
  background-color: #e0f7fa;
}

/* area hija (formulario, etc) */
.child-view {
  margin-top: 1rem;
  padding: 1rem;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

/* responsive tweaks */
@media (max-width: 640px) {
  .dashboard-title { font-size: 1.6rem; }
  .card { padding: 1.25rem; }
}
</style>