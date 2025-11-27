import { IngresoDTO } from '@/models/IngresoDTO.js';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const INGRESOS_BASE = `${API_BASE_URL}/ingresos`;

function getAuthHeader() {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function handleResponse(response) {
  if (!response.ok) {
    const text = await response.text().catch(() => '');
    const contentType = response.headers.get('content-type') || '';
    let body = text;

    try {
      if (text && contentType.includes('application/json')) {
        body = JSON.parse(text);
      }
    } catch (e) {
      body = text;
    }

    const message =
      (body && (body.message || body.error))
        ? (body.message || body.error)
        : (text || `${response.status} ${response.statusText}`);

    const err = new Error(message);
    err.status = response.status;
    err.body = body;
    throw err;
  }

  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    return response.json();
  }
  return response.text();
}

function getUserIdFromToken() {
  try {
    const token = localStorage.getItem('token');
    if (!token) return null;

    const payloadBase64 = token.split('.')[1];
    const payload = JSON.parse(atob(payloadBase64));

    return payload.idUsuario || payload.id || null;
  } catch (error) {
    console.error('Error al decodificar el token:', error);
    return null;
  }
}

export const IngresoProvider = {

  /** TEST */
  async test() {
    const res = await fetch(`${INGRESOS_BASE}/test`, {
      method: 'GET',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    });
    return handleResponse(res);
  },

  /** 
   * Obtener ingresos del usuario autenticado
   * GET /ingresos/mis-ingresos
   */
  async getAll() {
    const userId = getUserIdFromToken();
    if (!userId) {
      throw new Error('No se pudo obtener el ID del usuario desde el token');
    }

    const res = await fetch(`${INGRESOS_BASE}/mis-ingresos`, {
      method: 'GET',
      headers: { ...getAuthHeader() },
      credentials: 'include'
    });

    const data = await handleResponse(res);
    return Array.isArray(data) ? data.map(i => new IngresoDTO(i)) : [];
  },

  /**
   * Filtrar ingresos por rango de fechas
   * GET /ingresos/filtrar?inicio=YYYY-MM-DD&fin=YYYY-MM-DD
   */
  async getByRango(fechaInicio, fechaFin) {
    const userId = getUserIdFromToken();
    if (!userId) {
      throw new Error('No se pudo obtener el ID del usuario desde el token');
    }

    const res = await fetch(
      `${INGRESOS_BASE}/filtrar?inicio=${fechaInicio}&fin=${fechaFin}`,
      {
        method: 'GET',
        headers: { ...getAuthHeader() },
        credentials: 'include'
      }
    );

    const data = await handleResponse(res);
    return Array.isArray(data) ? data.map(i => new IngresoDTO(i)) : [];
  }
};

export default IngresoProvider;