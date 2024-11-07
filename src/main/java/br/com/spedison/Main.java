package br.com.spedison;

import br.com.spedison.comandos.*;
import br.com.spedison.processadores.Conexoes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class Main {


    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Criando todas os comandos.");

        List<ComandoInterface> comandos =
                List.of(
                        new ComandoIndexar(),
                        new ComandoApagarTodosLivros(),
                        new ComandoApagarUmLivro(),
                        new ComandoPessoaFake(),
                        new ComandoBuscaLivroComLike(),
                        new ComandoBuscaLivroTextualMariaDB(),
                        new ComandoBuscaPalavrasComIndiceReverso(),
                        new ComandoBuscaLucene(),
                        new ComandoIndexaLucene()
                );

        if (args.length == 0){
            mostraHelp(comandos);
            return;
        }

        logger.info("Executando o comando");

        Predicate<ComandoInterface> aceitaComando = (ci) -> ci.aceitoComando(args);
        Consumer<ComandoInterface> executaComando = (ci) -> ci.execute(args);

        comandos
                .stream()
                .filter(aceitaComando)
                .limit(1)
                .forEach(executaComando);

        Conexoes.terminaConexoes();
    }

    private static void mostraHelp(List<ComandoInterface> comandos) {
        final StringBuilder _out = new StringBuilder();
        _out.append("""
                Lista de comandos possíveis para trabalhar com o indexador de livos:
                
                """);
        comandos
                .stream()
                .map(c -> c.showHelp(new StringBuilder()).toString())
                .map(s->s+"\n")
                .forEach(_out::append);

        _out.append("""
                
                Feito por @spedison 11/2024
                """);

        System.out.println(_out);
    }
}