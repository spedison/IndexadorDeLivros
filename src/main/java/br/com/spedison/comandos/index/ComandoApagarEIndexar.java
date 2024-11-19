package br.com.spedison.comandos.index;

import br.com.spedison.comandos.apagar.ComandoApagarTodosLivros;
import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.processadores.*;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.Livro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class ComandoApagarEIndexar implements ComandoInterface {

    Logger log = LoggerFactory.getLogger(ComandoApagarEIndexar.class);

    private void processaUmLivro(String nomeArquivoPDF) {

        try (Conexoes conexoes = new Conexoes()) {
            ProcessaLivro processaLivro = new ProcessaLivro(conexoes, nomeArquivoPDF);
            Livro livro = processaLivro.adicionaOuAtualizaLivro(nomeArquivoPDF);

            ProcessaPaginas processadorPaginas = new ProcessaPaginas(conexoes, livro);
            processadorPaginas.processaArquivo(nomeArquivoPDF);
            log.error("Processado as Páginas do livro : " + livro.getCaminhoCompletoArquivo());

            ProcessaParagrafos processadorParagrafos = new ProcessaParagrafos(conexoes, livro);
            processadorParagrafos.processaParagrafos();
            log.info("Processado as Parágrafos do livro : " + livro.getCaminhoCompletoArquivo());

            ProcessaPalavras processadorPalavras = new ProcessaPalavras(conexoes, livro);
            processadorPalavras.processaPalavras();
            log.info("Processado as Palavras do livro : " + livro.getCaminhoCompletoArquivo());

            // Marca o final o instante final do processamento.
            processaLivro.atualizaHorarioProcessamento();
        } catch (Exception e) {
            log.error("Problemas ao abrir conexão com banco : " + e.getMessage(), e);
            return;
        }

        log.info("Terminando o processamento do livro : " + nomeArquivoPDF);
    }

    @Override
    public void execute(String[] args) {

        File file = new File(args[1]);
        File[] files = file.listFiles(f -> {
            String a = f.toString();
            return a.toLowerCase().endsWith("pdf");
        });

        if (Objects.isNull(files) || files.length == 0) {
            log.error("Nenhum livro para indexar. Processo terminado. Livros não foram apagados.");
            return;
        }

        // Apagando todos os livros.
        ComandoApagarTodosLivros apagarTodosLivros = new ComandoApagarTodosLivros(false);
        apagarTodosLivros.execute(null);

        Consumer<String> peekMessage = (nomeArquivo) -> log.info("Processando Arquivo " + nomeArquivo);

        Arrays.stream(files)
                .map(File::toString)
                .sequential()
                .peek(peekMessage)
                .forEach(this::processaUmLivro);
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -apidx ou -apagar-e-indexar ou -apagar-indexar
                Descrição : Apaga o banco e indexa os livros do diretório fornecido passando por todas as fases.
                Argumentos: Nome do diretório com os PDFs para indexar
                Exemplo   : java -jar %s -apidx  livros_para_indexar/
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 2 &&
                (args[0].equalsIgnoreCase("-apidx") ||
                        args[0].equalsIgnoreCase("-apagar-e-indexar") ||
                        args[0].equalsIgnoreCase("-apagar-indexar"));
    }
}