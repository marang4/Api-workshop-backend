package com.senac.Api_workshops.application.services;

import com.senac.Api_workshops.application.dto.workshop.WorkshopRequestDto;
import com.senac.Api_workshops.application.dto.workshop.WorkshopResponseDto;
import com.senac.Api_workshops.domain.entity.Workshop;
import com.senac.Api_workshops.domain.repository.WorkshopRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;


    public List<WorkshopResponseDto> listarTodos() {

        return workshopRepository.findAll()
                .stream()
                .map(WorkshopResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkshopResponseDto salvarWorkshop(WorkshopRequestDto requestDto) {

        Workshop workshop;

        if (requestDto.id() != null) {

            workshop = workshopRepository.findById(requestDto.id())
                    .map(w -> {
                        w.setTema(requestDto.tema());
                        w.setData(requestDto.data());
                        w.setLocal(requestDto.local());
                        w.setVagasTotais(requestDto.vagasTotais());
                        w.setVagasOcupadas(requestDto.vagasOcupadas());
                        return w;
                    })
                    .orElse(new Workshop(requestDto));
        } else {

            workshop = new Workshop(requestDto);
        }

        workshopRepository.save(workshop);

        return new WorkshopResponseDto(workshop);
    }

    public boolean excluirWorkshop(Long id) {
        if (!workshopRepository.existsById(id)) {
            return false;
        }
        workshopRepository.deleteById(id);
        return true;
    }



    @Transactional
    public WorkshopResponseDto editarWorkshop(Long id, WorkshopRequestDto requestDto) {

        var workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workshop não encontrado"));

        workshop.setTema(requestDto.tema());
        workshop.setData(requestDto.data());
        workshop.setLocal(requestDto.local());
        workshop.setVagasTotais(requestDto.vagasTotais());
        workshop.setVagasOcupadas(requestDto.vagasOcupadas());

        workshopRepository.save(workshop);

        return new WorkshopResponseDto(workshop);
    }



}
