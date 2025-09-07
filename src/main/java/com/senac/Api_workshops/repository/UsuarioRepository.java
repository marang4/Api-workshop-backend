package com.senac.Api_workshops.repository;

import com.senac.Api_workshops.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
