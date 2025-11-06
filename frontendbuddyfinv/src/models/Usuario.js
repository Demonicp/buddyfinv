import { Rol } from './Rol';

export class UsuarioDTO{
    constructor({id, nombre, email, usuario, password, rol}) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.usuario = usuario;
        this.password = password;
        this.rol = new Rol(rol);
    }

    esAdmin() {
        return this.rol.descripcion === 'ADMIN';
    }
}

