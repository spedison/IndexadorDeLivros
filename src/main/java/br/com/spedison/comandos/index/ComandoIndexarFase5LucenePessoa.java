package br.com.spedison.comandos.index;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.comandos.ComandosComunsUtils;
import br.com.spedison.processadores.Conexoes;
import br.com.spedison.processadores.ProcessaPessoaFake;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.PessoaFake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ComandoIndexarFase5LucenePessoa implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoIndexarFase5LucenePessoa.class);

    Date start;
    Date end;

    @Override
    public void execute(String[] args) {

        File diretorioIndexador = new File(args[2]);

        long accTempoIdx = 0;

        // Apaga todos os diretorios e arquivos que estao em args[1]
        if (!ComandosComunsUtils.ajusteDiretorio(diretorioIndexador))
            return;

        try (Conexoes conexoes = new Conexoes()) {

            ProcessaPessoaFake ppf = new ProcessaPessoaFake(conexoes, diretorioIndexador);

            log.info("Indexando PessoasFake em: " + diretorioIndexador.getAbsolutePath());

            start = new Date();

            for (char ch = 'a'; ch <= 'z'; ch++) {

                log.info("Processando pessoas com a letra %c".formatted(ch));
                List<PessoaFake> pessoas = ppf.listaPessoasFake(ch);
                log.info("Carregados os livros do banco de dados.");

                log.info("Iniciando a indexação no Lucene.");
                long t1 = System.currentTimeMillis();
                ppf.indexarPessoasFake(pessoas, ch == 'a');
                accTempoIdx += (System.currentTimeMillis() - t1);
            }
            end = new Date();

            log.info("Avaliando consistência índice");
            long contaRegistros = ppf.contaPessoasFake();
            ppf.listaEstadoIndice(contaRegistros);

        } catch (IOException e) {
            log.error("Erro ao indexar pessoas fake: {}", e.getMessage());
        }

        System.out.printf("""               
                        --------------------------
                        Indexação concluida!
                        Tempo de execução: %f s
                        Tempo da Indexação Lucene : %f s
                        --------------------------
                        """,
                (end.getTime() - start.getTime()) / 1000.0,
                accTempoIdx / 1000.0);
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                        Comando   : %s
                        Descrição : Indexa as pessoas fake que estão no MariaDB em um banco de dados Lucene.
                        Argumentos:
                                    <diretório do indexador>
                        Exemplo   : java -jar %s %s /caminho/do/indexador
                        """.formatted(
                        ComandosComunsUtils.montaParametrosIdx(5),
                        SystemUtils.getJarUsado(),
                        ComandosComunsUtils.montaParametrosExemplo(5)
                )
        );
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return ComandosComunsUtils.comparaFase(args, 5, 3);
    }
}