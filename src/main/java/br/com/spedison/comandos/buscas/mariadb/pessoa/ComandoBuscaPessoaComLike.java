package br.com.spedison.comandos.buscas.mariadb.pessoa;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.processadores.Conexoes;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Pagina;
import br.com.spedison.vo.PessoaFake;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


public class ComandoBuscaPessoaComLike implements ComandoInterface {

    @Override
    public void execute(String[] args) {

        final String busca = args[1];
        final int quantidadeRegistro = Integer.parseInt(args[2]);
        Consumer<PessoaFake> mostraPagina = (p) -> ComandoPessoaUtils.mostraUmRegistro(busca, p);

        try (Conexoes conexoes = new Conexoes()) {
            long inicio = System.currentTimeMillis();
            List<PessoaFake> paginas =
                    conexoes
                            .getEntityManager()
                            .createQuery("""                                     
                                            select p
                                            from
                                              PessoaFake p
                                            where
                                              p.nome like :conteudo
                                            order by
                                              p.nome
                                            """,
                                    PessoaFake.class)
                            .setParameter("conteudo", busca)
                            .setMaxResults(100_000)
                            .getResultList();

            paginas
                    .stream()
                    .limit(quantidadeRegistro)
                    .forEach(mostraPagina);

            long fim = System.currentTimeMillis();
            System.out.printf("Foram encontrados %d registros\nO tempo gasto para a consulta foi %d msec\n", paginas.size(), fim - inicio);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -busca-pessoa-com-like ou -bpcl
                Descrição : Busca pessoa com nome que contém uma palavra-chave.
                Argumentos:
                          Palavra-chave para busca e
                          Quantidade de resultados desejados.
                Exemplo   : java -jar %s -busca-pessoa-com-like "%%java%%classe%%" 10
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 3 &&
                (args[0].equalsIgnoreCase("-busca-pessoa-com-like") ||
                        args[0].equalsIgnoreCase("-bpcl")) &&
                args[2].matches("^[0-9]*$");
    }
}
