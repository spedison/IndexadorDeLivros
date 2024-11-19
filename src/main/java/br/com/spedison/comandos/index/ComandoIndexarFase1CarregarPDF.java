package br.com.spedison.comandos.index;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.comandos.ComandosComunsUtils;
import br.com.spedison.processadores.*;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.Livro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class ComandoIndexarFase1CarregarPDF implements ComandoInterface {

    Logger log = LoggerFactory.getLogger(ComandoIndexarFase1CarregarPDF.class);

    private void processaUmLivro(String nomeArquivoPDF) {

        try (Conexoes conexoes = new Conexoes()) {

            ProcessaLivro processaLivro = new ProcessaLivro(conexoes, nomeArquivoPDF);
            Livro livro = processaLivro.adicionaOuAtualizaLivro(nomeArquivoPDF);

            // Esse livro já foi processado ?
            if (!livro.getPaginas().isEmpty()) {
                System.err.println(Instant.now() + " - Livro já foi processado : " + livro.getCaminhoCompletoArquivo());
                return;
            }

        } catch (Exception e) {
            log.error("Problemas ao abrir conexão : " + e.getMessage(), e);
        }

        log.info("Terminando o processamento do livro : " + nomeArquivoPDF);
    }

    @Override
    public void execute(String[] args) {

        File file = new File(args[2]);
        File[] files = file.listFiles(f -> {
            String a = f.toString();
            return a.toLowerCase().endsWith("pdf");
        });

        Consumer<String> peekMessage = (nomeArquivo) -> log.info("Processando Arquivo " + nomeArquivo);

        if (Objects.nonNull(files)) {
            Arrays.stream(files)
                    .map(File::toString)
                    .sequential()
                    .peek(peekMessage)
                    .forEach(this::processaUmLivro);
        }
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {

        return help.append("""
                Comando   : %s
                Descrição : Indexa os livros do diretorio forncecido para o MariaDB.
                Argumentos:
                            <diretório dos livros para indexar>
                Exemplo   : java -jar %s %s /caminho/dos/livros/para/indexar
                """.formatted(
                ComandosComunsUtils.montaParametrosIdx(1),
                SystemUtils.getJarUsado(),
                ComandosComunsUtils.montaParametrosExemplo(1)
                )
        );
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return ComandosComunsUtils.comparaFase(args, 1, 3);
    }
}