package br.com.spedison.processadores;

import br.com.spedison.config.Preposicoes;
import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Palavra;
import br.com.spedison.vo.Paragrafo;
import br.com.spedison.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ProcessaPalavras {

    private final Livro livroAtual;
    private final Conexoes conexoes;
    private static final Preposicoes preposicoes = new Preposicoes();

    public ProcessaPalavras(Conexoes conexoes, Livro livroAtual) {
        this.livroAtual = livroAtual;
        this.conexoes = conexoes;
    }

    public void apagaPalavrasDoLivro() {
        Livro livro = livroAtual;
        //conexoes.beginTransaction();
        conexoes.getEntityManager().createQuery(
                        """
                                    delete from Palavra p
                                    where p.paragrafo.pagina.livro.idLivro = :idLivro
                                """)
                .setParameter("idLivro", livro.getIdLivro())
                .executeUpdate();
        //conexoes.commitTransaction();
    }

    public void processaPalavras() {
        // Se for preposição ele deve não se processado. Não deve estar na lista de preposições.
        Predicate<String> filtroPreposicaoEStopWords = preposicoes::notContains ;

        // Implementar aqui a leitura e extração de palavras a partir dos diversos parágrafos.
        List<Paragrafo> paragrafoList = conexoes
                .getEntityManager()
                .createQuery("""
                                SELECT
                                    paragrafo
                                FROM
                                    Livro livro
                                join livro.paginas pagina
                                join pagina.paragrafos paragrafo
                                where
                                    livro = :livro
                                order by
                                    livro.idLivro, pagina.idPagina, paragrafo.idParagrafo""",
                Paragrafo.class)
                .setParameter("livro", livroAtual)
                .getResultList();

        // Contador de todas as palavras, pq aqui só vai ser processado 1 único livro.
        int posicaoPalavraLivro = 1;

        int idPagina = -1;
        int posicaoPalavraPagina = 1;

        int idParagrafo = -1;
        int posicaoPalavraParagrafo = 1;

        for (Paragrafo p : paragrafoList) {
            String[] palavrasOriginais = StringUtils.toToken(p.getConteudo(), 2, filtroPreposicaoEStopWords);
            conexoes.beginTransaction();
            for (String palavra : palavrasOriginais) {

                // Não tem mudança de Livro, pois essa chamada será por livro.

                // Se mudou a página
                if (idPagina != p.getPagina().getIdPagina()) {
                    posicaoPalavraPagina = 1;
                    idPagina = p.getPagina().getIdPagina();
                }

                // Se mudou de parágrafo
                if(idParagrafo != p.getIdParagrafo()) {
                    posicaoPalavraParagrafo = 1;
                    idParagrafo = p.getIdParagrafo();
                }

                Palavra palavraGravada = new Palavra();
                palavraGravada.setParagrafo(p);
                String palavraLimpa = StringUtils.removeAcentos(palavra.trim().toLowerCase());
                palavraGravada.setConteudo(palavraLimpa);
                palavraGravada.setConteudoOriginal(palavra);
                palavraGravada.setPosicaoPalavraLivro(posicaoPalavraLivro++);
                palavraGravada.setPosicaoPalavraPagina(posicaoPalavraPagina++);
                palavraGravada.setPosicaoPalavraParagrafo(posicaoPalavraParagrafo++);
                palavraGravada.setParagrafo(p);
                if (Objects.isNull(p.getPalavras()))
                    p.setPalavras(new LinkedList<>());
                p.getPalavras().add(palavraGravada);
                // Inserir palavra no banco de dados
                conexoes.grava(palavraGravada);
                conexoes.grava(p);
            }// Faz palavra a palavra
            conexoes.commitTransaction();
        }// Repete para todos paragrafos
    }
}
