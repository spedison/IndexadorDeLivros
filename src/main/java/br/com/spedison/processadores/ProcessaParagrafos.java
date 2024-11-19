package br.com.spedison.processadores;

import br.com.spedison.processadores.dto.ResultadoLematizador;
import br.com.spedison.processadores.dto.ResultadoTokens;
import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Pagina;
import br.com.spedison.vo.Paragrafo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ProcessaParagrafos {

    ProcessadorNLP nlp;
    private final Conexoes conexoes;
    Livro livro;

    private static final Logger log = LoggerFactory.getLogger(ProcessaParagrafos.class);


    public ProcessaParagrafos(Conexoes conexoes, Livro livro) {
        this.livro = livro;
        this.conexoes = conexoes;
        nlp = new ProcessadorNLP();
    }


    public void processaParagrafos() {
        List<Pagina> paginasParaProcessar = conexoes
                .getEntityManager()
                .createQuery("""
                        SELECT
                          p
                        FROM
                          Pagina p
                        WHERE
                          p.livro = :livro
                        """, Pagina.class)
                .setParameter("livro", livro)
                .getResultList();


        for (Pagina pagina : paginasParaProcessar) {
            int contaParagrafos = 1;

            // Extrai os parágrafos da página (separados por paragrafos)
            ResultadoTokens resultadoTokens = nlp.getTokensParagrafoComRetry(pagina.getConteudo());

            List<String> paragrafos;
            if (resultadoTokens.getEstado().trim().equalsIgnoreCase("ok"))
                paragrafos = resultadoTokens.getTokens();
            else
                paragrafos = Arrays.stream(pagina.getConteudo().split("\n\s")).toList();

            conexoes.beginTransaction();

            // Processa o conteúdo da página
            for (String paragrafo : paragrafos) {

                if (paragrafo.trim().isBlank())
                    continue;

                Paragrafo paragrafoGravado = new Paragrafo();
                paragrafoGravado.setPagina(pagina);
                paragrafoGravado.setConteudo(paragrafo);
                paragrafoGravado.setPosicaoParagrafo(contaParagrafos);
                ResultadoLematizador resultadoLemanizador = nlp.getLemanizacaoComRetry(paragrafo);
                if (resultadoLemanizador.getEstado().trim().equalsIgnoreCase("ok"))
                    paragrafoGravado.setConteudoLematizado(resultadoLemanizador.getLematizado());
                else {
                    paragrafoGravado.setConteudoLematizado(paragrafo);
                    log.error("Problemas :: Retorno %s - com o paragrafo %d do arquivo %s ao lemanizar : %s".formatted(
                            resultadoLemanizador.getEstado(),
                            contaParagrafos,
                            livro.getCaminhoArquivo(),
                            paragrafo
                    ));
                }
                conexoes.grava(paragrafoGravado);
                contaParagrafos++;
            }
            conexoes.commitTransaction();
        }
    }

    public void apagaParagrafosDoLivro() {
        // conexoes.beginTransaction();
        conexoes
                .getEntityManager()
                .createQuery(
                        """
                                    delete from Paragrafo p
                                    where p.pagina.livro.idLivro = :livro
                                """)
                .setParameter("livro", livro.getIdLivro())
                .executeUpdate();
        //conexoes.commitTransaction();
    }
}
