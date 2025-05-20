package com.eventos.demo.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "participantes")
public class Participante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String telefone;
    
    @ManyToMany(mappedBy = "participantes")
    private Set<Evento> eventos = new HashSet<>();
    
    // Construtores
    public Participante() {
    }
    
    public Participante(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public Set<Evento> getEventos() {
        return eventos;
    }
    
    public void setEventos(Set<Evento> eventos) {
        this.eventos = eventos;
    }
    
    // Métodos auxiliares
    public void adicionarEvento(Evento evento) {
        this.eventos.add(evento);
        evento.getParticipantes().add(this);
    }
    
    public void removerEvento(Evento evento) {
        this.eventos.remove(evento);
        evento.getParticipantes().remove(this);
    }
}