package com.es.backendbuddyfinv.dto;

import com.es.backendbuddyfinv.model.Usuario;

import lombok.Data;
@Data

//CREADO POR ESTEBAN MORENO COMO MEDIDA PARA SOLUCIONA ERROR DE JSON Y ANIDADO 

public class UsuarioResponseDTO {
    
    private Long id;
    private String nombre;
    private String usuario;
    private String email;
  // getters y setters by lombok

  public static UsuarioResponseDTO fromEntity(Usuario u) {
    UsuarioResponseDTO dto = new UsuarioResponseDTO();
    dto.setId(u.getId());
    dto.setNombre(u.getNombre());
    dto.setUsuario(u.getUsuario());
    dto.setEmail(u.getEmail());
    return dto;
  }

}
