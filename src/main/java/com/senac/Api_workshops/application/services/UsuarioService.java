package com.senac.Api_workshops.application.services;

import com.senac.Api_workshops.application.dto.login.EsqueciMinhaSenhaDto;
import com.senac.Api_workshops.application.dto.login.LoginRequestDto;
import com.senac.Api_workshops.application.dto.usuario.RegistroNovaSenhaDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;
import com.senac.Api_workshops.domain.interfaces.IEnvioEmail;
import com.senac.Api_workshops.domain.entity.Usuario;
import com.senac.Api_workshops.domain.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private IEnvioEmail iEnvioEmail;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean validarSenha(LoginRequestDto loginRequestDto) {
        var usuario = usuarioRepository.findByEmail(loginRequestDto.email()).orElse(null);
        if (usuario != null) {
            return passwordEncoder.matches(loginRequestDto.senha(), usuario.getSenha());
        }
        return false;
    }

    public UsuarioResponseDto buscarPorEmail(String email) {
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UsuarioResponseDto(usuario);
    }



    @Transactional
    public UsuarioResponseDto salvarUsuarioPeloAdmin(UsuarioRequestDto dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        Usuario usuario = new Usuario(dto);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        String roleRecebida = dto.role().toUpperCase();
        if (!roleRecebida.startsWith("ROLE_")) {
            usuario.setRole("ROLE_" + roleRecebida);
        } else {
            usuario.setRole(roleRecebida);
        }
        usuarioRepository.save(usuario);
        return new UsuarioResponseDto(usuario);
    }

    @Transactional
    public UsuarioResponseDto salvarUsuario(UsuarioRequestDto usuarioRequest) {

        if (usuarioRepository.findByEmail(usuarioRequest.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        var usuario = new Usuario(usuarioRequest);
        usuario.setSenha(passwordEncoder.encode(usuarioRequest.senha()));
        if (usuarioRepository.count() == 0) {
            usuario.setRole("ROLE_ADMIN");
        } else {

            usuario.setRole("ROLE_USER");
        }

        usuarioRepository.save(usuario);
        return usuario.toDtoResponse();

    }

    @Transactional
    public void alterarSenha(Long idUsuario, String senhaAtual, String novaSenha) {
        var usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new RuntimeException("A senha atual está incorreta.");
        }
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    public void excluirUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }



    public void esqueciMinhaSenha(EsqueciMinhaSenhaDto dto) {
        var usuario = usuarioRepository.findByEmail(dto.email()).orElse(null);
        if (usuario != null) {
            var codigo = gerarCodigoAleatorio(20);
            usuario.setTokenSenha(codigo);
            usuarioRepository.save(usuario);

            String linkReset = "http://localhost:5173/resetar-senha?token=" + codigo;
            String mensagemHtml = "<p>Seu link de redefinição:</p><a href='" + linkReset + "'>Clique aqui</a>";

            iEnvioEmail.enviarEmailComTemplate(dto.email(), "Recuperação de Senha", mensagemHtml);
        }
    }

    public void registrarNovaSenha(RegistroNovaSenhaDto dto) {
        var usuario = usuarioRepository.findByTokenSenha(dto.token())
                .orElseThrow(() -> new RuntimeException("Link inválido ou expirado."));
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setTokenSenha(null);
        usuarioRepository.save(usuario);
    }

    private String gerarCodigoAleatorio(int length) {
        final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            senha.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return senha.toString();
    }


    @Transactional
    public UsuarioResponseDto consultarPorId(Long id) {
        return usuarioRepository.findById(id).map(UsuarioResponseDto::new).orElse(null);
    }

    public List<UsuarioResponseDto> consultarTodosSemFiltro() {
        return usuarioRepository.findAll().stream().map(UsuarioResponseDto::new).collect(Collectors.toList());
    }

    public List<UsuarioResponseDto> consultarPaginaFiltrada(Long take, Long page, String filtro) {
        return usuarioRepository.findAll().stream()
                .sorted(Comparator.comparing(Usuario::getId).reversed())
                .filter(p -> p.getDataCadastro().isAfter(LocalDateTime.now().plusDays(-7)))
                .filter(a -> filtro != null ? a.getNome().contains(filtro) : true)
                .skip((long) page * take)
                .limit(take)
                .map(UsuarioResponseDto::new)
                .collect(Collectors.toList());
    }
}