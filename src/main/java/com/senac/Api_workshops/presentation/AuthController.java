package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.login.EsqueciMinhaSenhaDto;
import com.senac.Api_workshops.application.dto.login.LoginRequestDto;
import com.senac.Api_workshops.application.dto.login.LoginResponseDto;
import com.senac.Api_workshops.application.dto.usuario.RegistroNovaSenhaDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioprincipalDto;
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
@Tag(name= "Controller autenticação", description = "Controller responsavel pela autenticação da aplicaçao")
public class AuthController {


    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioService usuarioService;
    @PostMapping("/login")
    @Operation(summary = "Login", description = "metodo responsavel por efetuar o login do usuario")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {


        if (!usuarioService.validarSenha(request)) {
            return ResponseEntity.badRequest().body("Usuario ou senha invalido");
        }


        var token = tokenService.gerarToken(request);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @GetMapping("/recuperarsenha/envio")
    @Operation(summary = "recuperar senha", description = "metodo utilizado para recuperaçao de senha")
    public ResponseEntity<?> recuperarSenha(@AuthenticationPrincipal UsuarioprincipalDto usuarioLogado) {
        usuarioService.recuperarSenhaEnvio(usuarioLogado);
        return ResponseEntity.ok("Codigo enviado com sucesso");
    }



    @PostMapping("/esqueciminhasenha")
    @Operation(summary = "esqueci minha senha", description = "metodo para recuperar senha")
    public ResponseEntity<?> esqueciMinhaSenha(@RequestBody EsqueciMinhaSenhaDto esqueciMinhaSenha){
        try {
            usuarioService.esqueciMinhaSenha(esqueciMinhaSenha);
            return ResponseEntity.ok("Codigo enviado com sucesso");
        }catch (Exception ex){
            return  ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/registrarnovasenha")
    @Operation(summary = "Registrar nova senha", description = "metodo para alterar senha")
    public ResponseEntity<?> registroNovaSenha(@RequestBody RegistroNovaSenhaDto registroNovaSenhaDto){
        try {
            usuarioService.registrarNovaSenha(registroNovaSenhaDto);
            return ResponseEntity.ok().build();

        }catch (Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
