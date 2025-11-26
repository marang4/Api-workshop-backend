package com.senac.Api_workshops.application.dto.workshop;


import java.time.LocalDate;

public record WorkshopRequestDto(
        Long id,
        String tema,
        String descricao, 
        LocalDate data,
        Integer vagasTotais,
        Integer vagasOcupadas,
        String local
) {}
