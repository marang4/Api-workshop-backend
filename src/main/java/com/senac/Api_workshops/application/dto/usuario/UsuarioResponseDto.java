package com.senac.Api_workshops.application.dto.usuario;

import com.senac.Api_workshops.domain.model.Usuario;

public record UsuarioResponseDto(Long id,
                                 String nome,
                                 String cpf,
                                 String email) {


    public UsuarioResponseDto(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getCpf(), usuario.getEmail());
    }

}