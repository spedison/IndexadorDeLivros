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
    @Column (name = "id_paragrafo")
    private Integer idParagrafo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String conteudoLematizado;

    private int posicaoParagrafo;

    @OneToMany(mappedBy = "paragrafo" , cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Palavra> palavras;
}
