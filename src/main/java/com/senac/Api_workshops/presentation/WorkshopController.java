package com.senac.Api_workshops.presentation;

import com.senac.Api_workshops.application.dto.workshop.WorkshopRequestDto;
import com.senac.Api_workshops.domain.model.Workshop;
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

    @GetMapping
    @Operation(summary = "Listar workshop", description = "Método responsável por listar todos os Workshops")
    public ResponseEntity<List<Workshop>> consultarTodos() {
        return ResponseEntity.ok(workshopService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Cadastrar Workshop", description = "Método responsável por cadastrar workshops")
    public ResponseEntity<?> cadastrarWorkshop(@RequestBody WorkshopRequestDto requestDto) {
        try {
            Workshop workshopSalvo = workshopService.salvarWorkshop(requestDto);
            return ResponseEntity.ok(workshopSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Dados inválidos: " + e.getMessage());
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
    @Operation(summary = "Editar workshop", description = "Metodo responsavel por editar workshops cadastrados")
    public ResponseEntity<Workshop> editarWorkshop(@PathVariable Long id, @RequestBody WorkshopRequestDto requestDto) {
        Optional<Workshop> workshopAtualizado = workshopService.editarWorkshop(id, requestDto);


        return workshopAtualizado
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}