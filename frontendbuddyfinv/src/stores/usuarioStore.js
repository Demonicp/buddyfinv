import { defineStore } from 'pinia'
import { useRouter } from 'vue-router'

export const useUsuarioStore = defineStore('usuario', {
  state: () => ({
    rol: null,
    id: null,
    token: localStorage.getItem('token') || null
  }),

  actions: {
    establecerDatosDesdeToken(token) {
      try {
        const payloadBase64 = token.split('.')[1]
        const payload = JSON.parse(atob(payloadBase64))
        this.rol = payload.rol || null
        this.id = payload.idUsuario || null
        this.token = token
        localStorage.setItem('token', token)
      } catch (error) {
        console.error('Error al decodificar el token:', error)
        this.rol = null
        this.id = null
        this.token = null
        localStorage.removeItem('token')
      }
    },

    async cerrarSesion() {


      try {
        await fetch(`${import.meta.env.VITE_API_BASE_URL}/auth/logout`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
      } catch (error) {
        console.error('Error al cerrar sesión:', error.message)
      } finally {
        this.rol = null
        this.id = null
        this.token = null
        localStorage.removeItem('token')

      }
    }
  }
})