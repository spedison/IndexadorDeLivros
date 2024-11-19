package br.com.spedison.vo;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tb_palavra_lemanizada")  // Define o nome da tabela no banco de dados)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PalavraLemanizada {

    @Id  // Define a coluna como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Define a coluna como auto-incremento
    private Integer id_palavra;

    @ManyToOne
    Paragrafo paragrafo;

    @Column(name = "posicao_palavra_livro")
    private int posicaoPalavraLivro;

    @Column(name = "posicao_palavra_pagina")
    private int posicaoPalavraPagina;

    @Column(name = "posicao_palavra_paragrafo")
    private int posicaoPalavraParagrafo;

    @Lob
    @Column(columnDefinition = "TEXT")  // Define a coluna como um texto longo
    private String conteudo;
}
