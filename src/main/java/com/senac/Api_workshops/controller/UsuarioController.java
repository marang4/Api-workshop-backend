package com.senac.Api_workshops.controller;

import com.senac.Api_workshops.model.Usuario;
import com.senac.Api_workshops.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Controlador de usuarios", description = "camada responsavel por controlar registros de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> consultaPorId(@PathVariable Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);


    }

    @GetMapping
    @Operation(summary = "usuario", description = "Método responsavel de calcular os custos da folha de pagamento e após faz os lançamentos contabeis na tabela....") //documentar a operaçcao
    public ResponseEntity<?> consultarTodos() {


        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "Salvar usuário", description = "Método responsavel em criar usuarios")
    public ResponseEntity<Usuario> salvarUsuario(@RequestBody Usuario usuario) {
        try {
            var usuarioResponse = usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuarioResponse);
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

}
