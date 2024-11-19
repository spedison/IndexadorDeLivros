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

public class ComandoIndexarFase2ProcessarPaginas implements ComandoInterface {

    Logger log = LoggerFactory.getLogger(ComandoIndexarFase2ProcessarPaginas.class);

    Conexoes conexoes;

    private void processaUmLivro(Livro livro) {

        ProcessaPaginas pp = new ProcessaPaginas(conexoes, livro);
        ProcessaParagrafos ppara = new ProcessaParagrafos(conexoes, livro);
        ProcessaPalavras ppala = new ProcessaPalavras(conexoes, livro);

        conexoes.beginTransaction();
        // Remove todas as páginas do livro.
        livro.getPaginas().clear();
        conexoes.grava(livro);

        // Vou apagar as palavras já processadas
        ppala.apagaPalavrasDoLivro();

        // Vou apagar os paragrafos já processados
        ppara.apagaParagrafosDoLivro();

        // Processa e carrega as páginas para dentro do banco.
        pp.apagaPaginasDoLivro();

        conexoes.commitTransaction();

        // Carrega os pdfs
        pp.processaArquivo(livro.getCaminhoCompletoArquivo());

        log.error("Processado as Páginas do livro : " + livro.getCaminhoCompletoArquivo());
        log.info("Terminando o processamento das páginas do livro : " + livro.getCaminhoCompletoArquivo());
    }

    @Override
    public void execute(String[] args) {

        List<Livro> livros;
        try (Conexoes conexao = new Conexoes()) {
            this.conexoes = conexao;

            ProcessaLivro processaLivro = new ProcessaLivro(conexao, (Livro) null);
            livros = processaLivro.getLivros();

            Consumer<Livro> peekMessage = (livro) -> log
                    .info("Processando Arquivo " + livro.getCaminhoCompletoArquivo());

            livros
                    .stream()
                    .sequential()
                    .peek(peekMessage)
                    .forEach(this::processaUmLivro);

        } catch (Exception e) {
            log.error("Erro ao abrir a conexão com o banco de dados : " + e.getMessage(), e);
            return;
        }
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : %s
                Descrição : Indexa os livros do MariaDB (Fase de leitura dos arquivos).
                Argumentos:
                            <Sem argumentos>
                Exemplo   : java -jar %s %s
                """.formatted(
                        ComandosComunsUtils.montaParametrosIdx(2),
                        SystemUtils.getJarUsado(),
                        ComandosComunsUtils.montaParametrosExemplo(2)
                )
        );
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return ComandosComunsUtils.comparaFase(args, 2, 2);
    }
}