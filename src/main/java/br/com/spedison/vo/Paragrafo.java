package br.com.spedison.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "tb_paragrafo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paragrafo {

    @ManyToOne
    Pagina pagina;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Define a coluna como auto-incremento
    private Integer id_paragrafo;

    private String conteudo;
    private int posicaoParagrafo;

    @OneToMany(mappedBy = "paragrafo")
    private List<Palavra> palavras;
}
