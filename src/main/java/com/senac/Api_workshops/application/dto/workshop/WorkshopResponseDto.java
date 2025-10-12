package com.senac.Api_workshops.application.dto.workshop;

import java.time.LocalDate;

public record WorkshopResponseDto(Long id, String tema,
                                  LocalDate data,
                                  Integer vagasTotais,
                                  Integer vagasOcupadas) {
}
