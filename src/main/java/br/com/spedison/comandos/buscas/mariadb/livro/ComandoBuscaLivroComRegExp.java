package br.com.spedison.comandos.buscas.mariadb.livro;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.processadores.Conexoes;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.PaginaComLivro;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


public class ComandoBuscaLivroComRegExp implements ComandoInterface {

    private void mostraUmRegistro(String busca, PaginaComLivro pagina) {
        System.out.println("\nBusca: [[" + busca + "]]  - Nome arquivo : " + pagina.getCaminhoArquivo());
        System.out.println("----Página localizada ----:\n" + pagina.getConteudo() + "\n---------------FIM PÁGINA DA BUSCA-----");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(String[] args) {

        final String busca = args[1];
        final int quantidadeRegistro = Integer.parseInt(args[2]);
        Consumer<PaginaComLivro> mostraPagina = (p) -> mostraUmRegistro(busca, p);

        try (Conexoes conexoes = new Conexoes()) {
            long inicio = System.currentTimeMillis();

            List<PaginaComLivro> paginas = conexoes
                    .getEntityManager()
                    .createNativeQuery("""
                                    select 
                                      p.idPagina, p.numeroPagina,
                                      p.conteudo, p.conteudoOriginal,
                                      l.caminhoArquivo
                                    from
                                      tb_pagina p
                                      inner join tb_livro l on (p.livro_id_Livro = l.id_Livro)
                                    where
                                      p.conteudo regexp :conteudo_regexp
                                    order by
                                      l.caminhoArquivo
                                    """,
                            PaginaComLivro.class)
                    .setParameter("conteudo_regexp", "(?m)" + args[1])
                    .setMaxResults(100_000)
                    .getResultList();

            long fim = System.currentTimeMillis();
            paginas
                    .stream()
                    .limit(quantidadeRegistro)
                    .forEach(mostraPagina);

            System.out.printf("""
                    Foram encontrados %d registros
                    O tempo gasto para a consulta foi %f s
                    """, paginas.size(), (fim - inicio)/1000.0);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -busca-livro-com-regexp ou -blcr
                Descrição : Busca livros com nome que contém uma palavra-chave usando regexp.
                Argumentos:
                          RegExp usadas para a busca e
                          Quantidade de resultados desejados.
                Exemplo   : java -jar %s -busca-livro-com-regexp ".*\bjava\b.*\bswitch\b.*" 10
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 3 &&
                (args[0].equalsIgnoreCase("-busca-livro-com-regexp") ||
                        args[0].equalsIgnoreCase("-blcr")) &&
                args[2].matches("^[0-9]*$");
    }
}
