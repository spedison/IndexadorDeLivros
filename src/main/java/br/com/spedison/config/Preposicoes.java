package br.com.spedison.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class Preposicoes extends LinkedList<String> {

    static private Logger logger = LoggerFactory.getLogger(Preposicoes.class);

    public Preposicoes() {
        carregaArquivo();
    }

    private void carregaArquivo() {
        try (InputStream inputStream = Preposicoes.class.getResourceAsStream("/br/com/spedison/conf/preposicoes-portugues.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                this.add(line.trim());
            }
        } catch (Exception e) {
            logger.error("Problemas ao ler configuração : " + e.getMessage());
        }
    }

    public boolean linhaTerminaComPreposicao(String linha) {
        linha = linha.toLowerCase().trim();
        for (String item : this) {
            if (linha.endsWith(" " + item) ||
                    linha.endsWith("," + item) ||
                    linha.endsWith(", " + item) ||
                    linha.endsWith(". " + item) ||
                    linha.endsWith("." + item) ||
                    linha.endsWith(":" + item) ||
                    linha.endsWith(": " + item)
            )
                return true;
        }
        return false;
    }
}