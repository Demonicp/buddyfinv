// src/models/UsuarioResponseDTO.js

export default class UsuarioResponseDTO {
  constructor({ id = null, nombre = '', usuario = '', email = '', nitUsuario = '' } = {}) {
    this.id = id
    this.nombre = nombre
    this.usuario = usuario
    this.email = email
    this.nitUsuario = nitUsuario
  }

  // Mapea desde el backend (UsuarioResponseDTO)
  static fromResponse(dto) {
    return new UsuarioResponseDTO({
      id: dto.id,
      nombre: dto.nombre,
      usuario: dto.usuario,
      email: dto.email,
      nitUsuario: dto.nitUsuario || ''
    })
  }
}