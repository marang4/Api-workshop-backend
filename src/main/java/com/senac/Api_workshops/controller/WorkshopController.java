package com.senac.Api_workshops.controller;

import com.senac.Api_workshops.dto.WorkshopRequestDto;
import com.senac.Api_workshops.model.Workshop;
import com.senac.Api_workshops.repository.WorkshopRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/workshop")
@Tag(name = "contorlador de workshops", description = "camada responsavel por administrar a criaçao, atualização, listagem e delete de workshops")
public class WorkshopController {


    @Autowired
    private WorkshopRepository  workshopRepository;

    @GetMapping
    @Operation(summary = "Listar workshop", description = "Método responsável por listar todos os Workshops")
    public ResponseEntity<?> consultarTodos() {

        return ResponseEntity.ok(workshopRepository.findAll());
    }



    @PostMapping
    @Operation(summary = "Cadastrar Workshop", description = "Método responsável por cadastrar workshops")
    public ResponseEntity<?> cadastrarWorkshop( @RequestBody WorkshopRequestDto requestDto) {
        try {
            var workshopEntidade = requestDto.transformarEmEntidade();
            workshopRepository.save(workshopEntidade);
            return ResponseEntity.ok(workshopEntidade);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("{id}")
    @Operation(summary = "excluir workshop", description = "Método responsavel por excluir workshops cadastrados")
    public ResponseEntity<Void> excluirWorkshop(@PathVariable Long id) {
        if (!workshopRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
        }
         try {
                workshopRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }



    }


    @PutMapping("{id}")
    @Operation(summary = "editar workshop", description = "Metodo responsavel por editar workshops cadastrados")
    public ResponseEntity<?> editarWorkshop(@PathVariable Long id, @RequestBody WorkshopRequestDto requestDto) {
        if (!workshopRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Optional<Workshop> optionalWorkshop = workshopRepository.findById(id);
        var workshop = optionalWorkshop.get();

        workshop.setTema(requestDto.tema());
        workshop.setData(requestDto.data());
        workshop.setVagasTotais(requestDto.vagasTotais());
        workshop.setVagasOcupadas(requestDto.vagasOcupadas());
        workshopRepository.save(workshop);


        return ResponseEntity.ok(workshop);

    }





}
