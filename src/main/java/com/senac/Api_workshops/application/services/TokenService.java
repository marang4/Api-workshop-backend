package com.senac.Api_workshops.application.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.senac.Api_workshops.application.dto.login.LoginRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
import com.senac.Api_workshops.domain.entity.Token;
import com.senac.Api_workshops.domain.entity.Usuario;
import com.senac.Api_workshops.domain.repository.TokenRepository;
import com.senac.Api_workshops.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${spring.secretkey}")
    private String secret;

    @Value("${spring.tempo_expiracao}")
    private Long tempo;

    private String emissor = "DEVTESTE";

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public String gerarToken(LoginRequestDto loginRequestDto) {
        var usuario = usuarioRepository.findByEmail(loginRequestDto.email()).orElse(null);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withIssuer(emissor)
                .withSubject(usuario.getEmail())
                .withExpiresAt(this.gerarDataExpiracao())
                .sign(algorithm);
        tokenRepository.save(new Token(null, token,usuario));
        return token;

    }


    public UsuarioPrincipalDTO validarToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(emissor)
                .build();
        verifier.verify(token);

        var tokenResult = tokenRepository.findByToken(token).orElse(null);

        if (tokenResult == null) {
            throw new IllegalArgumentException("token invalido");

        }

        return new UsuarioPrincipalDTO(tokenResult.getUsuario());
    }



    private Instant gerarDataExpiracao() {
        var dataAtual = LocalDateTime.now();
        dataAtual = dataAtual.plusMinutes(tempo);
        return dataAtual.toInstant(ZoneOffset.of("-03:00"));

    }

}
