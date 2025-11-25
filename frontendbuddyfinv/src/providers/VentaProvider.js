import { crearVentaDTO } from '../models/VentaDTO.js' // conserva si lo usas en algún lugar

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const VENTAS_BASE = `${API_BASE_URL}/ventas`
const METODOS_BASE = `${API_BASE_URL}/MetodoPago`
const ESTADOS_BASE = `${API_BASE_URL}/EstadoVenta/estados`

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

function authHeaders() {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export const VentaProvider = {
  // ventas
  async getDetalladas() {
    // NOTA: El backend ahora devuelve 'empleadoId' (id del usuario/empleado que realizó la venta)
    // Este provider reenvía la respuesta tal cual al frontend para que los components puedan usar 'empleadoId'.
    const token = localStorage.getItem('token')
    if (!token) throw new Error('Token no disponible')
    const res = await fetch(`${VENTAS_BASE}/detalladas`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })
    return handleResponse(res)
  },

  async crearVenta(ventaCrearDto) {
    const res = await fetch(`${VENTAS_BASE}/crearVenta`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders()
      },
      body: JSON.stringify(ventaCrearDto)
    })
    return handleResponse(res)
  },

  async listarVentas(params = {}) {
    const qs = new URLSearchParams(params).toString()
    const res = await fetch(`${VENTAS_BASE}${qs ? `?${qs}` : ''}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders()
      }
    })
    return handleResponse(res)
  },

  async obtenerVenta(id) {
    const res = await fetch(`${VENTAS_BASE}/${id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders()
      }
    })
    return handleResponse(res)
  },

  // catálogos reutilizables: métodos de pago y estados de venta
  async fetchMetodosPago() {
    const res = await fetch(METODOS_BASE, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders()
      }
    })
    return handleResponse(res)
  },

  async fetchEstadosVenta() {
    const res = await fetch(ESTADOS_BASE, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...authHeaders()
      }
    })
    return handleResponse(res)
  }
}