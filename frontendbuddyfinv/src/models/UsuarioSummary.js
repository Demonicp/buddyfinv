export default class UsuarioSummary {
  constructor({ id = null, nombre = '', usuario = '', email = '' } = {}) {
    this.id = id;
    this.nombre = nombre;
    this.usuario = usuario;
    this.email = email;
  }

  static fromApi(obj = {}) {
    return new UsuarioSummary({
      id: obj.id,
      nombre: obj.nombre,
      usuario: obj.usuario,
      email: obj.email
    });
  }
}