package br.com.spedison;

import br.com.spedison.comandos.*;
import br.com.spedison.comandos.apagar.ComandoApagarTodosLivros;
import br.com.spedison.comandos.apagar.ComandoApagarUmLivro;
import br.com.spedison.comandos.buscas.lucene.ComandoBuscaLivroLucene;
import br.com.spedison.comandos.buscas.lucene.ComandoBuscaPessoaLucene;
import br.com.spedison.comandos.buscas.mariadb.ComandoBuscaLivroComLike;
import br.com.spedison.comandos.buscas.mariadb.ComandoBuscaLivroComRegExp;
import br.com.spedison.comandos.buscas.mariadb.ComandoBuscaLivroTextualMariaDB;
import br.com.spedison.comandos.buscas.mariadb.ComandoBuscaPalavrasComIndiceReverso;
import br.com.spedison.comandos.index.*;
import br.com.spedison.comandos.outros.ComandoConectUsandoJDBC;
import br.com.spedison.comandos.outros.ComandoCriaPessoaFake;
import br.com.spedison.comandos.outros.ComandoLemarizaPalavras;
import br.com.spedison.comandos.outros.ComandoRadicalizarPalavras;
import br.com.spedison.processadores.Conexoes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class Main {


    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Criando todas os comandos.");

        List<ComandoInterface> comandos =
                List.of(
                        // Indexa os livor no banco MariaDB
                        new ComandoApagarEIndexar(),
                        // Apaga todos os Livros na base de dados.
                        new ComandoApagarTodosLivros(),
                        new ComandoApagarEIndexar(),
                        new ComandoIndexarFase1CarregarPDF(),
                        new ComandoIndexarFase2ProcessarPaginas(),
                        new ComandoIndexarFase3ProcessarParagrafosEPalavras(),
                        new ComandoIndexarFase4LuceneLivro(),
                        new ComandoIndexarFase5LucenePessoa(),
                        // Apaga um livro específico na base de dados.
                        new ComandoApagarUmLivro(),
                        // Usa comandos simples para conectar na base usando
                        // Somente JDBC. (Conta a quantidade de registros em
                        // todas as tabelas)
                        new ComandoConectUsandoJDBC(),
                        // Preenche uma tabela com milhares de registros para testes.
                        new ComandoCriaPessoaFake(),
                        // Busca de Livros usando Like.
                        new ComandoBuscaLivroComLike(),
                        // Busca de Livros usando o MariaDB em modo texto booleano, extendido e por palavras soltas.
                        new ComandoBuscaLivroTextualMariaDB(),
                        // Busca Livros com RegExp
                        new ComandoBuscaLivroComRegExp(),
                        // Busca usando banco de dados mariadb com dados quebrados por palavras.
                        new ComandoBuscaPalavrasComIndiceReverso(),
                        new ComandoBuscaPessoaLucene(),
                        new ComandoBuscaLivroLucene()
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
