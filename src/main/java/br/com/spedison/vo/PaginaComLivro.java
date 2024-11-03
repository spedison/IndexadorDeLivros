package br.com.spedison.vo;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginaComLivro {
    private Integer idPagina;
    private int numeroPagina;
    private String conteudo;
    private String caminhoArquivo;
}
