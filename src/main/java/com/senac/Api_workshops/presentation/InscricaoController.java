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
    @Operation(summary = "Inscrever usuário", description = "usuario se inscreve em um workshop.")
    public ResponseEntity<InscricaoResponseDto> realizarInscricao(@RequestBody InscricaoRequestDto request) {
        try {
            InscricaoResponseDto response = inscricaoService.realizarInscricao(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/cancelar")
    @Operation(summary = "Cancelar Inscrição", description = "usuarios cancela a inscriçao no workshop.")
    public ResponseEntity<Void> cancelarInscricao(@RequestBody InscricaoRequestDto request) {
        try {
            inscricaoService.cancelarInscricao(request.usuarioId(), request.workshopId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Verificar Status", description = "verifica se o usuario ja esta cadastrado no workshop.")
    public ResponseEntity<Boolean> verificarInscricao(
            @RequestParam Long idUsuario,
            @RequestParam Long idWorkshop) {
        boolean inscrito = inscricaoService.isInscrito(idUsuario, idWorkshop);
        return ResponseEntity.ok(inscrito);
    }
}