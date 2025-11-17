package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.usuario.UsuarioResponseDto;
import com.senac.Api_workshops.application.dto.workshop.WorkshopRequestDto;
import com.senac.Api_workshops.application.dto.workshop.WorkshopResponseDto;
import com.senac.Api_workshops.application.services.UsuarioService;
import com.senac.Api_workshops.domain.entity.Workshop;
import com.senac.Api_workshops.application.services.WorkshopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/workshop")
@Tag(name = "controlador de workshops", description = "camada responsavel por administrar a criaçao, atualização, listagem e delete de workshops")
public class WorkshopController {

    @Autowired
    private WorkshopService workshopService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar workshop", description = "Método responsável por listar todos os Workshops")
    public ResponseEntity<List<WorkshopResponseDto>> consultarTodos() {
        return ResponseEntity.ok(workshopService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Cadastrar Workshop", description = "Método responsável por cadastrar workshops")
    public ResponseEntity<WorkshopResponseDto> cadastrarWorkshop(@RequestBody WorkshopRequestDto requestDto) {
        try {
            var workshopResponse = workshopService.salvarWorkshop(requestDto);
            return ResponseEntity.ok(workshopResponse);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir workshop", description = "Método responsavel por excluir workshops cadastrados")
    public ResponseEntity<Void> excluirWorkshop(@PathVariable Long id) {
        boolean deleted = workshopService.excluirWorkshop(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar workshop", description = "Método para editar workshops cadastrados")
    public ResponseEntity<WorkshopResponseDto> editarWorkshop(
            @PathVariable Long id,
            @RequestBody WorkshopRequestDto requestDto) {

        WorkshopResponseDto workshopAtualizado = workshopService.editarWorkshop(id, requestDto);
        return ResponseEntity.ok(workshopAtualizado);
    }
}