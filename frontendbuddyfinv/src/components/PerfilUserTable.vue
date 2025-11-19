<template>
    <div class="perfil-user-wrapper">
      <button class="perfil-user-button" @click="toggleMenu">
        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" viewBox="0 0 16 16">
          <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10z"/>
        </svg>
      </button>
  
      <div v-if="menuVisible" class="perfil-user-card" @click.stop>
        <p><strong>Usuario:</strong> {{ perfil.usuario }}</p>
        <p><strong>Nombre:</strong> {{ perfil.nombre }}</p>
        <p><strong>Email:</strong> {{ perfil.email }}</p>
        <p><strong>Rol:</strong> {{ perfil.rol }}</p>
      </div>
    </div>
  </template>
  
  <script>
  import PerfilUsuario from '@/models/PerfilUsuario';
  import { UsuarioProvider } from '@/providers/UsuarioProvider';
  
  export default {
    name: 'PerfilUserTable',
    data() {
      return {
        perfil: new PerfilUsuario(),
        menuVisible: false,
      };
    },
    async mounted() {
      document.addEventListener('click', this.handleClickOutside);
      const data = await UsuarioProvider.getPerfil();
      this.perfil = new PerfilUsuario(data);
    },
    beforeUnmount() {
      document.removeEventListener('click', this.handleClickOutside);
    },
    methods: {
      toggleMenu() {
        this.menuVisible = !this.menuVisible;
      },
      handleClickOutside(event) {
        if (!this.$el.contains(event.target)) {
          this.menuVisible = false;
        }
      },
    },
  };
  </script>
  
  <style scoped>
  .perfil-user-wrapper {
    position: relative;
    display: inline-block;
    margin-right: 20px;
  }
  .perfil-user-button {
    background: transparent;
    border: none;
    cursor: pointer;
  }
  .perfil-user-card {
    position: absolute;
    top: 40px;
    right: 0;
    background: #fff7e6;
    border: 1px solid #f0d9b5;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    padding: 12px;
    border-radius: 10px;
    min-width: 220px;
    z-index: 1000;
    font-size: 14px;
    color: #5a3e1b;
  }
  .perfil-user-card p {
    margin: 6px 0;
    font-weight: 500;
  }
  </style>