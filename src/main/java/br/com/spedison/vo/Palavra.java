package br.com.spedison.vo;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tb_palavra")  // Define o nome da tabela no banco de dados)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Palavra {


    @ManyToOne
    Paragrafo paragrafo;


    @Id  // Define a coluna como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Define a coluna como auto-incremento
    private Integer id_palavra;

    private int posicaoParagrafoDaPalavra;

    private String conteudo;
    private String conteudoOriginal;
}
