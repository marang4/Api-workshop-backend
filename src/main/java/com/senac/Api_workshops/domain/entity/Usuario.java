package com.senac.Api_workshops.domain.entity;

import com.senac.Api_workshops.application.dto.usuario.UsuarioRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="usuarios")
public class Usuario implements UserDetails {



    public Usuario(UsuarioRequestDto UsuarioRequestDto) {
        this.setCpf(UsuarioRequestDto.cpf());
        this.setNome(UsuarioRequestDto.nome());
        this.setEmail(UsuarioRequestDto.email());
        this.setSenha(UsuarioRequestDto.senha());
        this.setRole(UsuarioRequestDto.role());
        if (this.getDataCadastro()==null) {
            this.setDataCadastro(LocalDateTime.now());
        }

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private String role;

    private LocalDateTime dataCadastro;

    private String tokenSenha;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if("ROLE_ADMIN".equals(this.role)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER"));
        }else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public UsuarioResponseDto toDtoResponse() {
        return new UsuarioResponseDto(this);
    }

}
