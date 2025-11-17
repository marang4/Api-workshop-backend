package com.senac.Api_workshops.application.services;

import com.senac.Api_workshops.application.dto.login.EsqueciMinhaSenhaDto;
import com.senac.Api_workshops.application.dto.login.LoginRequestDto;
import com.senac.Api_workshops.application.dto.usuario.RegistroNovaSenhaDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioprincipalDto;
import com.senac.Api_workshops.domain.interfaces.IEnvioEmail;
import com.senac.Api_workshops.domain.entity.Usuario;
import com.senac.Api_workshops.domain.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    public boolean validarSenha(LoginRequestDto loginRequestDto) {
        return usuarioRepository.existsUsuarioByEmailContainingAndSenha(loginRequestDto.email(), loginRequestDto.senha());
    }


    @Transactional
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
                .stream()
                .sorted(Comparator.comparing(Usuario::getId).reversed())//ordenar por data
                .filter(p -> p.getDataCadastro().isAfter(LocalDateTime.now().plusDays(-7)))
                .filter( a -> filtro !=null ? a.getNome().contains(filtro) : true)
                .skip((long)page * take)
                .limit(take)
                .map(UsuarioResponseDto::new)
                .collect(Collectors.toList());
    }

    public void recuperarSenhaEnvio(UsuarioprincipalDto usuarioLogado) {
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

            iEnvioEmail.enviarEmailComTemplate(esqueciMinhaSenhaDto.email(),
                    "codigo recuperacao",
                    codigo);
        }
    }


    public void registrarNovaSenha(RegistroNovaSenhaDto registroNovaSenhaDto) {
        var usuario = usuarioRepository.findByEmailAndTokenSenha(
                        registroNovaSenhaDto.email(),
                        registroNovaSenhaDto.token())
                .orElse(null);

        if(usuario != null) {
            usuario.setSenha(registroNovaSenhaDto.senha());
            usuarioRepository.save(usuario);
        }
    }


}
