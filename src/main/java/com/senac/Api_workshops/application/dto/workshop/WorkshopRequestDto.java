package com.senac.Api_workshops.application.dto.workshop;

import com.senac.Api_workshops.domain.entity.Workshop;

import java.time.LocalDate;

public record WorkshopRequestDto(Long id, String tema,
                                 LocalDate data,
                                 Integer vagasTotais,
                                 Integer vagasOcupadas,
                                 String local) {

}
