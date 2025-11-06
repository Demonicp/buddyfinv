// providers/ventaProvider.js
import { crearVentaDTO } from '../models/VentaDTO.js'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const VENTAS_BASE = `${API_BASE_URL}/api/ventas`

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

export const VentaProvider = {
  async getDetalladas() {
    const token = localStorage.getItem('token') // Asegúrate de que el token esté guardado correctamente

    const res = await fetch(`${VENTAS_BASE}/detalladas`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })

    const data = await handleResponse(res)
    return Array.isArray(data) ? data.map(crearVentaDTO) : []
  }
}