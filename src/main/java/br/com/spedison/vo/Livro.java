package br.com.spedison.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "tb_livro")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro")
    private Integer idLivro;
    private String caminhoArquivo;
    private String caminhoCompletoArquivo;
    private String hash;
    private Instant dataHoraInicial;
    private Instant dataHoraFinal;
    private Long tempoGastoSegundos;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Pagina> paginas;
}
