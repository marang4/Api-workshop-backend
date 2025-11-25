package com.senac.Api_workshops.application.dto.usuario;

import com.senac.Api_workshops.domain.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public record UsuarioPrincipalDTO(
        Long id,
        String email,
        Collection<? extends GrantedAuthority> autorizacao,
        String role
) {

    public UsuarioPrincipalDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getAuthorities(),
                usuario.getRole()
        );
    }

    public boolean isAdmin() {

        return this.role != null && this.role.toUpperCase().contains("ADMIN");
    }

    public boolean isOrganizador() {
        return this.role != null && this.role.toUpperCase().contains("ORGANIZADOR");
    }


    public boolean isUser() {
        return "ROLE_USER".equalsIgnoreCase(this.role);
    }
}