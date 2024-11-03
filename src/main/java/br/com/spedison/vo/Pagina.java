package br.com.spedison.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "tb_pagina")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagina {
    @ManyToOne
    Livro livro;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPagina;

    private int numeroPagina;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @OneToMany(mappedBy = "pagina", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Paragrafo> paragrafos;
}
