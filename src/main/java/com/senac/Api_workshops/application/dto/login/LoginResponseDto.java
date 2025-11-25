package com.senac.Api_workshops.application.dto.login;

import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;

public record LoginResponseDto(String token, UsuarioResponseDto usuario) {
}
