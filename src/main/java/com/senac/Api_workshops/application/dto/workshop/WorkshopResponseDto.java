package com.senac.Api_workshops.application.dto.workshop;

import com.senac.Api_workshops.domain.entity.Workshop;

import java.time.LocalDate;

public record WorkshopResponseDto( Long id,
                                   String tema,
                                  String descricao,
                                  LocalDate data,
                                  Integer vagasTotais,
                                  Integer vagasOcupadas,
                                  String local) {


    public WorkshopResponseDto(Workshop workshop) {
        this(
                workshop.getId(),
                workshop.getTema(),
                workshop.getDescricao(),
                workshop.getData(),
                workshop.getVagasTotais(),
                workshop.getVagasOcupadas(),
                workshop.getLocal()
        );
    }




}
