package com.senac.Api_workshops.dto;

import com.senac.Api_workshops.model.Workshop;

import java.time.LocalDate;

public record WorkshopRequestDto(Long id, String tema,
                                 LocalDate data,
                                 Integer vagasTotais,
                                 Integer vagasOcupadas) {


    public Workshop transformarEmEntidade() {
        Workshop workshop = new Workshop();
        workshop.setTema(tema);
        workshop.setData(data);
        workshop.setVagasTotais(vagasTotais);
        workshop.setVagasOcupadas(vagasOcupadas);

        return workshop;
    }



}
