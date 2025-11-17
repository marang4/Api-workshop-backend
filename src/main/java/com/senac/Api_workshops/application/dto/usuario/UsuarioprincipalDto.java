package com.senac.Api_workshops.application.dto.usuario;

import com.senac.Api_workshops.domain.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record UsuarioprincipalDto(Long id, String email, Collection<? extends GrantedAuthority> autorizacao) {

    public UsuarioprincipalDto(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getAuthorities()
        );
    }

    
}
