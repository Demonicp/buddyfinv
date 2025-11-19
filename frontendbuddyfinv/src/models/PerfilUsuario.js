export default class PerfilUsuario {
    constructor({ usuario = '', nombre = '', email = '', rol = '' } = {}) {
      this.usuario = usuario;
      this.nombre = nombre;
      this.email = email;
      this.rol = rol;
    }
  }