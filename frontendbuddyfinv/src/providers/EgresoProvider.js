const API_BASE_URL = import.meta?.env?.VITE_API_BASE_URL || 'http://localhost:8080'
const EGRESO_BASE = `${API_BASE_URL}/Egresos`

function getAuthHeader() {
  const token = localStorage.getItem('token')
  return token ? { 'Authorization': `Bearer ${token}` } : {}
}

async function handleResponse(response) {
  if (!response.ok) {
    const text = await response.text().catch(() => '')
    // Intentar parsear como JSON si es posible
    let message = text
    try {
      const json = JSON.parse(text)
      message = json.message || json.error || text
    } catch (e) {
      // Si no es JSON, usar el texto o el status
      message = text || `${response.status} ${response.statusText}`
    }
    const error = new Error(message)
    error.status = response.status
    throw error
  }
  const contentType = response.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return response.json()
  }
  return response.text()
}

export const EgresoProvider = {
  async test() {
    const res = await fetch(`${EGRESO_BASE}/test`, {
      method: 'GET',
      headers: {
        ...getAuthHeader(),
      }
    })
    return handleResponse(res)
  },

  async getAll() {
    const res = await fetch(`${EGRESO_BASE}/obtenerTegresos`, {
      method: 'GET',
      headers: {
        ...getAuthHeader(),
      }
    })
    return handleResponse(res)
  },

  async registerEgreso(data) {
    const token = localStorage.getItem('token')
    console.log('🔑 Token disponible:', token ? 'Sí' : 'No')
    console.log('📦 Datos a enviar:', data)
    
    const headers = {
      'Content-Type': 'application/json',
      ...getAuthHeader(),
    }
    console.log('📤 Headers:', headers)
    
    const res = await fetch(`${EGRESO_BASE}/agregarEgreso`, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(data)
    })
    
    console.log('📥 Respuesta status:', res.status, res.statusText)
    return handleResponse(res)
  },

  async modifyEgreso(id, data) {
    const res = await fetch(`${EGRESO_BASE}/modificar/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
      body: JSON.stringify(data)
    })
    return handleResponse(res)
  },

  async validateEgreso(id, data) {
    const res = await fetch(`${EGRESO_BASE}/valida/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader(),
      },
      body: JSON.stringify(data)
    })
    return handleResponse(res)
  },

  async eliminateEgreso(id) {
    const res = await fetch(`${EGRESO_BASE}/eliminar/${id}`, {
      method: 'DELETE',
      headers: {
        ...getAuthHeader(),
      }
    })
    return handleResponse(res)
  },

  async getAllEgresosByUsuario() {
    const res = await fetch(`${EGRESO_BASE}/propietario`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader()
      
      }
    })
    return handleResponse(res)
  },

  async costoTotalEgresosById() {
    const res = await fetch(`${EGRESO_BASE}/total`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader()
      
      }
    })
    return handleResponse(res)

  }, async filtrarByFechas(fechaInicio, fechaFin) {
    const res = await fetch(`${EGRESO_BASE}/filtrarFechas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeader()
      }
    })
    return handleResponse(res)
  }
  
  





  
  

  
}

export default EgresoProvider

