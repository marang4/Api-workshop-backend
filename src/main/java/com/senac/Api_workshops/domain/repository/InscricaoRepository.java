package com.senac.Api_workshops.domain.repository;

import com.senac.Api_workshops.domain.entity.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
    boolean existsByUsuarioIdAndWorkshopId(Long usuarioId, Long workshopId);




    Optional<Inscricao> findByUsuarioIdAndWorkshopId(Long usuarioId, Long workshopId);
}
