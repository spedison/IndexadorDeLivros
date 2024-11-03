package br.com.spedison;

import br.com.spedison.comandos.*;
import br.com.spedison.processadores.Conexoes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


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
                        new ComandoBuscaLivroTextualMariaDB()
                );

        if (args.length == 0){
            mostraHelp(comandos);
            return;
        }

        logger.info("Executando o comando");
        comandos
                .stream()
                .filter(c -> c.aceitoComando(args))
                .limit(1)
                .forEach(c -> c.execute(args));

        Conexoes.terminaConexoes();
    }

    private static void mostraHelp(List<ComandoInterface> comandos) {
        final StringBuffer _out = new StringBuffer();

        _out.append("""
                Lista de comandos possiveis para trabalhar com o indexador de livos:
                
                """);
        comandos
                .stream()
                .map(c -> c.showHelp(new StringBuffer()).toString())
                .map(s->s+"\n")
                .forEach(_out::append);

        _out.append("""
                
                Feito por @spedison 11/2024
                """);

        System.out.println(_out.toString());
    }
}