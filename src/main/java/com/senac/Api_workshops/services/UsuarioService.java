package com.senac.Api_workshops.services;

import com.senac.Api_workshops.dto.LoginRequestDto;
import com.senac.Api_workshops.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean validarSenha(LoginRequestDto loginRequestDto) {
        return usuarioRepository.existsUsuarioByEmailContainingAndSenha(loginRequestDto.email(), loginRequestDto.senha());
    }
}
