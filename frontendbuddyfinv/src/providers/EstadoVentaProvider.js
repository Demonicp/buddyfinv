import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
});

/**
 * Obtiene el catálogo de estados de venta.
 * @param {string|null} token Bearer token opcional (si tu API requiere auth)
 * @returns {Promise<Array>} lista de objetos { id, nombre } (o la forma que devuelva tu DTO)
 */
export async function fetchEstadosVenta(token = null) {
  try {
    const headers = token ? { Authorization: `Bearer ${token}` } : {};
    const res = await api.get('/EstadoVenta/estados', { headers });
    return res.data;
  } catch (err) {
    // lanza error uniforme para el caller
    const message = err?.response?.data?.message || err.message || 'Error al cargar estados de venta';
    throw new Error(message);
  }
}