import { defineStore } from 'pinia'

export const useUsuarioStore = defineStore('usuario', {
  state: () => ({
    rol: null,
    id: null
  }),
  actions: {
    establecerDatosDesdeToken(token) {
      try {
        const payloadBase64 = token.split('.')[1]
        const payload = JSON.parse(atob(payloadBase64))
        this.rol = payload.rol || null
        this.id = payload.id || null
      } catch (error) {
        console.error('Error al decodificar el token:', error)
        this.rol = null
        this.id = null
      }
    }
  }
})