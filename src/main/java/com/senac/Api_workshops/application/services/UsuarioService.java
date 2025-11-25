package com.senac.Api_workshops.application.services;

import com.senac.Api_workshops.application.dto.login.EsqueciMinhaSenhaDto;
import com.senac.Api_workshops.application.dto.login.LoginRequestDto;
import com.senac.Api_workshops.application.dto.usuario.RegistroNovaSenhaDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
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
    private PasswordEncoder passwordEncoder; // <--- INJETE ISSO

    // 1. AJUSTE NO LOGIN (Validar Senha)
    public boolean validarSenha(LoginRequestDto loginRequestDto) {
        // Busca o usuário pelo email
        var usuario = usuarioRepository.findByEmail(loginRequestDto.email()).orElse(null);

        if (usuario != null) {
            // Compara a senha que chegou (texto puro) com o hash do banco
            return passwordEncoder.matches(loginRequestDto.senha(), usuario.getSenha());
        }

        return false;
    }

    @Transactional
    public UsuarioResponseDto salvarUsuarioPeloAdmin(UsuarioRequestDto dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = new Usuario(dto);

        // CRIPTOGRAFA A SENHA ANTES DE SALVAR
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        // ... (lógica das roles que já fizemos) ...
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
    public UsuarioResponseDto consultarPorId(Long id) {
        return usuarioRepository.findById(id).map(UsuarioResponseDto::new).orElse(null);
    }

    public UsuarioResponseDto buscarPorEmail(String email) {
        // 1. Busca a entidade no banco (aqui o Service pode chamar o Repository)
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Converte para DTO e retorna
        return new UsuarioResponseDto(usuario);
    }
    public List<UsuarioResponseDto> consultarTodosSemFiltro() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDto::new)
                .collect((Collectors.toList()));
    }


    @Transactional
    public UsuarioResponseDto salvarUsuario(UsuarioRequestDto usuarioRequest) {
        // ... (lógica de busca por CPF existente) ...
        var usuario = new Usuario(usuarioRequest); // Simplificando para o exemplo

        // CRIPTOGRAFA A SENHA
        usuario.setSenha(passwordEncoder.encode(usuarioRequest.senha()));

        // Define Role
        usuario.setRole("ROLE_USER");

        usuarioRepository.save(usuario);
        return usuario.toDtoResponse();
    }

    public List<UsuarioResponseDto> consultarPaginaFiltrada(Long take, Long page, String filtro) {

        return usuarioRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Usuario::getId).reversed())//ordenar por data
                .filter(p -> p.getDataCadastro().isAfter(LocalDateTime.now().plusDays(-7)))
                .filter( a -> filtro !=null ? a.getNome().contains(filtro) : true)
                .skip((long)page * take)
                .limit(take)
                .map(UsuarioResponseDto::new)
                .collect(Collectors.toList());
    }

    public void recuperarSenhaEnvio(UsuarioPrincipalDTO usuarioLogado) {
        iEnvioEmail.enviarEmailSimples(usuarioLogado.email(),
                "codigo de recuperação",
                "123456"
        );
    }

    public String gerarCodigoAleatorio(int length) {
        final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARS.length());
            senha.append(CHARS.charAt(randomIndex));
        }
        return senha.toString();
    }

    public void esqueciMinhaSenha(EsqueciMinhaSenhaDto esqueciMinhaSenhaDto) {
        var usuario = usuarioRepository.findByEmail(esqueciMinhaSenhaDto.email()).orElse(null);

        if (usuario != null) {
            var codigo = gerarCodigoAleatorio(8);
            usuario.setTokenSenha(codigo);
            usuarioRepository.save(usuario);


            String linkReset = "http://localhost:5173/resetar-senha?token=" + codigo + "&email=" + usuario.getEmail();


            String mensagemHtml = "<p>Recebemos uma solicitação para redefinir sua senha.</p>" +
                    "<p>Seu código de verificação é: <strong style='font-size: 18px; color: #0056b3;'>" + codigo + "</strong></p>" +
                    "<p>Ou clique no botão abaixo para redefinir diretamente:</p>" +
                    "<br>" +
                    "<a href='" + linkReset + "' style='display: inline-block; padding: 10px 20px; font-size: 16px; font-weight: bold; color: #ffffff; background-color: #0056b3; text-decoration: none; border-radius: 5px;'>Redefinir Senha</a>";


            iEnvioEmail.enviarEmailComTemplate(
                    esqueciMinhaSenhaDto.email(),
                    "Redefinição de Senha - WorkshopsDev",
                    mensagemHtml
            );
        }
    }


    public void registrarNovaSenha(RegistroNovaSenhaDto registroNovaSenhaDto) {
        var usuario = usuarioRepository.findByEmailAndTokenSenha(
                        registroNovaSenhaDto.email(),
                        registroNovaSenhaDto.token())
                .orElse(null);

        if(usuario != null) {

            usuario.setSenha(passwordEncoder.encode(registroNovaSenhaDto.senha()));

            usuario.setTokenSenha(null);

            usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Token inválido ou expirado.");
        }
    }

    public void excluirUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }


}
