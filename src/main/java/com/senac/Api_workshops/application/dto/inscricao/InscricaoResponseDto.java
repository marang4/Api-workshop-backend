package com.senac.Api_workshops.application.dto.inscricao;

import com.senac.Api_workshops.domain.entity.Inscricao;

import java.time.LocalDateTime;

public record InscricaoResponseDto(
        Long id,
        String nomeUsuario,
        String temaWorkshop,
        LocalDateTime dataInscricao
) {

    public InscricaoResponseDto(Inscricao inscricao){
        this (
                inscricao.getId(),
                inscricao.getUsuario().getNome(),
                inscricao.getWorkshop().getTema(),
                inscricao.getDataInscricao()
        );
    }


}
