package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
import com.senac.Api_workshops.application.dto.workshop.WorkshopRequestDto;
import com.senac.Api_workshops.application.dto.workshop.WorkshopResponseDto;
import com.senac.Api_workshops.application.services.WorkshopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workshop")
@Tag(name = "Workshop Controller", description = "Gerenciamento completo de workshops")
public class WorkshopController {

    @Autowired
    private WorkshopService workshopService;

    @GetMapping
    @Operation(summary = "Listar Vitrine", description = "Lista workshops para Home com filtro.")
    public ResponseEntity<List<WorkshopResponseDto>> listarVitrine(
            @RequestParam(required = false) String filtro
    ) {
        var lista = workshopService.listarParaVitrine(filtro);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/gerenciar")
    @Operation(summary = "Listar Meus Workshops", description = "Lista workshops organizadores e admin - dmin vê tudo, org vê os seus.")
    public ResponseEntity<List<WorkshopResponseDto>> listarMeusWorkshops(
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        return ResponseEntity.ok(workshopService.listarParaGerenciamento(usuarioLogado));
    }

    @PostMapping
    @Operation(summary = "Cadastrar Workshop", description = "Cria um novo workshop")
    public ResponseEntity<WorkshopResponseDto> cadastrarWorkshop(
            @RequestBody WorkshopRequestDto requestDto,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        try {
            var response = workshopService.salvarWorkshop(requestDto, usuarioLogado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar Workshop", description = "Atualiza dados de um workshop existente")
    public ResponseEntity<WorkshopResponseDto> editarWorkshop(
            @PathVariable Long id,
            @RequestBody WorkshopRequestDto requestDto,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        try {
            var response = workshopService.editarWorkshop(id, requestDto, usuarioLogado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir Workshop", description = "Excluir Workshop.")
    public ResponseEntity<Void> excluirWorkshop(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        try {
            workshopService.excluirWorkshop(id, usuarioLogado);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}