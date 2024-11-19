package br.com.spedison.processadores;

import br.com.spedison.vo.PessoaFake;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ProcessaPessoaFake {

    static private Logger log = LoggerFactory.getLogger(ProcessaPessoaFake.class);

    private Conexoes conexoes;
    private File diretorioIndexador;

    public ProcessaPessoaFake(Conexoes conexoes, File diretorioIndexador) {
        this.conexoes = conexoes;
        this.diretorioIndexador = diretorioIndexador;
    }

    public List<PessoaFake> listaPessoasFake(char letraInicio) {

        return
                conexoes
                        .getEntityManager()
                        .createQuery(
                                """
                                        SELECT
                                          p
                                        FROM
                                          PessoaFake p
                                        where p.nome like :filtro
                                        """,
                                PessoaFake.class)
                        .setParameter("filtro", letraInicio + "%")
                        .getResultList();
    }

    public void listaEstadoIndice(long numDocsAdicionados) {
        try (FSDirectory directory = FSDirectory.open(diretorioIndexador.toPath());
             // Cria um DirectoryReader para ler o índice e o abre
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



    public long contaPessoasFake() {
        return conexoes
                .getEntityManager()
                .createQuery("""
                                    select count(p)
                                    from PessoaFake p
                                    """, Long.class)
                .getSingleResult();
    }

    public void indexarPessoasFake(List<PessoaFake> pessoasFake, boolean create) throws IOException {

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        Path indexDir = diretorioIndexador.toPath();

        int conta = 0;

        try (FSDirectory directory = FSDirectory.open(indexDir);
             IndexWriter indexWriter = new IndexWriter(directory, config)
        ) {
            // Se for adicionar deve-se alterar aqui :
            if (create)
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            else
                config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);

            // Indexa cada página como um documento separado
            for (PessoaFake pessoaFake : pessoasFake) {
                Document doc = new Document();
                // Adicionado no índice.
                //Usados para localizar por valor exato o documento.
                //newExactQuery for matching a value.
                //newSetQuery for matching any of the values coming from a set.
                //newSortField for matching a value.
                doc.add(new KeywordField("id_txt", "%d".formatted(pessoaFake.getId()), Field.Store.YES));
                //Exite vários tipos de campos, exemplo LongField, etc.
                doc.add(new TextField("nome", pessoaFake.getNome(), Field.Store.YES));
                doc.add(new TextField("endereco", pessoaFake.getEndereco(), Field.Store.YES));
                doc.add(new IntField("idade", pessoaFake.getIdade(), Field.Store.YES));
                doc.add(new IntField("id_num", pessoaFake.getId(), Field.Store.YES));

                // Campo para ordenação
                doc.add(new SortedDocValuesField("nome_ordenacao", new BytesRef(pessoaFake.getNome())));
// Opcional: Adicionar um campo não analisado para busca exata
                doc.add(new StringField("nome_busca_exata", pessoaFake.getNome(), Field.Store.YES));

                indexWriter.addDocument(doc);

                if (conta++ % 1000 == 0 && conta > 0)
                    System.out.print(".");

                if (conta % 10000 == 0 && conta > 0)
                    System.out.print("*");
            }
        } catch (IOException e) {
            log.error("Erro ao indexar: {}", e.getMessage());
            e.printStackTrace();
        }

        log.info("Indexação concluída!");
    }
}
