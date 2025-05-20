package com.eventos.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "eventos")
public class Evento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(nullable = false)
    private LocalDateTime data;
    
    @Column(nullable = false)
    private String local;
    
    @Column(nullable = false)
    private Integer vagas;
    
    @ManyToMany
    @JoinTable(
        name = "evento_participante",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "participante_id")
    )
    private Set<Participante> participantes = new HashSet<>();
    
    // Construtores
    public Evento() {
    }
    
    public Evento(String nome, String descricao, LocalDateTime data, String local, Integer vagas) {
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.local = local;
        this.vagas = vagas;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public LocalDateTime getData() {
        return data;
    }
    
    public void setData(LocalDateTime data) {
        this.data = data;
    }
    
    public String getLocal() {
        return local;
    }
    
    public void setLocal(String local) {
        this.local = local;
    }
    
    public Integer getVagas() {
        return vagas;
    }
    
    public void setVagas(Integer vagas) {
        this.vagas = vagas;
    }
    
    public Set<Participante> getParticipantes() {
        return participantes;
    }
    
    public void setParticipantes(Set<Participante> participantes) {
        this.participantes = participantes;
    }
    
    // Métodos de negócio
    public boolean temVagasDisponiveis() {
        return participantes.size() < vagas;
    }
    
    public void adicionarParticipante(Participante participante) {
        if (!temVagasDisponiveis()) {
            throw new IllegalStateException("Não há vagas disponíveis para este evento");
        }
        if (participante == null) {
            throw new IllegalArgumentException("O participante não pode ser nulo");
        }
        if (participantes.contains(participante)) {
            throw new IllegalStateException("Este participante já está inscrito no evento");
        }
        
        this.participantes.add(participante);
        participante.getEventos().add(this);
    }
    
    public void removerParticipante(Participante participante) {
        if (participante == null) {
            return;
        }
        
        if (this.participantes.remove(participante)) {
            participante.getEventos().remove(this);
        }
    }
    
    public int getQuantidadeInscritos() {
        return this.participantes.size();
    }
    
    public int getVagasDisponiveis() {
        return this.vagas - this.participantes.size();
    }
}