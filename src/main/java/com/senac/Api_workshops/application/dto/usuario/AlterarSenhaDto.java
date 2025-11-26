package com.senac.Api_workshops.application.dto.usuario;

public record AlterarSenhaDto(
        String senhaAtual,
        String novaSenha
) {
}
