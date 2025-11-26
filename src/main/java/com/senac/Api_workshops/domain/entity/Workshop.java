package com.senac.Api_workshops.domain.entity;

import com.senac.Api_workshops.application.dto.workshop.WorkshopRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workshop {

    public Workshop(WorkshopRequestDto WorkshopRequestDto) {
        this.setTema(WorkshopRequestDto.tema());
        this.setData(WorkshopRequestDto.data());
        this.setLocal(WorkshopRequestDto.local());
        this.setVagasTotais(WorkshopRequestDto.vagasTotais());
        this.setVagasOcupadas(WorkshopRequestDto.vagasOcupadas());
    }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tema;
    private LocalDate data;
    private Integer vagasTotais;
    private Integer vagasOcupadas;
    private String local;

    @Column(columnDefinition = "TEXT") // Permite textos longos
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "criador_id")
    private Usuario criador;


}
