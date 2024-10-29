package br.com.spedison.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_frequencia_palavra")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrequenciaPalavra {

    @ManyToOne
    Livro livro;

    @ManyToOne
    Pagina pagina;

    @Id
    @Column(name = "id_pagina")
    private Integer idPagina;

    @Column(name = "frequencia")
    private Double frequencia;

    @Column(name = "conteudo")
    private String conteudo;

}