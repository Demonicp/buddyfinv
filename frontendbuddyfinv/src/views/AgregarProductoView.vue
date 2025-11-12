<template>
    <div>
      <h3>Añadir producto</h3>
  
      <form>
        <label>Código:</label>
        <input type="number" maxlength="6" />
  
        <label>Nombre:</label>
        <input type="text" maxlength="150" />
  
        <label>Tipo de producto:</label>
        <select>
          <option>Frutas</option>
          <option>Verduras</option>
          <option>Panes</option>
        </select>
  
        <label>Precio:</label>
        <input type="number" maxlength="10" />
  
        <label>Estado:</label>
        <select>
          <option>Disponible</option>
          <option>No disponible</option>
        </select>
  
        <div class="botones">
          <button type="button">Cancelar</button>
          <button type="submit">Confirmar</button>
        </div>
      </form>
    </div>
  </template>
  
  <style scoped>
  form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    max-width: 400px;
  }
  
  input, select {
    padding: 8px;
  }
  
  button {
    margin-top: 10px;
    padding: 8px;
  }
  </style>