
import { ProductoDTO } from '../models/Producto.js'
import { ProductoEdicionDTO } from '../models/ProductoEdicion.js'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const PRODUCTOS_BASE = `${API_BASE_URL}/productos`

// Función para obtener el header de autorización con el token
function getAuthHeader() {
  const token = localStorage.getItem('token')
  return token ? { 'Authorization': `Bearer ${token}` } : {}
}

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

// Función para obtener el ID del usuario desde el token
function getUserIdFromToken() {
  try {
    const token = localStorage.getItem('token')
    if (!token) return null

    const payloadBase64 = token.split('.')[1]
    const payload = JSON.parse(atob(payloadBase64))
    return payload.idUsuario || null
  } catch (error) {
    console.error('Error al decodificar el token:', error)
    return null
  }
}

export const ProductoProvider = {
  // 🔹 Obtener todos los productos
  async getAll() {
    const res = await fetch(`${PRODUCTOS_BASE}/all`, {
      method: 'GET',
      headers: {
        ...getAuthHeader()
      },
      credentials: 'include'
    })
    const data = await handleResponse(res)
    return Array.isArray(data) ? data.map(p => new ProductoDTO(p)) : []
  },

  // 🔹 Obtener productos del usuario autenticado (basado en el token)
  async getByUsuario() {
    const userId = getUserIdFromToken()
    if (!userId) {
      throw new Error('No se pudo obtener el ID del usuario desde el token')
    }

    const res = await fetch(`${PRODUCTOS_BASE}/propietario`, {
      method: 'GET',
      headers: {
        ...getAuthHeader()
      },
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
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader()
      },
      body: JSON.stringify(producto)
    })
    return handleResponse(res)
  },

  // 🔹 Eliminar un producto por ID
  async delete(id) {
    const res = await fetch(`${PRODUCTOS_BASE}/delete/${id}`, {
      method: 'DELETE',
      headers: {
        ...getAuthHeader()
      },
      credentials: 'include'
    })
    return handleResponse(res)
  },



  //////////////////SANTIAGO MONTENEGRO RUALES MODIFICAR PRODUCTO

  async buscarPorCodigo(idProducto) {
    const res = await fetch(`${PRODUCTOS_BASE}/modificar/buscar/${idProducto}`, {
      method: 'GET',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    })
    const data = await handleResponse(res)
    return new ProductoEdicionDTO(data)
  },
  async guardarEdicion(productoEditadoDTO) {
    const res = await fetch(`${PRODUCTOS_BASE}/modificar/guardar`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader()
      },
      credentials: 'include',
      body: JSON.stringify(productoEditadoDTO)
    })
    return handleResponse(res)
  }
  //////////////////SANTIAGO MONTENEGRO RUALES MODIFICAR PRODUCTO FIN
}