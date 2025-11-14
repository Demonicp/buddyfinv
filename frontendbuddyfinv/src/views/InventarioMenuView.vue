<template>
    <div class="inventario-menu">
      <h2>Inventario</h2>
  
      <div class="botones">
        <button @click="$router.push({ name: 'VerInventario' })">Ver inventario</button>
        <button @click="$router.push({ name: 'AgregarProducto' })">Añadir producto</button>
        <button @click="$router.push({ name: 'ModificarProducto' })">Modificar producto</button>
        <button @click="$router.push({ name: 'ReabastecerProducto' })">Reabastecer producto</button>
      </div>
    </div>
  </template>
  
  <style scoped>
  .inventario-menu {
    text-align: center;
    margin-top: 50px;
  }
  
  .botones {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
    margin-top: 20px;
  }
  
  button {
    background-color: #f39c12;
    color: white;
    border: none;
    padding: 12px 20px;
    border-radius: 8px;
    width: 250px;
    font-size: 16px;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  button:hover {
    background-color: #e67e22;
  }
  </style>