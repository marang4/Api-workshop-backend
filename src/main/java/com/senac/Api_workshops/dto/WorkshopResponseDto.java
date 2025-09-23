package com.senac.Api_workshops.dto;

import java.time.LocalDate;

public record WorkshopResponseDto(Long id, String tema,
                                  LocalDate data,
                                  Integer vagasTotais,
                                  Integer vagasOcupadas) {
}
