package br.com.spedison.comandos.buscas.lucene;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.util.StringUtils;
import br.com.spedison.util.SystemUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class ComandoBuscaPessoaLucene implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoBuscaPessoaLucene.class);

    File diretorioIndexador;
    Date start;
    Date end;


    private void imprimeDoc(Document doc, float score, String strDestaque) {
        /****
         * KeywordField("id_txt", "%d".formatted(pessoaFake.getId())
         * Existe vários tipos de campos, exemplo LongField, etc.
         * TextField("nome", pessoaFake.getNome()
         * TextField("endereco", pessoaFake.getEndereco()
         * IntField("idade", pessoaFake.getIdade()
         * IntField("id_num", pessoaFake.getId()
         **/

        System.out.printf("""          
                        --------------------------------------------------------------------------------- 
                        ID: %-8s - Score:%5.2f - %-40s - [%3d] - [%-70s]
                        <             DESTAQUE          <
                        >>%-100s<<
                        """,
                doc.getField("id_txt").stringValue(),
                score,
                doc.getField("nome").stringValue(),
                doc.getField("idade").numericValue().intValue(),
                doc.getField("endereco").stringValue(),
                strDestaque);
    }


    @Override
    public void execute(String[] args) {

        diretorioIndexador = new File(args[1]);
        String palavras = args[2];

        int distancia = (args.length == 5 ? Integer.parseInt(args[3]) : -1);

        int quantidade = (args.length == 5 ? Integer.parseInt(args[4]) : Integer.parseInt(args[3]));

        // Apaga todos os diretorios e arquivos que estao em args[1]
        if (!diretorioIndexador.exists()) {
            log.error("Diretório {} não existe. Crie esse ínidice", diretorioIndexador.getAbsolutePath());
            return;
        }

        start = new Date();
        log.info("Iniciando a Busca no Lucene.");

        buscaPaginas(palavras, distancia, quantidade);
        end = new Date();

        System.out.printf("""
                        
                        Busca feita!
                        Tempo de execução: %f s
                        
                        """,
                (end.getTime() - start.getTime()) / 1000.0);
    }

    @SuppressWarnings("unchecked")
    private void buscaPaginas(String textoPagina,
                              int distancia,
                              int quantidade) {
        try (FSDirectory directory = FSDirectory.open(diretorioIndexador.toPath());
             // Cria um DirectoryReader para ler o índice
             DirectoryReader reader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            // Usa o StandardAnalyzer para analisar a consulta
            Analyzer analyzer = new StandardAnalyzer();

            int numDocsLidos = reader.numDocs();
            String[] palavrasBuscas = textoPagina.split("[\\s]");

            boolean temTermosLogicos =
                    Arrays
                            .stream(palavrasBuscas)
                            .map(String::toLowerCase)
                            .map(String::trim)
                            .filter(s -> s.matches("^(and|or|not)$"))
                            .count() >= 1;


            Query query = null;

            if (distancia == -1 || temTermosLogicos) {
                // Configura o parser para buscar no campo "content"
                QueryParser parser = new QueryParser("nome", analyzer);
                query = parser.parse(textoPagina);
            } else {
                PhraseQuery.Builder builder = new PhraseQuery.Builder();
                builder.setSlop(distancia);
                log.info("Distancia = " + distancia);
                log.info("Palavras  = " + Arrays.toString(palavrasBuscas));
                for (String palavraBusca : palavrasBuscas) {
                    if (palavraBusca.isBlank()) continue;
                    builder.add(new Term("nome", palavraBusca.trim().toLowerCase()));
                }
                query = builder.build();
            }

            // Executa a busca e limita os resultados para os 1 milhões primeiros
            TopDocs results = searcher.search(query, 1_000_000);

            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("***", "***");
            Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(50)); // Define o tamanho do fragmento exibido

            int contagem = 0;
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                @SuppressWarnings("deprecation")
                var tokenStream = TokenSources.getAnyTokenStream(reader, scoreDoc.doc, "nome", analyzer);
                String highlightedText = highlighter.getBestFragment(tokenStream, doc.get("nome"));
                imprimeDoc(doc, scoreDoc.score, highlightedText);
                if (contagem++ > quantidade)
                    break;
            }

            System.out.printf(
                    """
                            ------------------------------
                            - Existem     %10s Registros
                            - Exibidos    %10s Registros
                            - Localizados %10s Registros
                            ------------------------------
                            """, StringUtils.formataNumero(numDocsLidos),
                    StringUtils.formataNumero(contagem),
                    StringUtils.formataNumero(results.totalHits.value()));

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
                Comando   : -buscar-lucene-pessoa ou -blp
                Descrição : Busca os livros que estão no índice do Lucene.
                Argumentos:
                            diretório do indexador
                            expressão a localizar
                            >> distancia entre palavras << ---Opcional---
                            quantidade de registros para mostrar
                Exemplo   : java -jar %s -buscar-lucene-pessoa /caminho/do/indexador  "joao lemos" 5 30
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length > 3 &&
                (args[0].equalsIgnoreCase("-buscar-lucene-pessoa") ||
                        args[0].equalsIgnoreCase("-blp")) &&

                args[3].trim().matches("\\d+") &&

                (args.length == 4 ||
                        (args.length == 5 &&
                                args[4].trim().matches("\\d+")
                        )
                );
    }
}