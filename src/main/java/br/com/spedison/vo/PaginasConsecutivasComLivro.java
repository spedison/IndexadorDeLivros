package br.com.spedison.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginasConsecutivasComLivro {
    private int id_livro;
    private String nomeArquivoLivro;
    private int idPagina;
    private int numeroPagina;
    private String conteudoAtual;
    private String conteudoProxima;
    private int id_palavra;
    private String conteudo;
    private String conteudoOriginal;
    private int posicao_palavra_livro;

}
