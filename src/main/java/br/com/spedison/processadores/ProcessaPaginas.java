package br.com.spedison.processadores;

import br.com.spedison.util.LePDF;
import br.com.spedison.util.StringUtils;
import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Pagina;

import java.util.function.Consumer;

public class ProcessaPaginas implements Consumer<LePDF.PaginaLida> {

    private final Conexoes conexoes;
    int contaPaginas;

    Livro livro;
    int commitBook = 50;

    public ProcessaPaginas(Conexoes conexoes, Livro livro) {
        this.livro = livro;
        this.conexoes = conexoes;
    }

    public void processaArquivo(String nomeArquivoPDF) {
        conexoes.beginTransaction();
        LePDF.lerPDF(nomeArquivoPDF, this);
        conexoes.commitTransaction();
    }

    public void apagaPaginasDoLivro() {
        conexoes.beginTransaction();
        conexoes.getEntityManager().createQuery(
                        """
                                    delete from Pagina p
                                    where p.livro = :livro
                                """, Pagina.class)
                .setParameter("livro", livro)
                .executeUpdate();
        conexoes.commitTransaction();
    }


    public String ajustaConteudoPagina(String conteudoPDF) {

        // Quebra a página em linhas
        String[] linhas = conteudoPDF.split("[\n]");

        // Buffer de retorno
        StringBuffer acumuladorDeLinhas = new StringBuffer();

        // Tá vazio, retorna.
        if (linhas.length == 0)
            return conteudoPDF;

        // O buffer atual vira a linha 1.
        acumuladorDeLinhas.append(linhas[0].trim());

        // Roda todas as linhas (pula a primeira)
        for (int l = 1; l < linhas.length; l++) {

            if (linhas[l].isBlank())
                continue;

            // Caso contrário tento unir as linhas
            if (!StringUtils.uneLinhas(acumuladorDeLinhas.toString(),
                    linhas[l], acumuladorDeLinhas)) {
                // Se as linhas não tem um relacionamento (aparente) pode-se adicionar um enter e a linha atual.
                acumuladorDeLinhas.append("\n").append(linhas[l].trim());
            }
        }
        return acumuladorDeLinhas.toString();
    }

    @Override
    public void accept(LePDF.PaginaLida paginaLida) {

        contaPaginas++;
        Pagina pagina = new Pagina();
        pagina.setLivro(livro);
        pagina.setNumeroPagina(paginaLida.getNumeroPagina());
        pagina.setConteudo(ajustaConteudoPagina(paginaLida.getConteudoPagina()));

        livro.getPaginas().add(pagina);
        conexoes.grava(pagina);

        // A cada x registros, é realizado um commit.
        if (contaPaginas % commitBook == 0) {
            conexoes.commitTransaction();
            conexoes.beginTransaction();
        }
    }
}
