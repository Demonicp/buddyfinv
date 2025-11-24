<template>
    <div class="dashboard-container">
      <section class="dashboard-content">
        <header>
          <h1 class="dashboard-title">Dashboard Configuración</h1>
          <p class="dashboard-subtitle">
            Administra usuarios, roles, permisos y preferencias del sistema.
          </p>
        </header>
  
        <div class="cards-container">
          <!-- Usuarios -->
          <article class="card card--usuarios" role="button" tabindex="0"@click="irAUsuarios">
            <div class="card-button">Usuarios</div>
            <p class="card-description">Gestiona los usuarios registrados en el sistema</p>
          </article>
  
          <!-- Roles -->
          <article class="card card--roles" role="button" tabindex="0" @click="irARoles">
            <div class="card-button">Roles</div>
            <p class="card-description">Define y edita los roles disponibles</p>
          </article>
  
          <!-- Permisos -->
          <article class="card card--permisos" role="button" tabindex="0" @click="irAPermisos">
            <div class="card-button">Permisos</div>
            <p class="card-description">Configura los permisos por rol</p>
          </article>
  
          <!-- Preferencias -->
          <article class="card card--preferencias" role="button" tabindex="0" @click="irAPreferencias">
            <div class="card-button">Preferencias</div>
            <p class="card-description">Personaliza la experiencia del sistema</p>
          </article>
  
          <!-- Eliminar Usuario -->
           <!-- Agregar Empleado -->
          <article class="card card--empleados" role="button" tabindex="0" @click="irAEmpleados">
            <div class="card-button">Empleados</div>
            <p class="card-description">Gestiona empleados y crea nuevos registros</p>
          </article>
        </div>
      </section>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router'
  const router = useRouter()
  
  
  const irARoles = () => router.push({ name: 'Roles' })
  const irAPermisos = () => router.push({ name: 'Permisos' })
  const irAPreferencias = () => router.push({ name: 'Preferencias' })
  const irAUsuarios = () => router.push({ name: 'Usuario' })
  const irAEmpleados = () => router.push({name: 'ListaEmpleados'})
  </script>
  
  <style scoped>
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
  .card--usuarios {
    background-color: #e3f2fd;
  }
  .card--roles {
    background-color: #f1f8e9;
  }
  .card--permisos {
    background-color: #fff8e1;
  }
  .card--preferencias {
    background-color: #fce4ec;
  }
  /* modificada a agregar empleados*/
  .card--empleados {
    background-color: #e8f5e9;
  }
  </style>