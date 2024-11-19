package br.com.spedison.comandos.buscas.mariadb;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.processadores.Conexoes;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Pagina;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


public class ComandoBuscaLivroComLike implements ComandoInterface {

    private void mostraUmRegistro(String busca, Pagina pagina) {
        Livro livro = pagina.getLivro();
        System.out.println("\nBusca: [[" + busca + "]]  - Nome arquivo : " + livro.getCaminhoArquivo());
        System.out.println("O livro tem %d ".formatted(livro.getPaginas().size()) + " Localizada na página : " + pagina.getNumeroPagina());
        System.out.println("----Página localizada ----:\n" + pagina.getConteudo() + "\n---------------FIM PÁGINA DA BUSCA-----");
    }

    @Override
    public void execute(String[] args) {

        final String busca = args[1];
        final int quantidadeRegistro = Integer.parseInt(args[2]);
        Consumer<Pagina> mostraPagina = (Pagina p) -> mostraUmRegistro(busca, p);

        try (Conexoes conexoes = new Conexoes()) {
            long inicio = System.currentTimeMillis();
            List<Pagina> paginas =
                    conexoes
                            .getEntityManager()
                            .createQuery("""                                     
                                            select p
                                            from
                                              Pagina p
                                            where
                                              p.conteudo like :conteudo
                                            order by
                                              p.livro.caminhoArquivo""",
                                    Pagina.class)
                            .setParameter("conteudo", args[1])
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
                Comando   : -busca-livro-com-like ou -blcl
                Descrição : Busca livros com nome que contém uma palavra-chave.
                Argumentos:
                          Palavra-chave para busca e
                          Quantidade de resultados desejados.
                Exemplo   : java -jar %s -busca-livro-com-like "%%java%%classe%%" 10
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 3 &&
                (args[0].equalsIgnoreCase("-busca-livro-com-like") ||
                        args[0].equalsIgnoreCase("-blcl")) &&
                args[2].matches("^[0-9]*$");
    }
}
