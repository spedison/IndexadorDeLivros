package br.com.spedison.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestAnalyser {

    @Test
    public void testAnalyser() {
        Analyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("nome", "Exemplo DE, TextO com ácento certô e éRRado!");
        try {
            tokenStream.reset();
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                if (!tokenStream.incrementToken()) break;
            } catch (IOException e) {
                Assertions.fail(e.getMessage());
                throw new RuntimeException(e);
            }
            System.out.println(tokenStream.getAttribute(CharTermAttribute.class).toString());
        }
        try {
            tokenStream.close();
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
