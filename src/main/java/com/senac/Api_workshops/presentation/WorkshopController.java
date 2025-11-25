package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.usuario.UsuarioPrincipalDTO;
import com.senac.Api_workshops.application.dto.workshop.WorkshopRequestDto;
import com.senac.Api_workshops.application.dto.workshop.WorkshopResponseDto;
import com.senac.Api_workshops.application.services.WorkshopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // <--- IMPORTANTE
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workshop")
@Tag(name = "Workshop Controller", description = "Gerenciamento de workshops")
public class WorkshopController {

    @Autowired
    private WorkshopService workshopService;


    @GetMapping
    public ResponseEntity<List<WorkshopResponseDto>> listarParaVitrine() {
        return ResponseEntity.ok(workshopService.listarParaVitrine());
    }

    @GetMapping("/gerenciar")
    public ResponseEntity<List<WorkshopResponseDto>> listarMeusWorkshops(@AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        return ResponseEntity.ok(workshopService.listarParaGerenciamento(usuarioLogado));
    }

    @PostMapping
    @Operation(summary = "Cadastrar Workshop")
    public ResponseEntity<?> cadastrarWorkshop(
            @RequestBody WorkshopRequestDto requestDto,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        try {
            var response = workshopService.salvarWorkshop(requestDto, usuarioLogado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar Workshop")
    public ResponseEntity<?> editarWorkshop(
            @PathVariable Long id,
            @RequestBody WorkshopRequestDto requestDto,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        try {
            var response = workshopService.editarWorkshop(id, requestDto, usuarioLogado);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir Workshop")
    public ResponseEntity<?> excluirWorkshop(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioPrincipalDTO usuarioLogado) {
        try {
            workshopService.excluirWorkshop(id, usuarioLogado);
            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {

            return ResponseEntity.status(403).body(e.getMessage());

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity.status(409).body("Não é possível excluir este workshop pois existem inscrições vinculadas a ele.");

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}