package com.senac.Api_workshops.application.services;

import com.senac.Api_workshops.application.dto.inscricao.InscricaoRequestDto;
import com.senac.Api_workshops.application.dto.inscricao.InscricaoResponseDto;
import com.senac.Api_workshops.domain.entity.Inscricao;
import com.senac.Api_workshops.domain.repository.InscricaoRepository;
import com.senac.Api_workshops.domain.repository.UsuarioRepository;
import com.senac.Api_workshops.domain.repository.WorkshopRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscricaoService {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private WorkshopRepository workshopRepository;



    @Transactional
    public InscricaoResponseDto realizarInscricao(InscricaoRequestDto request) {
        var usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var workshop = workshopRepository.findById(request.workshopId())
                .orElseThrow(() -> new RuntimeException("Workshop não encontrado"));

        String role = usuario.getRole().toUpperCase();
        if (role.contains("ORGANIZADOR") || role.contains("ADMIN")) {
            throw new RuntimeException("Organizadores e Admins não podem se inscrever em workshops.");
        }

        // 3. Verificar Duplicidade
        if (inscricaoRepository.existsByUsuarioIdAndWorkshopId(usuario.getId(), workshop.getId())) {
            throw new RuntimeException("Usuário já está inscrito neste workshop.");
        }

        // 4. Verificar Vagas
        if (workshop.getVagasOcupadas() >= workshop.getVagasTotais()) {
            throw new RuntimeException("Não há vagas disponíveis para este workshop.");
        }

        // 5. Atualizar Vagas Ocupadas
        workshop.setVagasOcupadas(workshop.getVagasOcupadas() + 1);
        workshopRepository.save(workshop); // Salva a nova contagem de vagas

        // 6. Criar e Salvar Inscrição
        Inscricao novaInscricao = new Inscricao(usuario, workshop);
        inscricaoRepository.save(novaInscricao);

        return new InscricaoResponseDto(novaInscricao);
    }

    @Transactional
    public void cancelarInscricao(Long idUsuario, Long idWorkshop) {

        var inscricao = inscricaoRepository.findByUsuarioIdAndWorkshopId(idUsuario, idWorkshop)
                .orElseThrow(() -> new RuntimeException("Você não está inscrito neste workshop."));

        var workshop = inscricao.getWorkshop();


        inscricaoRepository.delete(inscricao);


        if (workshop.getVagasOcupadas() > 0) {
            workshop.setVagasOcupadas(workshop.getVagasOcupadas() - 1);
            workshopRepository.save(workshop);
        }
    }


    public boolean isInscrito(Long idUsuario, Long idWorkshop) {
        return inscricaoRepository.existsByUsuarioIdAndWorkshopId(idUsuario, idWorkshop);
    }
}
