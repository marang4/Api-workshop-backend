package com.senac.Api_workshops.application.services;

import com.senac.Api_workshops.application.dto.login.LoginRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;
import com.senac.Api_workshops.domain.model.Usuario;
import com.senac.Api_workshops.domain.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean validarSenha(LoginRequestDto loginRequestDto) {
        return usuarioRepository.existsUsuarioByEmailContainingAndSenha(loginRequestDto.email(), loginRequestDto.senha());
    }


    public UsuarioResponseDto consultarPorId(Long id) {
        return usuarioRepository.findById(id).map(UsuarioResponseDto::new).orElse(null);
    }

    public List<UsuarioResponseDto> consultarTodosSemFiltro() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDto::new)
                .collect((Collectors.toList()));
    }


    @Transactional
    public UsuarioResponseDto salvarUsuario(UsuarioRequestDto usuarioRequest) {
        var usuario = usuarioRepository.findByCpf(usuarioRequest.cpf()).map(u -> {

                    u.setNome(usuarioRequest.nome());
                    u.setEmail(usuarioRequest.email());
                    u.setSenha(usuarioRequest.senha());
                    u.setRole(usuarioRequest.role());
                    return u;
                })
                .orElse(new Usuario(usuarioRequest));


        usuarioRepository.save(usuario);
        return usuario.toDtoResponse();

    }

    public List<UsuarioResponseDto> consultarPaginaFiltrada(Long take, Long page, String filtro) {

        return usuarioRepository.findAll()
                .stream().skip((long) page + take )
                .filter(p -> p.getDataCadastro().isAfter(LocalDateTime.now().plusDays(-7)))
                .filter(a-> filtro != null ? a.getNome().contains(filtro) : true)
                .skip((long) page + take )
                .limit((take))
                .map(UsuarioResponseDto::new)
                .collect((Collectors.toList()));
    }
}
