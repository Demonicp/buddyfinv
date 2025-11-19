import { UsuarioDTO } from '../models/Usuario.js'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const USUARIOS_BASE = `${API_BASE_URL}/usuarios`


function getAuthHeader() {
  const token = localStorage.getItem('token');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
}

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

export const UsuarioProvider = {
  async getAll() {
    const res = await fetch(`${USUARIOS_BASE}/all`, {
      method: 'GET',
      credentials: 'include'
    })
    const data = await handleResponse(res)
    return Array.isArray(data) ? data.map(u => new UsuarioDTO(u)) : []
  },

  async registrar(datos) {
    try {
      const res = await fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
      });
  
      const data = await res.json();
      return {
        success: data.success ?? false,
        message: data.message ?? 'Error desconocido'
      };
  
    } catch (error) {
      return { success: false, message: 'Error de conexión con el servidor' };
    }
  },
  ////////////////Santiago montenego ruales perfil usuario inicio
  async getPerfil() {
    const res = await fetch(`${USUARIOS_BASE}/perfil`, {
      method: 'GET',
      headers: {
        ...getAuthHeader()
      },
      credentials: 'include'
    });
    return handleResponse(res);
  }
  ////////////////Santiago montenegro ruales perfil usuario fin
  
}