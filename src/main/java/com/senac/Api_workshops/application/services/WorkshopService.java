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

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<WorkshopResponseDto> listarParaVitrine(String filtro) {
        return workshopRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Workshop::getData).reversed())
                .filter(w -> filtro == null || filtro.isBlank() || w.getTema().toLowerCase().contains(filtro.toLowerCase()))
                .map(WorkshopResponseDto::new)
                .collect(Collectors.toList());
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

        return workshops.stream()
                .map(WorkshopResponseDto::new)
                .collect(Collectors.toList());
    }


    @Transactional
    public WorkshopResponseDto salvarWorkshop(WorkshopRequestDto requestDto, UsuarioPrincipalDTO usuarioLogado) {

        if (!usuarioLogado.isAdmin() && !usuarioLogado.isOrganizador()) {
            throw new RuntimeException("Acesso negado: Você não tem permissão para criar workshops.");
        }
        if (requestDto.vagasTotais() <= 0) {
            throw new IllegalArgumentException("O número de vagas totais deve ser maior que zero.");
        }
        if (requestDto.data().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data do workshop não pode ser no passado.");
        }

        var criador = usuarioRepository.findById(usuarioLogado.id()).orElseThrow();
        Workshop workshop = new Workshop(requestDto);
        workshop.setCriador(criador);
        workshop.setDescricao(requestDto.descricao());

        workshopRepository.save(workshop);
        return new WorkshopResponseDto(workshop);
    }


    @Transactional
    public WorkshopResponseDto editarWorkshop(Long id, WorkshopRequestDto requestDto, UsuarioPrincipalDTO usuarioLogado) {
        var workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workshop não encontrado"));




        if (usuarioLogado.isOrganizador() && !usuarioLogado.isAdmin()) {

            if (!workshop.getCriador().getId().equals(usuarioLogado.id())) {
                throw new RuntimeException("Organizadores só podem editar seus próprios workshops.");
            }
        }

        else if (!usuarioLogado.isAdmin()) {
            throw new RuntimeException("Acesso negado.");
        }




        if (requestDto.vagasTotais() <= 0) throw new IllegalArgumentException("Vagas devem ser > 0");
        if (requestDto.data().isBefore(LocalDate.now())) throw new IllegalArgumentException("Data inválida");

        workshop.setTema(requestDto.tema());
        workshop.setDescricao(requestDto.descricao());
        workshop.setData(requestDto.data());
        workshop.setLocal(requestDto.local());
        workshop.setVagasTotais(requestDto.vagasTotais());
        workshop.setVagasOcupadas(requestDto.vagasOcupadas());

        workshopRepository.save(workshop);
        return new WorkshopResponseDto(workshop);
    }


    public void excluirWorkshop(Long id, UsuarioPrincipalDTO usuarioLogado) {
        var workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workshop não encontrado"));


        if (usuarioLogado.isOrganizador() && !usuarioLogado.isAdmin()) {
            if (!workshop.getCriador().getId().equals(usuarioLogado.id())) {
                throw new SecurityException("Organizadores só podem excluir seus próprios workshops.");
            }
        } else if (!usuarioLogado.isAdmin()) {
            throw new SecurityException("Acesso negado.");
        }

        workshopRepository.deleteById(id);
    }
}