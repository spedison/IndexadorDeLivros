package br.com.spedison.comandos;

import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.PaginaComLivro;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ComandoBuscaLucene implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoBuscaLucene.class);

    File diretorioIndexador;
    Date start;
    Date end;


    private void imprimeDoc(Document doc, float score, String strDestaque){
        System.out.printf("""           
                        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                        ID: %d  -   Score   : %.2f
                        Arquivo      : %s
                        Número Pagina: %d
                        Conteúdo     :
                        
                        %s
                        
                        < < < < < < < < < < < < < < < < <
                        <             DESTAQUE          <
                        < < < < < < < < < < < < < < < < <
                        %s
                        > > > > > > > > > > > > > > > > > 
                        
                        ------------------------------------------
                        """,
                doc.getField("idPagina").numericValue().intValue(),
                score,
                doc.get("caminhoArquivo"),
                doc.getField("numeroPagina").numericValue().intValue(),
                doc.get("conteudo"),
                strDestaque);
    }


    @Override
    public void execute(String[] args) {

        diretorioIndexador = new File(args[1]);

        // Apaga todos os diretorios e arquivos que estao em args[1]
        if (!verificaDiretorio())
            return;

        start = new Date();
        log.info("Iniciando a Busca no Lucene.");
        buscaPaginas(args[2]);
        end = new Date();

        System.out.printf("""
                        
                        Busca feita!
                        Tempo de execução: %f s
                        
                        """,
                (end.getTime() - start.getTime()) / 1000.0);
    }

    private void buscaPaginas(String textoPagina) {
        try (FSDirectory directory = FSDirectory.open(diretorioIndexador.toPath());
             // Cria um DirectoryReader para ler o índice
             DirectoryReader reader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            // Usa o StandardAnalyzer para analisar a consulta
            Analyzer analyzer = new StandardAnalyzer();

            int numDocsLidos = reader.numDocs();

            // Configura o parser para buscar no campo "content"
            QueryParser parser = new QueryParser("conteudo", analyzer);
            Query query = parser.parse(textoPagina);

            // Executa a busca e limita os resultados para os 50 primeiros
            TopDocs results = searcher.search(query, 50);
            System.out.println("Total de documentos encontrados: " + results.totalHits);

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("***", "***");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(50)); // Define o tamanho do fragmento exibido

            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                var tokenStream = TokenSources.getAnyTokenStream(reader, scoreDoc.doc, "conteudo", analyzer);
                String highlightedText = highlighter.getBestFragment(tokenStream, doc.get("conteudo"));
                imprimeDoc(doc,scoreDoc.score, highlightedText);
            }

        } catch (IOException e) {
            log.error("Erro ao verificar ínidice : " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (InvalidTokenOffsetsException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verificaDiretorio() {
            if (!diretorioIndexador.exists()) {
                log.error("Diretório de Índice não existe.");
                return false;
            }
            return true;
        }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -buscar-lucene ou -bl
                Descrição : Busca os livros que estão no índice do Lucene.
                Argumentos:
                            <diretório do indexador>
                            <expressão a localizar>
                Exemplo   : java -jar %s -buscar-lucene /caminho/do/indexador  "switch java"
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 3 &&
                (args[0].equalsIgnoreCase("-buscar-lucene") ||
                        args[0].equalsIgnoreCase("-bl"));
    }
}