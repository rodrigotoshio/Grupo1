package com.eventos.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.eventos.dto.EventosDTO;
import com.eventos.dto.ParticipanteDTO;
import com.eventos.services.EventosService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/eventos")
public class EventosController {

    @Autowired
    private EventosService eventosService;

    // List all events
    @GetMapping
    public ResponseEntity<List<EventosDTO>> listar() {
        return ResponseEntity.ok(eventosService.buscarTodos());
    }

    // Get event by ID
    @GetMapping("/{id}")
    public ResponseEntity<EventosDTO> buscarPorId(@PathVariable Long id) {
        return eventosService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new event
    @PostMapping
    public ResponseEntity<EventosDTO> salvar(@Valid @RequestBody EventosDTO eventosDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventosService.salvar(eventosDTO));
    }

    // Enroll a participant in an event
    @PostMapping("/{eventoId}/participantes")
    public ResponseEntity<Map<String, Object>> adicionarParticipante(
            @PathVariable Long eventoId,
            @RequestBody Map<String, Long> payload) {
        Long participanteId = payload.get("participanteId");
        if (participanteId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "O ID do participante é obrigatório"
            ));
        }

        boolean inscrito = eventosService.inscreverParticipante(eventoId, participanteId);

        if (inscrito) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Participante inscrito com sucesso"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Não foi possível inscrever o participante. Verifique se o evento ou participante existe, se há vagas disponíveis ou se o participante já está inscrito."
            ));
        }
    }

    // Update an existing event
    @PutMapping("/{id}")
    public ResponseEntity<EventosDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EventosDTO eventosDTO) {
        return eventosService.atualizar(id, eventosDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete an event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (eventosService.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Cancel participant enrollment
    @DeleteMapping("/{eventoId}/participantes/{participanteId}")
    public ResponseEntity<Map<String, Object>> cancelarInscricao(
            @PathVariable Long eventoId,
            @PathVariable Long participanteId) {
        boolean cancelado = eventosService.cancelarInscricao(eventoId, participanteId);

        if (cancelado) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Inscrição cancelada com sucesso"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Não foi possível cancelar a inscrição. Verifique se o evento ou participante existe ou se o participante está inscrito."
            ));
        }
    }

    // List participants of an event
    @GetMapping("/{eventoId}/participantes")
    public ResponseEntity<List<ParticipanteDTO>> listarParticipantes(@PathVariable Long eventoId) {
        List<ParticipanteDTO> participantes = eventosService.listarParticipantesDoEvento(eventoId);
        if (participantes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(participantes);
    }
}