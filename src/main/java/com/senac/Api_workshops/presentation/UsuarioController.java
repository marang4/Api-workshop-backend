package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
import com.senac.Api_workshops.application.dto.usuario.UsuarioRequestDto;
import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;
import com.senac.Api_workshops.application.services.UsuarioService;
import com.senac.Api_workshops.domain.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Controlador de usuarios", description = "camada responsavel por controlar registros de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> consultaPorId(@PathVariable Long id) {

        var usuario = usuarioService.consultarPorId(id);

        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);


    }


    @GetMapping("/grid")
    @Operation(summary = "Usuarios", description = "Metodo resposável por consultar dados dos usuarios paginados e filtrados")
    public ResponseEntity<List<UsuarioResponseDto>> consultarPaginaFiltrado(
            @Parameter(description = "Parametro de quantidade de registro por pagina") @RequestParam Long take,
            @Parameter(description = "Parametro de quantidade de paginas")@RequestParam Long page,
            @Parameter(description = "Parametro de filtro")@RequestParam String filtro){
        return ResponseEntity.ok(usuarioService.consultarPaginaFiltrada(take, page, filtro));
    }

    @PostMapping
    @Operation(summary = "Salvar usuário", description = "Método responsavel em criar usuarios")
    public ResponseEntity<UsuarioResponseDto> salvarUsuario(@RequestBody UsuarioRequestDto usuario) {
        try {

            var usuarioResponse = usuarioService.salvarUsuario(usuario);

            return ResponseEntity.ok(usuarioResponse);
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/admin/criar")
    @Operation(summary = "Criar Usuário (Admin)", description = "Cria Organizadores ou Admins")
    public ResponseEntity<?> criarUsuarioPeloAdmin(
            @RequestBody UsuarioRequestDto dto,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado
    ) {

        if (!usuarioLogado.isAdmin()) {
            return ResponseEntity.status(403).body("Apenas Administradores podem realizar essa ação.");
        }

        try {
            var response = usuarioService.salvarUsuarioPeloAdmin(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos(@AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        if (!usuarioLogado.isAdmin()) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(usuarioService.consultarTodosSemFiltro());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema (Apenas Admin)")
    public ResponseEntity<?> excluirUsuario(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {


        if (!usuarioLogado.isAdmin()) {
            return ResponseEntity.status(403).body("Acesso negado.");
        }


        if (usuarioLogado.id().equals(id)) {
            return ResponseEntity.badRequest().body("Você não pode excluir sua própria conta enquanto está logado.");
        }

        try {
            usuarioService.excluirUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
