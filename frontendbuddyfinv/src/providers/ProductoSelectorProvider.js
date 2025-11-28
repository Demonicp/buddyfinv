// CREADO POR SANTIAGO MONTENEGRO PARA HISTORIA 17
import { ProductoDTO } from "../models/Producto.js"
import { TipoProductoDTO } from "../models/TipoProductoDTO.js"

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
const PRODUCTOS_BASE = `${API_BASE_URL}/productos`

function getAuthHeader() {
  const token = localStorage.getItem("token")
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function handleResponse(response) {
  if (!response.ok) {
    const text = await response.text().catch(() => "")
    const contentType = response.headers.get("content-type") || ""
    let body = text
    try {
      if (text && contentType.includes("application/json")) {
        body = JSON.parse(text)
      }
    } catch (e) {
      body = text
    }
    const message = (body && (body.message || body.error))
      ? (body.message || body.error)
      : (text || `${response.status} ${response.statusText}`)
    const err = new Error(message)
    err.status = response.status
    err.body = body
    throw err
  }

  const contentType = response.headers.get("content-type") || ""
  if (contentType.includes("application/json")) {
    return response.json()
  }
  return response.text()
}

export const ProductoSelectorProvider = {
  // 🔹 Obtener tipos de producto exclusivos del administrador
  async obtenerTiposProducto() {
    const res = await fetch(`${API_BASE_URL}/tipo-producto/mis-tipos`, {
      method: "GET",
      headers: { ...getAuthHeader() },
      credentials: "include"
    })
    const data = await handleResponse(res)
    return Array.isArray(data) ? data.map(t => new TipoProductoDTO(t)) : []
  },

  // 🔹 Buscar productos en inventario con filtros
  async buscarInventario({ idProducto, nombre, idTipoProducto }) {
    const params = new URLSearchParams()
    if (idProducto) params.append("idProducto", idProducto)
    if (nombre) params.append("nombre", nombre)
    if (idTipoProducto) params.append("idTipoProducto", idTipoProducto)

    const res = await fetch(`${PRODUCTOS_BASE}/inventario/search?${params.toString()}`, {
      method: "GET",
      headers: { ...getAuthHeader() },
      credentials: "include"
    })
    const data = await handleResponse(res)
    if (!Array.isArray(data) || data.length === 0) {
      throw new Error("No se encontraron productos con esas características")
    }
    return data.map(p => new ProductoDTO(p))
  },

  // 🔹 Buscar productos para selector/autocomplete
  async buscarSelector(q, limit = 10) {
    const params = new URLSearchParams({ q, limit: String(limit) }).toString()
    const res = await fetch(`${PRODUCTOS_BASE}/search?${params}`, {
      method: "GET",
      headers: { ...getAuthHeader() },
      credentials: "include"
    })
    const data = await handleResponse(res)
    if (!Array.isArray(data) || data.length === 0) {
      throw new Error("No se encontraron productos para el selector")
    }
    return data.map(p => new ProductoDTO(p))
  }
  //FINAL SANTIAGO MONTENEGRO HISTORIA 17
}
