package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.inscricao.InscricaoRequestDto;
import com.senac.Api_workshops.application.dto.inscricao.InscricaoResponseDto;
import com.senac.Api_workshops.application.services.InscricaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inscricoes")
@Tag(name = "Inscrições", description = "Gerenciamento de inscrições em workshops")
public class InscricaoController {

    @Autowired
    private InscricaoService inscricaoService;

    @PostMapping
    @Operation(summary = "Inscrever usuário", description = "Realiza a inscrição de um usuário em um workshop")
    // Alterado para Object para permitir retornar o DTO (sucesso) ou uma String/Erro (falha)
    // Se quisesse ser purista, criaria uma classe ErrorResponseDto.
    public ResponseEntity<Object> realizarInscricao(@RequestBody InscricaoRequestDto request) {
        try {
            InscricaoResponseDto response = inscricaoService.realizarInscricao(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Retorna erro 400 com a mensagem da exceção (ex: "Sem vagas")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/cancelar")
    @Operation(summary = "Cancelar Inscrição", description = "Remove a inscrição e libera a vaga")
    public ResponseEntity<?> cancelarInscricao(@RequestBody InscricaoRequestDto request) {
        try {
            inscricaoService.cancelarInscricao(request.usuarioId(), request.workshopId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/status")
    public ResponseEntity<Boolean> verificarInscricao(
            @RequestParam Long idUsuario,
            @RequestParam Long idWorkshop) {
        boolean inscrito = inscricaoService.isInscrito(idUsuario, idWorkshop);
        return ResponseEntity.ok(inscrito);
    }
}