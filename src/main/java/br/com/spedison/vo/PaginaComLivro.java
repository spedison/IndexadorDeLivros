package br.com.spedison.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PaginaComLivro {
    private Integer idPagina;
    private Integer numeroPagina;
    private String conteudo;
    private String conteudoOriginal;
    private String caminhoArquivo;

    public PaginaComLivro() {
    }

    public PaginaComLivro(Integer idPagina, Integer numeroPagina, String conteudo, String conteudoOriginal, String caminhoArquivo) {
        this.idPagina = idPagina;
        this.numeroPagina = numeroPagina;
        this.conteudo = conteudo;
        this.conteudoOriginal = conteudoOriginal;
        this.caminhoArquivo = caminhoArquivo;
    }

    public String leIdPaginaStr() {
        return "%d".formatted(idPagina);
    }
}
