package br.com.spedison.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "tb_arquivo_livro")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_livro;
    private String nomeLivro;

    @OneToMany(mappedBy = "livro")
    private List<Pagina> paginas;
}
