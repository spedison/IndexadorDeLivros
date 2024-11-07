package br.com.spedison.comandos;

import br.com.spedison.processadores.Conexoes;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.PaginaComLivro;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public class ComandoIndexaLucene implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoIndexaLucene.class);

    File diretorioIndexador;
    Date start;
    Date end;

    public void indexarPaginas(List<PaginaComLivro> pages, Path indexDir) {

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try (FSDirectory directory = FSDirectory.open(indexDir);
             IndexWriter indexWriter = new IndexWriter(directory, config)
        ) {
            // Se for adicionar deve-se alterar aqui :
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // Indexa cada página como um documento separado
            for (PaginaComLivro paginaComLivro : pages) {
                Document doc = new Document();
                // Adicionado no índice.
                //Usados para localizar por valor exato o documento.
                //newExactQuery for matching a value.
                //newSetQuery for matching any of the values coming from a set.
                //newSortField for matching a value.
                doc.add(new KeywordField("caminhoArquivo", paginaComLivro.getCaminhoArquivo(), Field.Store.YES));
                //Exite vários tipos de campos, exemplo LongField, etc.
                doc.add(new KeywordField("idPaginaStr", paginaComLivro.leIdPaginaStr(), Field.Store.YES));
                doc.add(new IntField("idPagina", paginaComLivro.getIdPagina(), Field.Store.YES));
                doc.add(new IntField("numeroPagina", paginaComLivro.getNumeroPagina(), Field.Store.YES));
                doc.add(new TextField("conteudo", paginaComLivro.getConteudo(), Field.Store.YES));
                indexWriter.addDocument(doc);
            }
        } catch (IOException e) {
            log.error("Erro ao indexar: {}", e.getMessage());
            e.printStackTrace();
        }

        log.info("Indexação concluída!");
    }

    private List<PaginaComLivro> listaLivros() {
        try (Conexoes conexoes = new Conexoes()) {
            return
                    conexoes
                            .getEntityManager()
                            .createQuery(
                                    """
                                               SELECT
                                                 p.idPagina, p.numeroPagina,
                                                 p.conteudo, p.livro.caminhoArquivo
                                               FROM
                                                  Pagina p
                                               order by
                                                  p.livro.idLivro, p.numeroPagina
                                            """, PaginaComLivro.class)
                            .getResultList();
        } catch (IOException e) {
            log.error("Erro ao listar documentos: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void execute(String[] args) {

        diretorioIndexador = new File(args[1]);

        // Apaga todos os diretorios e arquivos que estao em args[1]
        if (!ajusteDiretorio())
            return;

        log.info("Indexando livros em: " + diretorioIndexador.getAbsolutePath());

        List<PaginaComLivro> livros = listaLivros();
        log.info("Carregados os livros do banco de dados.");

        start = new Date();
        log.info("Iniciando a indexação no Lucene.");
        indexarPaginas(livros, diretorioIndexador.toPath());
        end = new Date();

        System.out.printf("""               
                        Indexação concluida!
                        Tempo de execução: %f s
                        
                        """,
                (end.getTime() - start.getTime()) / 1000.0);

        listaEstadoIndice(livros.size());
    }

    private void listaEstadoIndice(int numDocsAdicionados) {
        try (FSDirectory directory = FSDirectory.open(diretorioIndexador.toPath());
             // Cria um DirectoryReader para ler o índice
             DirectoryReader reader = DirectoryReader.open(directory)) {

            int numDocsLidos = reader.numDocs();

            if (numDocsAdicionados != numDocsLidos) {
                log.error("Erro ao verificar estado do índice. Número de documentos adicionados ({}) não confere com o número de documentos lidos ({})", numDocsAdicionados, numDocsLidos);
            } else {
                log.info("Índice está consistente. Número de documentos adicionados ({}) confere com o número de documentos lidos ({})", numDocsAdicionados, numDocsLidos);
            }

        } catch (IOException e) {
            log.error("Erro ao verificar ínidice : " + e.getMessage());
        }
    }

    private boolean ajusteDiretorio() {
        try {
            if (diretorioIndexador.exists()) {
                FileUtils.deleteDirectory(diretorioIndexador);
                log.info("Todos os diretórios e arquivos foram apagados!");
            }
            return diretorioIndexador.mkdirs();
        } catch (IOException e) {
            log.error("Erro ao apagar diretórios e arquivos: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -indexar-lucene ou -il
                Descrição : Indexa os livros que estão no MariaDB em um banco de dados Lucene.
                Argumentos:
                            <diretório do indexador>
                Exemplo   : java -jar %s -indexar-lucene -create /caminho/do/indexador  
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 2 &&
                (args[0].equalsIgnoreCase("-indexar-lucene") ||
                        args[0].equalsIgnoreCase("-il"));

    }
}