package br.com.spedison.comandos;

import br.com.spedison.processadores.Conexoes;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.PaginaComLivro;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


public class ComandoBuscaLivroTextualMariaDB implements ComandoInterface {

    @Getter
    @AllArgsConstructor
    private enum ModoBusca {
        TODOS("[sS]", ""),
        EXPANSAO("[eE]", "WITH QUERY EXPANSION"),
        BOOLEANO("[bB]", "IN BOOLEAN MODE");

        private String tipoBuca;
        private String mariadbBusca;

        public static ModoBusca getModoBusca(String tipo) {
            for (ModoBusca m : values()) {
                if (tipo.trim().toLowerCase().matches(m.tipoBuca)) {
                    return m;
                }
            }
            return null;
        }
    }

    private void mostraUmRegistro(String busca, PaginaComLivro pagina) {
        System.out.println("\nBusca: [[" + busca + "]]  - Nome arquivo : " + pagina.getCaminhoArquivo());
        System.out.println(" Localizada na página : " + pagina.getNumeroPagina());
        System.out.println("----Página localizada ----:\n" + pagina.getConteudo() + "\n---------------FIM PÁGINA DA BUSCA-----");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(String[] args) {

        final String busca = args[1];
        final int quantidadeRegistro = Integer.parseInt(args[2]);
        Consumer<PaginaComLivro> mostraPagina = (PaginaComLivro p) -> mostraUmRegistro(busca, p);
        String modoMariaDb = ModoBusca.getModoBusca(args[3]).getMariadbBusca();

        try (Conexoes conexoes = new Conexoes()) {
            long inicio = System.currentTimeMillis();
            List<PaginaComLivro> paginas =
                    conexoes
                            .getEntityManager()
                            .createNativeQuery("""
                                            select 
                                                p.idPagina, p.numeroPagina,
                                                p.conteudo, l.caminhoArquivo
                                            from
                                              tb_pagina p
                                              inner join tb_livro l on (p.livro_id_Livro = l.id_Livro)
                                            where
                                              MATCH (p.conteudo) AGAINST (? %s)
                                            order by
                                              l.caminhoArquivo""".formatted(modoMariaDb),
                                    PaginaComLivro.class)
                            .setParameter(1, args[1])
                            .setMaxResults(100_000)
                            .getResultList();
            paginas
                    .stream()
                    .limit(quantidadeRegistro)
                    .forEach(mostraPagina);
            long fim = System.currentTimeMillis();
            System.out.printf("Foram encontrados %d registros\nO tempo gasto para a consulta foi %d msec%n", paginas.size(),
                    fim - inicio);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -busca-livro-textual-mariadb ou -bltmdb
                Descrição : Busca livros com nome que contém as expressões do mariadb in boolean mode
                Argumentos:
                          Expressão usada pelo MariaDB para busca textual
                          Quantidade de resultados desejados na impressão.
                          Modos : Simples "S", Extendida "E" ou BinaryMode "B",
                Exemplo   : java -jar %s -busca-livro-textual-mariadbbm "+java +class* -java" 10 B
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 4 &&
                (args[0].equalsIgnoreCase("-busca-livro-textual-mariadb") ||
                        args[0].equalsIgnoreCase("-bltmdb")) &&
                args[2].matches("^[0-9]*$") &&
                Objects.nonNull(ModoBusca.getModoBusca(args[3]));
    }
}