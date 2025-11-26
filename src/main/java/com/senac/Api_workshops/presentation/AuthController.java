package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.login.EsqueciMinhaSenhaDto;
import com.senac.Api_workshops.application.dto.login.LoginRequestDto;
import com.senac.Api_workshops.application.dto.login.LoginResponseDto;
import com.senac.Api_workshops.application.dto.usuario.AlterarSenhaDto;
import com.senac.Api_workshops.application.dto.usuario.RegistroNovaSenhaDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
import com.senac.Api_workshops.application.services.TokenService;
import com.senac.Api_workshops.application.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name= "Autenticação", description = "Endpoints para login e recuperação de senha")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Realizar Login", description = "Fazer Login no sistema.")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        if (!usuarioService.validarSenha(request)) {
            return ResponseEntity.badRequest().build();
        }

        var token = tokenService.gerarToken(request);
        var usuarioDto = usuarioService.buscarPorEmail(request.email());

        return ResponseEntity.ok(new LoginResponseDto(token, usuarioDto));
    }

    @PostMapping("/esqueciminhasenha")
    @Operation(summary = "Solicitar Recuperação", description = "Envia e-mail com link para redefinição de senha.")
    public ResponseEntity<String> esqueciMinhaSenha(@RequestBody EsqueciMinhaSenhaDto dto){
        try {
            usuarioService.esqueciMinhaSenha(dto);
            return ResponseEntity.ok("Se o e-mail existir, o link foi enviado.");
        }catch (Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registrarnovasenha")
    @Operation(summary = "Redefinir Senha", description = "Salva a nova senha utilizando o token recebido por e-mail.")
    public ResponseEntity<Void> registroNovaSenha(@RequestBody RegistroNovaSenhaDto dto){
        try {
            usuarioService.registrarNovaSenha(dto);
            return ResponseEntity.ok().build();
        }catch (Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/alterarsenha")
    @Operation(summary = "Alterar Senha (Logado)", description = "alterar senha do usuario")
    public ResponseEntity<Void> alterarSenha(
            @RequestBody AlterarSenhaDto dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado
    ) {
        try {
            usuarioService.alterarSenha(usuarioLogado.id(), dto.senhaAtual(), dto.novaSenha());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}