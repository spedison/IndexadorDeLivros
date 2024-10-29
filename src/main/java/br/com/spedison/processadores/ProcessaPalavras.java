package br.com.spedison.processadores;

import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Palavra;
import br.com.spedison.vo.Paragrafo;
import br.com.spedison.util.StringUtils;

import java.util.List;
import java.util.Locale;

public class ProcessaPalavras {
    private Livro livroAtual;
    private Conexoes conexoes;

    public ProcessaPalavras(Livro livroAtual, Conexoes conexoes) {
        this.livroAtual = livroAtual;
        this.conexoes = conexoes;
    }

    public void processaPalavras() {
        // Implementar aqui a leitura e extração de palavras
        List<Paragrafo> paragrafoList = conexoes
                .getEntityManager()
                .createQuery(
                        "SELECT paragrafo FROM Livro livro " +
                                "join livro.paginas pagina " +
                                "join pagina.paragrafos paragrafo " +
                                "where livro = :livro " +
                                "order by pagina.numeroPagina, paragrafo.posicaoParagrafo"
                        , Paragrafo.class)
                .setParameter("livro", livroAtual)
                .getResultList();

        int posicaoPalavra = 1;
        int posicaoPagina = paragrafoList.get(0).getPagina().getNumeroPagina();

        for (Paragrafo p : paragrafoList) {

            // Posicao da palavra é reiniciado.
            if (p.getPagina().getNumeroPagina() != posicaoPagina) {
                posicaoPagina = p.getPagina().getNumeroPagina();
                posicaoPalavra = 1;
            }

            String[] palavrasOriginais = StringUtils.toToken(p.getConteudo(), 3);

            conexoes.getEntityManager().getTransaction().begin();
            for (String palavra : palavrasOriginais) {
                Palavra palavraGravada = new Palavra();
                palavraGravada.setParagrafo(p);
                String palavraLimpa = StringUtils.removeAcentos(palavra.trim().toLowerCase());
                palavraGravada.setConteudo(palavraLimpa);
                palavraGravada.setConteudoOriginal(palavra);
                palavraGravada.setPosicaoParagrafoDaPalavra(posicaoPalavra);
                posicaoPalavra++;
                palavraGravada.setParagrafo(p);
                // Inserir palavra no banco de dados
                conexoes.getEntityManager().persist(palavraGravada);
            }// Faz palavra a palavra
            conexoes.getEntityManager().getTransaction().commit();
        }// Repete para todos paragrafos
    }
}