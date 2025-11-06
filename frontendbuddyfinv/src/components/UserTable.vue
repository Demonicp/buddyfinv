<template>
  <div class="user-table-container">
    <h2 class="title">Lista de Usuarios</h2>

    <div class="table-header" role="row">
      <span>Rol</span>
      <span>Nombre</span>
      <span>Correo</span>
      <span>Usuario</span>
    </div>

    <div
      v-for="(user, index) in usuarios"
      :key="user.id || index"
      class="table-row"
      role="row"
    >
      <span class="cell">{{ user.rol.descripcion }}</span>
      <span class="cell">{{ user.nombre }}</span>
      <span class="cell">{{ user.email }}</span>
      <span class="cell">{{ user.usuario }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UsuarioTable',
  props: {
    usuarios: {
      type: Array,
      required: true
    }
  }
}
</script>

<style scoped>
.user-table-container {
  width: 95%;
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: "Segoe UI", sans-serif;
}

.title {
  text-align: center;
  color: #ff8800;
  margin-bottom: 20px;
  font-size: 22px;
  font-weight: bold;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.table-header {
  background: #ff8800;
  color: white;
  text-transform: uppercase;
  font-weight: bold;
}

.table-row {
  background: #f1f1f1;
  margin-top: 8px;
  transition: 0.2s;
}

.table-row:nth-child(even) {
  background: #eaeaea;
}

.table-row:hover {
  background: #ffd9b3;
  transform: scale(1.01);
}

.cell {
  color: #333;
}
</style>