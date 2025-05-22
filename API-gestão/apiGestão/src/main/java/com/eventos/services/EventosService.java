package com.eventos.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventos.dto.EventosDTO;
import com.eventos.dto.ParticipanteDTO;
import com.eventos.models.Eventos;
import com.eventos.models.Participante;
import com.eventos.repository.EventosRepository;
import com.eventos.repository.ParticipanteRepository;

@Service
public class EventosService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventosService.class);

    @Autowired
    private EventosRepository eventosRepository;
    
    @Autowired
    private ParticipanteRepository participanteRepository;
    
    // Buscar todos os eventos
    public List<EventosDTO> buscarTodos() {
        return eventosRepository.findAll().stream()
                .map(EventosDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Buscar evento por ID
    public Optional<EventosDTO> buscarPorId(Long id) {
        return eventosRepository.findById(id)
                .map(EventosDTO::fromEntity);
    }
    
    // Salvar novo evento
    @Transactional
    public EventosDTO salvar(EventosDTO dto) {
        logger.info("Received DTO: {}", dto);
        Eventos evento = dto.toEntity();
        logger.info("Converted to Entity: {}", evento);
        evento.setVagasDisponiveis(evento.getVagasTotais());
        logger.info("Saving event with vagasDisponiveis: {}", evento.getVagasDisponiveis());
        return EventosDTO.fromEntity(eventosRepository.save(evento));
    }
    
    // Atualizar evento existente
    @Transactional
    public Optional<EventosDTO> atualizar(Long id, EventosDTO dto) {
        return eventosRepository.findById(id)
            .map(evento -> {
                evento.setNome(dto.getNome());
                evento.setDescricao(dto.getDescricao());
                evento.setData(dto.getData());
                evento.setLocal(dto.getLocal());
                // Atualiza vagasTotais e ajusta vagasDisponiveis se necessário
                int inscritos = evento.getVagasTotais() - evento.getVagasDisponiveis();
                evento.setVagasTotais(dto.getVagasTotais());
                // Garante que vagasDisponiveis não seja maior que vagasTotais
                evento.setVagasDisponiveis(Math.min(dto.getVagasTotais(), evento.getVagasTotais() - inscritos));
                return EventosDTO.fromEntity(eventosRepository.save(evento));
            });
    }
    
    // Deletar evento
    @Transactional
    public boolean deletar(Long id) {
        if (eventosRepository.existsById(id)) {
            eventosRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Inscrever participante em um evento
    @Transactional
    public boolean inscreverParticipante(Long eventoId, Long participanteId) {
        Optional<Eventos> eventoOpt = eventosRepository.findById(eventoId);
        Optional<Participante> participanteOpt = participanteRepository.findById(participanteId);
        
        if (eventoOpt.isEmpty() || participanteOpt.isEmpty()) {
            return false;
        }
        
        Eventos evento = eventoOpt.get();
        Participante participante = participanteOpt.get();
        
        // Verifica se há vagas disponíveis
        if (evento.getVagasDisponiveis() <= 0) {
            return false;
        }
        
        // Verifica se já está inscrito comparando IDs
        boolean jaInscrito = evento.getParticipantes().stream()
            .anyMatch(p -> p.getId().equals(participanteId));
            
        if (jaInscrito) {
            return false; // Retorna false para indicar que o participante já estava inscrito
        }
        
        // Atualiza vagasDisponiveis e adiciona participante
        evento.setVagasDisponiveis(evento.getVagasDisponiveis() - 1);
        evento.getParticipantes().add(participante);
        eventosRepository.save(evento);
        
        return true;
    }
    
    // Cancelar inscrição de participante
    @Transactional
    public boolean cancelarInscricao(Long eventoId, Long participanteId) {
        Optional<Eventos> eventoOpt = eventosRepository.findById(eventoId);
        Optional<Participante> participanteOpt = participanteRepository.findById(participanteId);
        
        if (eventoOpt.isEmpty() || participanteOpt.isEmpty()) {
            return false;
        }
        
        Eventos evento = eventoOpt.get();
        Participante participante = participanteOpt.get();
        
        // Remove participante e atualiza vagasDisponiveis
        if (evento.getParticipantes().remove(participante)) {
            evento.setVagasDisponiveis(evento.getVagasDisponiveis() + 1);
            eventosRepository.save(evento);
            return true;
        }
        
        return false;
    }
    
    // Listar participantes de um evento
    public List<ParticipanteDTO> listarParticipantesDoEvento(Long eventoId) {
        return eventosRepository.findById(eventoId)
            .map(evento -> evento.getParticipantes().stream()
                .map(participante -> {
                    ParticipanteDTO dto = new ParticipanteDTO();
                    dto.setId(participante.getId());
                    dto.setNome(participante.getNome());
                    dto.setEmail(participante.getEmail());
                    dto.setTelefone(participante.getTelefone());
                    return dto;
                })
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
    }
}