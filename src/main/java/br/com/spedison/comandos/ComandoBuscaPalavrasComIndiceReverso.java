package br.com.spedison.comandos;

import br.com.spedison.config.Preposicoes;
import br.com.spedison.processadores.Conexoes;
import br.com.spedison.util.StringUtils;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.PaginasConsecutivasComLivro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ComandoBuscaPalavrasComIndiceReverso implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoBuscaPalavrasComIndiceReverso.class);
    private static final Preposicoes preposicoes = new Preposicoes();
    private String[] buscas;

    public String montaConsultaUsandoBusca(String busca, int distanciaMaxima) {
        /** Entrada : Consulta que será quebrado em vocábulos
         * Depois de quebrado, podemos transformar em array de palabras sem considerar as preposições.
         * Saida : Consulta que considera cada palavra um relacionamento com o a tabela palavra que será fechada com
         * com um inner join entre os relacionamentos e estabelecendo a distancia entre as tabelas.
         * Campos : PáginaAtual e Posterior, Número da página e do parágrafo
         */
        buscas = StringUtils
                .toToken(
                        StringUtils.removeAcentos(busca.toLowerCase()),
                        2,
                        preposicoes::notContains);

        if (Objects.isNull(buscas) || buscas.length == 0) {
            log.warn("Não tem busca para realizar pois só tem palavras ignoradas ou espaços.");
            return null;
        }

        final StringBuilder demaisTabelas = new StringBuilder();
        IntStream
                .range(1, buscas.length)
                .forEach(i ->
                        //Exemplos
                        //    ,palavra_com_livro_e_posicao p2
                        //    ,palavra_com_livro_e_posicao p3
                        //    ,palavra_com_livro_e_posicao p4
                        // Quantas forem as palavras para localizar.
                        demaisTabelas.append(",palavra_com_livro_e_posicao p%d\n".formatted(i + 1))
                );

        final StringBuilder comparacoes = new StringBuilder();
        IntStream
                .range(0, buscas.length)
                .forEach(i ->
                        //Exemplo
                        //    p1.conteudo = buscas[0]
                        //and p2.conteudo = buscas[1]
                        //and p3.conteudo = buscas[2]
                        //... até todos os elementos de buscas.
                        comparacoes.append("%s p%d.conteudo = '%s'\n".formatted(
                                i == 0 ? "" : "and", i + 1,
                                StringUtils.removeAcentos(buscas[i]))
                        )
                );

        final StringBuilder ligacoesIdsLivros = new StringBuilder();
        IntStream
                .range(1, buscas.length)
                .forEach(i ->
                        //Exemplo
                        //and p1.id_livro = p2.id_livro
                        //and p2.id_livro = p3.id_livro
                        ligacoesIdsLivros.append("and p%d.id_livro = p%d.id_livro\n".formatted(i, i + 1))
                );
        final StringBuilder comparaDistanciaPalavras = new StringBuilder();
        IntStream
                .range(1, buscas.length)
                .forEach(i ->
                        //exemplo
                        // and (p2.posicao_palavra_livro-p1.posicao_palavra_livro) between  0 and 5
                        // and (p3.posicao_palavra_livro-p2.posicao_palavra_livro) between  0 and 5
                        comparaDistanciaPalavras.append("and (p%d.posicao_palavra_livro - p%d.posicao_palavra_livro) between 0 and %d\n".formatted(i + 1, i, distanciaMaxima))
                );

        return """
                with
                pagina_pagina_posterior as (
                     select
                             pag1.livro_id_livro id_livro,
                             li.caminhoArquivo nomeArquivoLivro,
                             pag1.idPagina,
                             pag1.numeroPagina,
                             pag1.conteudo       conteudo_atual,
                             pag2.conteudo       conteudo_proxima
                     from tb_pagina pag1
                          left join tb_pagina pag2 on (pag1.livro_id_livro = pag2.livro_id_livro and
                                                       pag1.numeroPagina + 1 = pag2.numeroPagina)
                          inner join tb_livro li on (pag1.livro_id_livro = li.id_livro)
                )
                , palavra_com_livro_e_posicao as (
                     select
                          pag.*,
                          pal1.id_palavra,
                          pal1.conteudo,
                          pal1.conteudoOriginal,
                          pal1.posicao_palavra_livro
                     from tb_palavra pal1
                          inner join tb_paragrafo par on (par.id_paragrafo = pal1.paragrafo_id_paragrafo)
                          inner join pagina_pagina_posterior pag on (par.pagina_idPagina = pag.idPagina)
                )
                select
                     p1.*
                from
                     palavra_com_livro_e_posicao p1
                     %s
                where
                     %s
                     %s
                     %s
                """.formatted(
                demaisTabelas.toString(),
                comparacoes.toString(),
                ligacoesIdsLivros.toString(),
                comparaDistanciaPalavras.toString());
    }


    @Override
    @SuppressWarnings("unchecked")
    public void execute(String[] args) {

        int distancia = 10;
        int linhasParaExibir = 3;
        String busca = args[1];

        try (Conexoes conexoes = new Conexoes()) {

            if (args.length >= 3)
                distancia = Integer.parseInt(args[2]);
            if (args.length >= 4)
                linhasParaExibir = Integer.parseInt(args[3]);

            String sql = montaConsultaUsandoBusca(busca, distancia);

            if (Objects.isNull(sql)) {
                log.error("Consulta SQL não foi montada. Verifique os parâmetros: \"busca\"=[%s], \"busca ajustada\"=[%s]"
                        .formatted(busca, Arrays.toString(buscas)));
                return;
            }

            long inicio = System.currentTimeMillis();
            List<PaginasConsecutivasComLivro> livros =
                    conexoes.getEntityManager().createNativeQuery(
                                    sql, PaginasConsecutivasComLivro.class
                            ).setMaxResults(10_000)
                            .getResultList();

            Consumer<PaginasConsecutivasComLivro> mostraLivros =
                    (pagina) -> mostraPagina(pagina, livros.size(), busca);

            livros
                    .stream()
                    .limit(linhasParaExibir)
                    .forEach(mostraLivros);

            System.out.println("Tempo de execução: " + (System.currentTimeMillis() - inicio) + " ms");
        } catch (Exception exception) {
            System.err.println("Erro ao executar comando: " + exception.getMessage());
        }
    }

    private void mostraPagina(PaginasConsecutivasComLivro pagina, int size, String busca) {
        System.out.printf("""
                        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                        Livro      : %s  -  Página: %d
                        Busca      : %s
                        Busca Usada: %s
                        Localizados: %d
                        Conteúdo   :
                        %s
                        %s         
                        ---------------------------------------------------------------- 
                                                
                        """,
                pagina.getNomeArquivoLivro(),
                pagina.getNumeroPagina(),
                busca,
                Arrays.toString(buscas),
                size,
                pagina.getConteudoAtual(),
                pagina.getConteudoProxima());
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return
                help.append("""
                        Comando   : -busca-palavras-indice-reverso ou -bpir
                        Descrição : Realiza uma busca de palavras utilizando o índice invertido. (vai ignorar as stop words)
                        Argumentos:
                                        Palavra a ser buscada
                                        distancia máxima entre palavras     (Opcional- Valor Padrão 10)
                                        quantidade de registros para exibir (Opcional - Valor padrão 3)
                        Exemplo   :
                                     java -jar %s -busca-palavras-indice-reverso "Java executado linux" 5 4       (distancia 5, linhas mostradas 4)
                                     java -jar %s -busca-palavras-indice-reverso "Java Switch"                    (distancia 10, linhas mostradas 3)
                                     java -jar %s -busca-palavras-indice-reverso "Java while" 2                   (distancia 2, linhas mostradas 3)
                        """.formatted(
                        SystemUtils.getJarUsado(),
                        SystemUtils.getJarUsado(),
                        SystemUtils.getJarUsado()
                ));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length >= 2 &&
                args.length <= 4 &&
                (args[0].equalsIgnoreCase("-busca-palavras-indice-reverso") ||
                        args[0].equalsIgnoreCase("-bpir"));
    }

    public String[] getBusca() {
        return buscas;
    }
}
