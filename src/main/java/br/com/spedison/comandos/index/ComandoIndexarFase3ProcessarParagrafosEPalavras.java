package br.com.spedison.comandos.index;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.comandos.ComandosComunsUtils;
import br.com.spedison.processadores.*;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.Livro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class ComandoIndexarFase3ProcessarParagrafosEPalavras implements ComandoInterface {

    Logger log = LoggerFactory.getLogger(ComandoIndexarFase3ProcessarParagrafosEPalavras.class);

    private void processaUmLivro(Livro livro) {

        try (Conexoes conexoes = new Conexoes()) {
            ProcessaParagrafos processadorParagrafos = new ProcessaParagrafos(conexoes, livro);
            ProcessaPalavras processaPalavras = new ProcessaPalavras(conexoes, livro);

            conexoes.beginTransaction();
            processaPalavras.apagaPalavrasDoLivro();
            processadorParagrafos.apagaParagrafosDoLivro();
            conexoes.commitTransaction();

            processadorParagrafos.processaParagrafos();
            log.info("Processado as Parágrafos do livro : " + livro.getCaminhoCompletoArquivo());

            processaPalavras.processaPalavras();
            log.info("Processado as Palavras do livro : " + livro.getCaminhoCompletoArquivo());

            ProcessaLivro processaLivro = new ProcessaLivro(conexoes, livro);
            processaLivro.atualizaHorarioProcessamento();
        } catch (Exception e) {
            log.error("Erro ao abrir conexão : " + e.getMessage(), e);
        }

        log.info("Terminando o processamento dos parágrafos do livro : " + livro.getCaminhoCompletoArquivo());
    }

    @Override
    public void execute(String[] args) {

        List<Livro> livros;
        try (Conexoes conexao = new Conexoes()) {
            ProcessaLivro processaLivro = new ProcessaLivro(conexao, (Livro) null);
            livros = processaLivro.getLivros();
        } catch (Exception e) {
            log.error("Erro ao abrir a conexão com o banco de dados : " + e.getMessage(), e);
            return;
        }

        Consumer<Livro> peekMessage = (livro) -> log.info("Processando Arquivo " + livro.getCaminhoCompletoArquivo());

        livros
                .stream()
                .sequential()
                .peek(peekMessage)
                .forEach(this::processaUmLivro);
    }


    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                        Comando   : %s
                        Descrição : Indexa os paragrafos dos livros do banco
                        Argumentos:
                                   <Sem Parâmetros>
                        Exemplo   : java -jar %s %s
                        """.formatted(
                        ComandosComunsUtils.montaParametrosIdx(3),
                        SystemUtils.getJarUsado(),
                        ComandosComunsUtils.montaParametrosExemplo(3)
                )
        );
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return ComandosComunsUtils.comparaFase(args, 3, 2);
    }
}
