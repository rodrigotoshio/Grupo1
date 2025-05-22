package com.eventos.dto;

import java.time.LocalDate;
import com.eventos.models.Eventos;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class EventosDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Size(max = 500, message = "Descrição pode ter no máximo 500 caracteres")
    private String descricao;

    @FutureOrPresent(message = "Data deve ser atual ou futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;

    @NotBlank(message = "Local é obrigatório")
    private String local;

    @PositiveOrZero(message = "Vagas não pode ser negativo")
    private Integer vagasTotais;

    @PositiveOrZero(message = "Vagas disponíveis não pode ser negativo")
    private Integer vagasDisponiveis;

    // Construtores
    public EventosDTO() {
        // Removed initialization to avoid NullPointerException during deserialization
    }

    public EventosDTO(Long id, String nome, String descricao, LocalDate data, 
                     String local, Integer vagasTotais, Integer vagasDisponiveis) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.local = local;
        this.vagasTotais = vagasTotais;
        this.vagasDisponiveis = vagasDisponiveis;
    }

    // Métodos de conversão
    public static EventosDTO fromEntity(Eventos eventos) {
        if (eventos == null) return null;
        
        return new EventosDTO(
            eventos.getId(),
            eventos.getNome(),
            eventos.getDescricao(),
            eventos.getData(),
            eventos.getLocal(),
            eventos.getVagasTotais(),
            eventos.getVagasDisponiveis()
        );
    }

    public Eventos toEntity() {
        Eventos eventos = new Eventos();
        eventos.setId(this.id);
        eventos.setNome(this.nome);
        eventos.setDescricao(this.descricao);
        eventos.setData(this.data);
        eventos.setLocal(this.local);
        eventos.setVagasTotais(this.vagasTotais);
        eventos.setVagasDisponiveis(this.vagasDisponiveis);
        return eventos;
    }

    // Getters and Setters
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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getVagasTotais() {
        return vagasTotais;
    }

    public void setVagasTotais(Integer vagasTotais) {
        this.vagasTotais = vagasTotais;
    }

    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(Integer vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }
}