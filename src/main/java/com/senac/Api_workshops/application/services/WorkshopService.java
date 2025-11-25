package com.senac.Api_workshops.application.services;

import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
import com.senac.Api_workshops.application.dto.workshop.WorkshopRequestDto;
import com.senac.Api_workshops.application.dto.workshop.WorkshopResponseDto;
import com.senac.Api_workshops.domain.entity.Workshop;
import com.senac.Api_workshops.domain.repository.UsuarioRepository;
import com.senac.Api_workshops.domain.repository.WorkshopRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<WorkshopResponseDto> listarParaVitrine() {
        return workshopRepository.findAll().stream().map(WorkshopResponseDto::new).collect(Collectors.toList());
    }

    public List<WorkshopResponseDto> listarParaGerenciamento(UsuarioPrincipalDTO usuarioLogado) {
        List<Workshop> workshops;
        if (usuarioLogado.isAdmin()) {
            workshops = workshopRepository.findAll();
        } else if (usuarioLogado.isOrganizador()) {
            workshops = workshopRepository.findByCriadorId(usuarioLogado.id());
        } else {
            workshops = Collections.emptyList();
        }
        return workshops.stream().map(WorkshopResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public WorkshopResponseDto salvarWorkshop(WorkshopRequestDto requestDto, UsuarioPrincipalDTO usuarioLogado) {

        if (!usuarioLogado.isAdmin() && !usuarioLogado.isOrganizador()) {
            throw new RuntimeException("Acesso negado: Você não tem permissão para criar workshops.");
        }


        if (requestDto.vagasTotais() <= 0) {
            throw new IllegalArgumentException("O número de vagas totais deve ser maior que zero.");
        }

        var criador = usuarioRepository.findById(usuarioLogado.id())
                .orElseThrow(() -> new RuntimeException("Usuário criador não encontrado."));

        Workshop workshop = new Workshop(requestDto);
        workshop.setCriador(criador);

        workshopRepository.save(workshop);
        return new WorkshopResponseDto(workshop);
    }

    @Transactional
    public WorkshopResponseDto editarWorkshop(Long id, WorkshopRequestDto requestDto, UsuarioPrincipalDTO usuarioLogado) {
        var workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workshop não encontrado"));

        boolean eDono = workshop.getCriador().getId().equals(usuarioLogado.id());

        if (!usuarioLogado.isAdmin() && !eDono) {
            throw new RuntimeException("Você não tem permissão para editar este workshop.");
        }


        if (requestDto.vagasTotais() <= 0) {
            throw new IllegalArgumentException("O número de vagas totais deve ser maior que zero.");
        }

        workshop.setTema(requestDto.tema());
        workshop.setData(requestDto.data());
        workshop.setLocal(requestDto.local());
        workshop.setVagasTotais(requestDto.vagasTotais());
        workshop.setVagasOcupadas(requestDto.vagasOcupadas());

        workshopRepository.save(workshop);
        return new WorkshopResponseDto(workshop);
    }


    public void excluirWorkshop(Long id, UsuarioPrincipalDTO usuarioLogado) {
        var workshop = workshopRepository.findById(id).orElseThrow(() -> new RuntimeException("Workshop não encontrado"));
        boolean eDono = workshop.getCriador().getId().equals(usuarioLogado.id());

        if (!usuarioLogado.isAdmin() && !eDono) {

            throw new SecurityException("Você não tem permissão para excluir este workshop.");
        }
        workshopRepository.deleteById(id);
    }
}