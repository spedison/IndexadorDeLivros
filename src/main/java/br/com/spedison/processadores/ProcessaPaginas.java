package br.com.spedison.processadores;

import br.com.spedison.util.LePDF;
import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Pagina;
import br.com.spedison.vo.PaginaComLivro;
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
import java.util.List;
import java.util.function.Consumer;

public class ProcessaPaginas implements Consumer<LePDF.PaginaLida> {


    private final Conexoes conexoes;
    private final File diretorioIndexador;

    int contaPaginas;
    static final private Logger log = LoggerFactory.getLogger(ProcessaPaginas.class);

    Livro livro;
    int commitBook = 50;
    PreProcessadorDePaginas preProcessadorDePaginas;

    public ProcessaPaginas(Conexoes conexoes, Livro livro) {
        this.livro = livro;
        this.conexoes = conexoes;
        preProcessadorDePaginas = new PreProcessadorDePaginas();
        preProcessadorDePaginas.carregarSiglas();
        diretorioIndexador = null;
    }

    public ProcessaPaginas(Conexoes conexoes, File diretorioIndexador) {
        this.conexoes = conexoes;
        this.diretorioIndexador = diretorioIndexador;
    }


    public void processaArquivo(String nomeArquivoPDF) {
        conexoes.beginTransaction();
        LePDF.lerPDF(nomeArquivoPDF, this);
        conexoes.grava(livro);
        conexoes.commitTransaction();
    }


    public void apagaPaginasDoLivro() {
        //conexoes.beginTransaction();
        conexoes.getEntityManager().createQuery(
                        """
                                delete
                                from
                                    Pagina p
                                where
                                    p.livro.idLivro = :livro
                                """)
                .setParameter("livro", livro.getIdLivro())
                .executeUpdate();
        //conexoes.commitTransaction();
    }


    public String ajustaConteudoPagina(String conteudoPDF) {
        if (conteudoPDF.isBlank())
            return conteudoPDF;

        String ret = preProcessadorDePaginas.processaPagina(conteudoPDF);
        return ret;
    }



    @Override
    public void accept(LePDF.PaginaLida paginaLida) {

        log.info("Processando página %d do arquivo %s ".formatted(paginaLida.getNumeroPagina(), livro.getCaminhoArquivo()));

        contaPaginas++;
        Pagina pagina = new Pagina();
        pagina.setLivro(livro);
        pagina.setNumeroPagina(paginaLida.getNumeroPagina());
        pagina.setConteudo(ajustaConteudoPagina(paginaLida.getConteudoPagina()));
        pagina.setConteudoOriginal(paginaLida.getConteudoPagina());

        livro.getPaginas().add(pagina);
        conexoes.grava(pagina);

        // A cada x registros, é realizado um commit.
        if (contaPaginas % commitBook == 0) {
            conexoes.commitTransaction();
            conexoes.beginTransaction();
        }
    }


    public List<PaginaComLivro> listaPaginasComLivros() {

        return
                conexoes
                        .getEntityManager()
                        .createQuery(
                                """
                                           SELECT
                                             p.idPagina, p.numeroPagina,
                                             p.conteudo, p.conteudoOriginal, p.livro.caminhoArquivo
                                           FROM
                                              Pagina p
                                           order by
                                              p.livro.idLivro, p.numeroPagina
                                        """, PaginaComLivro.class)
                        .getResultList();
    }

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

    public void listaEstadoIndice(int numDocsAdicionados) {
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
}
