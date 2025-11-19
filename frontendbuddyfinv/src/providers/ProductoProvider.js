import { ProductoDTO } from '../models/Producto.js'
import { ProductoEdicionDTO } from '../models/ProductoEdicion.js'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const PRODUCTOS_BASE = `${API_BASE_URL}/productos`

function getAuthHeader() {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function handleResponse(response) {
  if (!response.ok) {
    const text = await response.text().catch(() => '')
    const contentType = response.headers.get('content-type') || ''
    let body = text
    try {
      if (text && contentType.includes('application/json')) {
        body = JSON.parse(text)
      }
    } catch (e) {
      body = text
    }
    const message = (body && (body.message || body.error)) ? (body.message || body.error) : (text || `${response.status} ${response.statusText}`)
    const err = new Error(message)
    err.status = response.status
    err.body = body
    throw err
  }

  const contentType = response.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return response.json()
  }
  return response.text()
}

function getUserIdFromToken() {
  try {
    const token = localStorage.getItem('token')
    if (!token) return null
    const payloadBase64 = token.split('.')[1]
    const payload = JSON.parse(atob(payloadBase64))
    return payload.idUsuario || payload.id || null
  } catch (error) {
    console.error('Error al decodificar el token:', error)
    return null
  }
}

export const ProductoProvider = {
  // Obtener todos los productos
  async getAll() {
    const res = await fetch(`${PRODUCTOS_BASE}/all`, {
      method: 'GET',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    })
    const data = await handleResponse(res)
    return Array.isArray(data) ? data.map(p => new ProductoDTO(p)) : []
  },

  // Obtener productos del usuario autenticado
  async getByUsuario() {
    const userId = getUserIdFromToken()
    if (!userId) {
      throw new Error('No se pudo obtener el ID del usuario desde el token')
    }

    const res = await fetch(`${PRODUCTOS_BASE}/propietario`, {
      method: 'GET',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    })
    const data = await handleResponse(res)
    return Array.isArray(data) ? data.map(p => new ProductoDTO(p)) : []
  },

  // Guardar o actualizar un producto (endpoint /save asumido)
  async save(producto) {
    const res = await fetch(`${PRODUCTOS_BASE}/save`, {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(producto)
    })
    return handleResponse(res)
  },

  // Eliminar un producto por ID
  async delete(id) {
    const res = await fetch(`${PRODUCTOS_BASE}/delete/${id}`, {
      method: 'DELETE',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    })
    return handleResponse(res)
  },

  // Buscar producto por código para edición
  async buscarPorCodigo(idProducto) {
    const res = await fetch(`${PRODUCTOS_BASE}/modificar/buscar/${idProducto}`, {
      method: 'GET',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    })
    const data = await handleResponse(res)
    return new ProductoEdicionDTO(data)
  },

  // Guardar edición de producto
  async guardarEdicion(productoEditadoDTO) {
    const res = await fetch(`${PRODUCTOS_BASE}/modificar/guardar`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      credentials: 'include',
      body: JSON.stringify(productoEditadoDTO)
    })
    return handleResponse(res)
  },

  // Buscar por id (usa GET /productos/{id})
  async buscarPorId(id) {
    const res = await fetch(`${PRODUCTOS_BASE}/${id}`, {
      method: 'GET',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    })
    return handleResponse(res)
  },

  // Buscar por texto para selector/autocomplete (usa GET /productos/search?q=&limit=)
  async search(q, limit = 12) {
    const params = new URLSearchParams({ q, limit: String(limit) }).toString()
    const res = await fetch(`${PRODUCTOS_BASE}/search?${params}`, {
      method: 'GET',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    })
    const data = await handleResponse(res)
    // devolver instancias ProductoDTO para consistencia en la UI
    return Array.isArray(data) ? data.map(p => new ProductoDTO(p)) : []
  },

  // Compatibilidad: listarParaSelector(params) -> usa /search por defecto
  async listarParaSelector(params = {}) {
    const qs = new URLSearchParams(params).toString()
    // si el caller pasa q/limit, reaprovechamos search; si no, llamamos /search sin q devolviendo []
    if (!params.q) return []
    return this.search(params.q, params.limit || 12)
  }
}