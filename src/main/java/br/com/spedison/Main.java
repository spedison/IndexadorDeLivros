package br.com.spedison;

import br.com.spedison.processadores.Conexoes;
import br.com.spedison.processadores.ProcessaPaginas;
import br.com.spedison.processadores.ProcessaPalavras;
import br.com.spedison.processadores.ProcessaParagrafos;
import br.com.spedison.vo.Livro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    void processaUmLivro(String nomeArquivoPDF) {

        Conexoes conexoes = new Conexoes();

        ProcessaPaginas processadorPaginas = new ProcessaPaginas(conexoes);
        processadorPaginas.processaArquivo(nomeArquivoPDF);

        ProcessaParagrafos processadorParagrafos = new ProcessaParagrafos(
                processadorPaginas.getLivroAtual(),
                conexoes);
        processadorParagrafos.processaParagrafos();


        ProcessaPalavras processadorPalavras = new ProcessaPalavras(
                processadorPaginas.getLivroAtual(),
                conexoes);
        processadorPalavras.processaPalavras();

        conexoes.terminaConexao();

        System.out.println(Instant.now() + " - Terminando o processamento do livro : " + nomeArquivoPDF);

    }

    public static void main(String[] args) {

        File file = new File("/mnt/dados/git/IndexaLivros/livros_para_indexar/");
        File[] files = file.listFiles();

        Main main = new Main();

        Consumer<String> peekMessage = (nomeArquivo) -> {
            System.out.println("Processando Arquivo " + nomeArquivo);
        };

        Arrays.stream(files)
                .map(File::toString)
                .parallel()
                .peek(peekMessage)
                .forEach(main::processaUmLivro);
    }
}