package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
import com.senac.Api_workshops.application.dto.usuario.UsuarioRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;
import com.senac.Api_workshops.application.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Controlador de usuarios", description = "Gerenciamento de usuários do sistema")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna os dados de um usuário específico.")
    public ResponseEntity<UsuarioResponseDto> consultaPorId(@PathVariable Long id) {
        var usuario = usuarioService.consultarPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/grid")
    @Operation(summary = "Listar Grid", description = "Consulta paginada e filtrada de usuários.")
    public ResponseEntity<List<UsuarioResponseDto>> consultarPaginaFiltrado(
            @Parameter(description = "Registros por pagina") @RequestParam Long take,
            @Parameter(description = "Número da pagina") @RequestParam Long page,
            @Parameter(description = "Filtro por nome") @RequestParam String filtro){
        return ResponseEntity.ok(usuarioService.consultarPaginaFiltrada(take, page, filtro));
    }

    @PostMapping
    @Operation(summary = "Cadastro Público", description = "usuario se cadastra com a role USER.")
    public ResponseEntity<UsuarioResponseDto> salvarUsuario(@RequestBody UsuarioRequestDto usuario) {
        try {
            var usuarioResponse = usuarioService.salvarUsuario(usuario);
            return ResponseEntity.ok(usuarioResponse);
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/admin/criar")
    @Operation(summary = "Criar Usuário (Admin)", description = "para o admin fazer cadastro de organizadores e tambem user.")
    public ResponseEntity<UsuarioResponseDto> criarUsuarioPeloAdmin(
            @RequestBody UsuarioRequestDto dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado
    ) {
        if (!usuarioLogado.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            var response = usuarioService.salvarUsuarioPeloAdmin(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar Todos", description = "lista toso os usuarios do sistema")
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos(@AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        if (!usuarioLogado.isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(usuarioService.consultarTodosSemFiltro());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "exclui usuarios do sistema")
    public ResponseEntity<Void> excluirUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {

        if (!usuarioLogado.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (usuarioLogado.id().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            usuarioService.excluirUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}