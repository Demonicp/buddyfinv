
import { ProductoDTO } from '../models/Producto.js'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const PRODUCTOS_BASE = `${API_BASE_URL}/productos`

// Función auxiliar para manejar respuestas del backend
async function handleResponse(response) {
  if (!response.ok) {
    const text = await response.text().catch(() => '')
    const message = text || `${response.status} ${response.statusText}`
    throw new Error(message)
  }

  const contentType = response.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return response.json()
  }
  return response.text()
}

export const ProductoProvider = {
  // 🔹 Obtener todos los productos
  async getAll() {
    const res = await fetch(`${PRODUCTOS_BASE}/all`, {
      method: 'GET',
      credentials: 'include'
    })
    const data = await handleResponse(res)
    return Array.isArray(data) ? data.map(p => new ProductoDTO(p)) : []
  },

  // 🔹 Guardar o actualizar un producto
  async save(producto) {
    const res = await fetch(`${PRODUCTOS_BASE}/save`, {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(producto)
    })
    return handleResponse(res)
  },

  // 🔹 Eliminar un producto por ID
  async delete(id) {
    const res = await fetch(`${PRODUCTOS_BASE}/delete/${id}`, {
      method: 'DELETE',
      credentials: 'include'
    })
    return handleResponse(res)
  }
}