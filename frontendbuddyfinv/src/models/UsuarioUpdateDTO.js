// src/models/UsuarioUpdateModel.js

export default class UsuarioUpdateDTO {
  constructor({ nombre = '', usuario = '', email = '', password = '' } = {}) {
    this.nombre = nombre
    this.usuario = usuario
    this.email = email
    this.password = password // opcional, solo si se quiere cambiar
  }

  // Prepara el objeto para enviarlo al backend (UsuarioEdicionDTO)
  toRequest() {
    const req = {
      nombre: this.nombre,
      usuario: this.usuario,
      email: this.email
    }
    if (this.password && this.password.trim() !== '') {
      req.password = this.password
    }
    return req
  }
}