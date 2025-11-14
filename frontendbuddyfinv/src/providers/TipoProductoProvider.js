const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export async function obtenerTiposProducto() {
  const res = await fetch(`${API_BASE_URL}/tipo-producto/mis-tipos`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('token')}`
    },
    credentials: 'include'
  })
  return await res.json()
}