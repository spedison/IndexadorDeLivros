package br.com.spedison.comandos.buscas.mariadb.pessoa;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.processadores.Conexoes;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.PaginaComLivro;
import br.com.spedison.vo.PessoaFake;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


public class ComandoBuscaPessoalTextualMariaDB implements ComandoInterface {

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



    @Override
    @SuppressWarnings("unchecked")
    public void execute(String[] args) {

        final String busca = args[1];
        final int quantidadeRegistro = Integer.parseInt(args[2]);
        Consumer<PessoaFake> mostraPagina = (p) -> ComandoPessoaUtils.mostraUmRegistro(busca, p);
        String modoMariaDb = ModoBusca.getModoBusca(args[3]).getMariadbBusca();

        try (Conexoes conexoes = new Conexoes()) {
            long inicio = System.currentTimeMillis();
            List<PessoaFake> paginas =
                    conexoes
                            .getEntityManager()
                            .createNativeQuery("""
                                            select
                                              id, endereco, idade, nome 
                                            from
                                              tb_pessoa_fake
                                            where
                                              MATCH (nome) AGAINST (? %s)
                                            order by
                                              nome""".formatted(modoMariaDb),
                                    PessoaFake.class)
                            .setParameter(1, modoMariaDb)
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
                        Comando   : -busca-pessoa-textual ou -bpt
                        Descrição : Busca livros com nome que contém as expressões do mariadb in boolean mode
                        Argumentos:
                                  1) Expressão usada pelo MariaDB para busca textual
                                  2) Quantidade de resultados desejados na impressão.
                                  3) Modos : Simples "S", Extendida "E" ou BinaryMode "B",
                        Exemplo   :
                                  java -jar %s -bpt "+java +class* +switch" 10 b
                                  java -jar %s -bpt "java class switch" 10 e
                                  java -jar %s -bpt "java class" 10 s
                        """.formatted(
                        SystemUtils.getJarUsado(),
                        SystemUtils.getJarUsado(),
                        SystemUtils.getJarUsado()
                )
        );
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 4 &&
                (args[0].equalsIgnoreCase("-busca-pessoa-textual") ||
                        args[0].equalsIgnoreCase("-bpt")) &&
                args[2].matches("^[0-9]*$") &&
                Objects.nonNull(ModoBusca.getModoBusca(args[3]));
    }
}