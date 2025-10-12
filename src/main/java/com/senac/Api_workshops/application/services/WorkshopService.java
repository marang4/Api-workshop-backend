package com.senac.Api_workshops.application.services;

import com.senac.Api_workshops.application.dto.workshop.WorkshopRequestDto;
import com.senac.Api_workshops.domain.model.Workshop;
import com.senac.Api_workshops.domain.repository.WorkshopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;


    public List<Workshop> listarTodos() {
        return workshopRepository.findAll();
    }


    public Workshop salvarWorkshop(WorkshopRequestDto requestDto) {
        Workshop workshopEntity = requestDto.transformarEmEntidade();
        return workshopRepository.save(workshopEntity);
    }

    public boolean excluirWorkshop(Long id) {
        if (!workshopRepository.existsById(id)) {
            return false;
        }
        workshopRepository.deleteById(id);
        return true;
    }



    public Optional<Workshop> editarWorkshop(Long id, WorkshopRequestDto requestDto) {
        Optional<Workshop> optionalWorkshop = workshopRepository.findById(id);

        if (optionalWorkshop.isPresent()) {
            Workshop workshop = optionalWorkshop.get();
            workshop.setTema(requestDto.tema());
            workshop.setData(requestDto.data());
            workshop.setVagasTotais(requestDto.vagasTotais());
            workshop.setVagasOcupadas(requestDto.vagasOcupadas());
            Workshop updatedWorkshop = workshopRepository.save(workshop);
            return Optional.of(updatedWorkshop);
        } else {
            return Optional.empty();
        }
    }



}
