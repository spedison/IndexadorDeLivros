package br.com.spedison.comandos.buscas.lucene;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.util.SystemUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ComandoBuscaLivroLucene implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoBuscaLivroLucene.class);

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

        diretorioIndexador = new File(args[2]);

        // Apaga todos os diretorios e arquivos que estao em args[1]
        if (!diretorioIndexador.exists()) {
            log.error("O diretório do índice não foi encontrada.");
            return;
        }

        start = new Date();
        log.info("Iniciando a Busca no Lucene.");
        buscaPaginas(args[2],Integer.parseInt(args[3]));
        end = new Date();

        System.out.printf("""
                        
                        Busca feita!
                        Tempo de execução: %f s
                        
                        """,
                (end.getTime() - start.getTime()) / 1000.0);
    }

    private void buscaPaginas(String textoPagina, int distancia) {
        try (FSDirectory directory = FSDirectory.open(diretorioIndexador.toPath());
             // Cria um DirectoryReader para ler o índice
             DirectoryReader reader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            // Usa o StandardAnalyzer para analisar a consulta
            Analyzer analyzer = new StandardAnalyzer();

            int numDocsLidos = reader.numDocs();

            //Busca de termo exato em um campo :
            // new TermQuery(new Term("content", "lucene"));

            /***
             * Para uma busca que tem vários itens de forma booleana
             * BooleanQuery.Builder builder = new BooleanQuery.Builder();
             * builder.add(new TermQuery(new Term("title", "java")), BooleanClause.Occur.MUST);
             * builder.add(new TermQuery(new Term("content", "lucene")), BooleanClause.Occur.SHOULD);
             * Query query = builder.build();
             */
            String [] palavrasBuscas = textoPagina.trim().split("[\s]");
            Query query;

            if (palavrasBuscas.length > 1) {
                query = new PhraseQuery(distancia, "conteudo", palavrasBuscas);
            } else {
                // Configura o parser para buscar no campo "conteudo". Query simples.
                QueryParser parser = new QueryParser("conteudo", analyzer);
                query = parser.parse(textoPagina);
            }

            // Executa a busca e limita os resultados para os 1E6 milhão de documentos primeiros
            TopDocs results = searcher.search(query, 1_000_000);
            System.out.println("Total de documentos encontrados: " + results.totalHits);
/*              Campos usados nesse índice.
                KeywordField("caminhoArquivo")
                KeywordField("idPaginaStr")
                IntField("idPagina")
                IntField("numeroPagina")
                TextField("conteudo")
* */
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("***", "***");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(250)); // Define o tamanho do fragmento exibido

            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                @SuppressWarnings("deprecation")
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


    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -buscar-livro-lucene ou -bll
                Descrição : Busca os livros que estão no índice do Lucene.
                Argumentos:
                            <diretório do indexador>
                            <expressão a localizar>
                            <distancia entre palavras>
                Exemplo   : java -jar %s -buscar-lucene /caminho/do/indexador  "switch java" 5
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 4 &&
                (args[0].equalsIgnoreCase("-buscar-lucene") ||
                        args[0].equalsIgnoreCase("-bl")) &&
                args[3].trim().matches("\\d+");
    }
}