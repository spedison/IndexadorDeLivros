package br.com.spedison.comandos;

import br.com.spedison.config.Preposicoes;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.store.OutputStreamDataOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/*
public class ComandoCriaKnnDict implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoCriaKnnDict.class);
    static final private Preposicoes preposicao = new Preposicoes();

    private Word2Vec word2Vec;
    private static final int VECTOR_DIMENSIONS = 300; // ajuste conforme o tamanho do vetor
    private Builder<Long> fstBuilder;
    private DataOutputStream binOut;

    // Gera o embedding de uma página tirando a média dos embeddings de cada palavra
    private static INDArray getPageEmbedding(Word2Vec word2Vec, String page, int dimension) {
        String[] words = StringUtils.toToken(page, 1, null);
        INDArray pageEmbedding = Nd4j.zeros(dimension);

        int wordCount = 0;
        for (String word : words) {
            if (word2Vec.hasWord(word)) {
                pageEmbedding.addi(word2Vec.getWordVectorMatrix(word));
                wordCount++;
            }
        }

        if (wordCount > 0) {
            pageEmbedding.divi(wordCount); // Média dos vetores das palavras da página
        }
        return pageEmbedding;
    }

    private static Word2Vec trainWord2VecModel(List<String> sentences) {
        CollectionSentenceIterator sentenceIterator = new CollectionSentenceIterator(sentences);
        Word2Vec word2Vec = new Word2Vec.Builder()
                .minWordFrequency(1)
                .layerSize(300) // Dimensão dos vetores de embedding
                .windowSize(5)
                .tokenizerFactory(new DefaultTokenizerFactory())
                .iterate(sentenceIterator)
                .build();
        word2Vec.fit();
        return word2Vec;
    }

    @Override
    public void execute(String[] args) {

        Conexoes conexoes = new Conexoes();
        List<String> paginas =
                conexoes
                        .getEntityManager()
                        .createQuery("""
                                select
                                  p.conteudo
                                from
                                  Pagina p
                                order by p.livro.idLivro, p.numeroPagina
                                """, String.class).getResultList();

        log.info("Processando %d paginas".formatted(paginas.size()));

        //Quebra páginas em tokens para fazer o processamento
        Function<String, String[]> funcQuebraTokens = (s) -> StringUtils.toToken(s, 2, preposicao::notContains);
        List<String> palavrasParaTrabalhar =
                paginas
                        .stream()
                        .map(funcQuebraTokens)
                        .flatMap(Arrays::stream)
                        .toList();

        Word2Vec word2Vec = trainWord2VecModel(palavrasParaTrabalhar);
        int vectorDimension = word2Vec.getLayerSize();

        //Cria o diretório se não existir.
        new File(args[1]).mkdirs();

        //Apaga os arquivos se exitirem
        Path binFilePath = Paths.get(args[1], "knn_dict.bin");
        Path fstFilePath = Paths.get(args[1], "knn_dict.fst");
        Path txtFilePath = Paths.get(args[1], "knn_dict.txt");

        // Apaga arquivo Antigo.
        if (binFilePath.toFile().exists()) {
            binFilePath.toFile().delete();
            log.warn("Apagando Arquivo Antigo do knn_dict.bin");
        }
        if (fstFilePath.toFile().exists()) {
            fstFilePath.toFile().delete();
            log.warn("Apagando Arquivo Antigo do knn_dict.fst");
        }
        if (txtFilePath.toFile().exists()) {
            txtFilePath.toFile().delete();
            log.warn("Apagando Arquivo Antigo do knn_dict.txt");

        // Gera e salva os embeddings das páginas no arquivo binário
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(binFilePath.toFile()))) {
            for (String page : paginas) {
                INDArray embedding = getPageEmbedding(word2Vec, page, vectorDimension);
                // Grava cada valor do vetor no arquivo binário
                for (int i = 0; i < vectorDimension; i++) {
                    dos.writeFloat(embedding.getFloat(i));
                }
            }
            System.out.println("Arquivo knn_dict.bin criado com embeddings das páginas!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        KnnVectorDict.

                KnnVectorDict.build(binFilePath, fstFilePath);

    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -cria-knn-dict ou -ckd
                Descrição : Gera um dicionário de vetores KNN (K-Nearest Neighbors) a partir
                            de um treinamento de Word2Vec usando com base o banco mariadb de livros.
                Argumentos:
                           Path do arquivo knn
                Exemplo   : java -jar %s -cria-knn-dict ./index/
                """);
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 2 &&
                (args[0].equalsIgnoreCase("-cria-knn-dict") ||
                        args[0].equalsIgnoreCase("-ckd"));
    }
}
*/