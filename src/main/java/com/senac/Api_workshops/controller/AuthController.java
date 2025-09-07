package com.senac.Api_workshops.controller;

import com.senac.Api_workshops.dto.LoginRequestDto;
import com.senac.Api_workshops.services.TokenService;
import com.senac.Api_workshops.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        return ResponseEntity.ok(token);
    }


}
