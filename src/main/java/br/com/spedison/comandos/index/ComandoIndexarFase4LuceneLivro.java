package br.com.spedison.comandos.index;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.comandos.ComandosComunsUtils;
import br.com.spedison.processadores.Conexoes;
import br.com.spedison.processadores.ProcessaPaginas;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.PaginaComLivro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ComandoIndexarFase4LuceneLivro implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoIndexarFase4LuceneLivro.class);

    File diretorioIndexador;
    Date inicioLucene;
    Date end;


    @Override
    public void execute(String[] args) {

        diretorioIndexador = new File(args[2]);

        // Apaga todos os diretorios e arquivos que estao em args[1]
        if (!ComandosComunsUtils.ajusteDiretorio(diretorioIndexador))
            return;

        try (Conexoes conexoes = new Conexoes()) {

            log.info("Indexando livros em: " + diretorioIndexador.getAbsolutePath());
            ProcessaPaginas pp = new ProcessaPaginas(conexoes, diretorioIndexador);

            Date inicioProc = new Date();
            List<PaginaComLivro> livros = pp.listaPaginasComLivros();
            log.info("Carregados os livros do banco de dados.");

            inicioLucene = new Date();
            log.info("Iniciando a indexação no Lucene.");
            pp.indexarPaginas(livros, diretorioIndexador.toPath());
            end = new Date();

            System.out.printf("""               
                            Indexação concluida!
                            Tempo de execução: %f s
                            Tempo Indexacao Lucene : %f s
                            """,
                    (end.getTime() - inicioProc.getTime()) / 1000.0,
                    (end.getTime() - inicioLucene.getTime()) / 1000.0
            );

            pp.listaEstadoIndice(livros.size());
        } catch (Exception e) {
            log.error("Problemas na indexação do livro :: " + e.getMessage(), e);
        }
    }


    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                        Comando   : %s
                        Descrição : Indexa os livros que estão no MariaDB em um banco de dados Lucene.
                        Argumentos:
                                    <diretório do indexador>
                        Exemplo   : java -jar %s %s /caminho/do/indexador
                        """.formatted(
                        ComandosComunsUtils.montaParametrosIdx(4),
                        SystemUtils.getJarUsado(),
                        ComandosComunsUtils.montaParametrosExemplo(4)
                )
        );
    }


    @Override
    public boolean aceitoComando(String[] args) {
        return ComandosComunsUtils.comparaFase(args, 4, 3);
    }
}