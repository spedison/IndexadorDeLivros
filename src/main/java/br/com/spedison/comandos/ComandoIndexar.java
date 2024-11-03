package br.com.spedison.comandos;

import br.com.spedison.Main;
import br.com.spedison.processadores.*;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.Livro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;

public class ComandoIndexar implements ComandoInterface {

    Logger log = LoggerFactory.getLogger(ComandoIndexar.class);

    private void processaUmLivro(String nomeArquivoPDF) {

        Conexoes conexoes = new Conexoes();

        ProcessaLivro processaLivro = new ProcessaLivro(conexoes, nomeArquivoPDF);
        Livro livro = processaLivro.adicionaOuAtualizaLivro(nomeArquivoPDF);

        // Esse livro já foi processado ?
        if (!livro.getPaginas().isEmpty()) {
            System.err.println(Instant.now() + " - Livro já foi processado : " + livro.getCaminhoCompletoArquivo());
            return;
        }

        ProcessaPaginas processadorPaginas = new ProcessaPaginas(conexoes, livro);
        processadorPaginas.processaArquivo(nomeArquivoPDF);
        log.error("Processado as Páginas do livro : " + livro.getCaminhoCompletoArquivo());

        ProcessaParagrafos processadorParagrafos = new ProcessaParagrafos(
                conexoes, livro);
        processadorParagrafos.processaParagrafos();
        log.info("Processado as Parágrafos do livro : " + livro.getCaminhoCompletoArquivo());

        ProcessaPalavras processadorPalavras = new ProcessaPalavras(conexoes, livro);

        processadorPalavras.processaPalavras();
        log.info("Processado as Palavras do livro : " + livro.getCaminhoCompletoArquivo());

        // Marca o final o instante final do processamento.
        livro.setDataHoraFinal(Instant.now());
        Long segundosGastosNoProcessamento = Duration.between(livro.getDataHoraInicial(),
                livro.getDataHoraFinal()).getSeconds();
        livro.setTempoGastoSegundos(segundosGastosNoProcessamento);

        conexoes.beginTransaction();
        conexoes.grava(livro);
        conexoes.commitTransaction();

        conexoes.terminaConexao();

        log.info("Terminando o processamento do livro : " + nomeArquivoPDF);
    }

    @Override
    public void execute(String[] args) {

        File file = new File(args[1]);
        File[] files = file.listFiles(f -> {
            String a = f.toString();
            return a.toLowerCase().endsWith("pdf");
        });

        Consumer<String> peekMessage = (nomeArquivo) -> {
            log.info("Processando Arquivo " + nomeArquivo);
        };

        Arrays.stream(files)
                .map(File::toString)
                .parallel()
                .peek(peekMessage)
                .forEach(this::processaUmLivro);
    }

    @Override
    public StringBuffer showHelp(StringBuffer help) {
        return help.append("""
                Comando   : -idx ou -indexar
                Descrição : Indexa os livros do diretório fornecido.
                Argumentos: Nome do diretório com os PDFs para indexar
                Exemplo   : java -jar %s -idx livros_para_indexar/
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length >= 2 &&
                (args[0].equalsIgnoreCase("-index") ||
                        args[0].equalsIgnoreCase("-idx") ||
                        args[0].equalsIgnoreCase("-indexar"));
    }
}
