package com.imqh.personas.controller;

import com.imqh.personas.dto.PersonaRequest;
import com.imqh.personas.dto.PersonaResponse;
import com.imqh.personas.dto.SolicitudCreacionResponse;
import com.imqh.personas.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public ResponseEntity<List<PersonaResponse>> listar() {
        return ResponseEntity.ok(personaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(personaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<SolicitudCreacionResponse> crear(@Valid @RequestBody PersonaRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(personaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PersonaRequest request) {
        return ResponseEntity.ok(personaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        personaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
