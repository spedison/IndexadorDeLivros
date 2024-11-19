package br.com.spedison.processadores;

import br.com.spedison.config.Preposicoes;
import br.com.spedison.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PreProcessadorDePaginas {

    @Data
    @NoArgsConstructor
    public static class Sigla {
        private String nome;
        private String valor;

        public Sigla(String nome, String valor) {
            this.nome = nome.trim();
            this.valor = valor.trim();
        }

        public String getValorAjustado() {
            return valor.trim().replaceAll("[ \t]", "_");
        }

        public String getExpressaoRegular() {
            StringBuilder expressa = new StringBuilder();
            expressa.append("(?<![\\p{L}\\d])"); // Lookbehind para garantir que "PR" não seja precedido por uma letra ou dígito
            for (char ch : getNome().toCharArray()) {
                expressa.append("[%c][\\.\\s]?".formatted(ch));
            }
            expressa.append("(?![\\p{L}\\d])"); // Lookahead para garantir que "PR" não seja seguido por uma letra ou dígito
            return expressa.toString();
        }
    }

    static private List<Sigla> siglas = new ArrayList<>();
    static private Logger log = LoggerFactory.getLogger(PreProcessadorDePaginas.class);

    public void ordenaSiglas(){
        log.info("Ordenando siglas...");
        siglas.sort(
                (o1, o2) -> {
                    int comp = Integer.compare(o2.getNome().length(), o1.getNome().length());
                    if(comp == 0)
                        return o1.getNome().compareTo(o2.getNome());
                    return comp;
                }
        );
    }

    public void carregarSiglas() {

        if (!siglas.isEmpty()) {
            return;
        }

        try (InputStream inputStream = Preposicoes.class.getResourceAsStream("/br/com/spedison/conf/sigla.properties");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank())
                    continue;
                String[] parts = StringUtils.splitComLimite(line.trim(), "[=]", 2);
                siglas.add(new Sigla(parts[0], parts[1]));
            }
            // Ordena para processar primeiro as Siglas Maiores e depois as menores.
            ordenaSiglas();
        } catch (Exception e) {
            log.error("Problemas ao ler configuração : " + e.getMessage());
        }
    }

    public List<Sigla> getSiglas() {
        return siglas;
    }


    public void addSigla(String nome, String valor) {
        siglas.add(new Sigla(nome, valor));
    }


    private String processaSiglas(String texto) {
        for (Sigla sigla : siglas) {
            Pattern pattern = Pattern.compile(sigla.getExpressaoRegular());
            do {
                Matcher matcher = pattern.matcher(texto);
                if (matcher.find()) {
                    String matchedText = matcher.group();
                    boolean ret = matchedText.matches(StringUtils.getRegExpPontuacao() + "$");
                    if (ret)
                        matchedText = matchedText.substring(0, matchedText.length() - 2);
                    texto = texto.replace(matchedText, " " + sigla.getValorAjustado() + " ");
                } else {
                    break;
                }
            } while (true);
        }
        return ajustaTrechoAposSiglas(texto);
    }


    public String processaPagina(String texto) {

        // Tire linhas vazias e faz o trim nas linhas.
        texto = StringUtils.trimNasLinhas(texto,true);

        // Une palavras quebras das linhas.
        texto = StringUtils.unelinhasAjustandoPalavraQuebradas(texto);

        // Esses caras abaixo não serão processados para não descarecterizar os dados.
        // Regex para identificar URLs                data numericas                    email            numeros                      Sites      http(s)           ftps.
        Pattern urlPattern = Pattern.compile("([0-9]{1,2}/[0-9]{1,2}/[0-9]{2,4}|[A-Za-z0-9.]@\\S+|\\b[0-9]+[.,-_ aA]?[0-9]*\\b|www.\\S+|https?://(www.)?\\S+|ftp://\\S+)");
        Matcher urlMatcher = urlPattern.matcher(texto);

        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (lastEnd != -1) {
            boolean encontrado = urlMatcher.find();
            // Texto antes da URL
            String beforeUrl = texto.substring(lastEnd, encontrado ? urlMatcher.start() : texto.length());
            result.append(processaSiglas(beforeUrl));
            // URL intacta
            result.append(encontrado ? urlMatcher.group() : "");
            lastEnd = encontrado ? urlMatcher.end() : -1;
        }


        return result
                .toString()
                // Tira caracteres não identificados (como carinhas) e caracteres utf-8 que não deveriam estar no texto
                .replaceAll("[^\\p{L}\\p{N}\\s!@#\\$%\\^&*()_+\\-=\\[\\]{}|;:'\",.<>?/\\\\~`”“]", " ")
                .replaceAll("[\\ ]{2,}"," ") // retira espaços duplos
                .replaceAll("(([\\ ]+|^)[pP][\\s]*[.])([\\s]*[0-9]+)"," Página $3 ") // P. xxx vira Página xxx
                .replaceAll("[\"]"," \" ")  // Envolve " com espaços
                .replaceAll("[']"," ' ")    // Envolve ' com espaços
                .replaceAll("[”]"," ” ")    // idem com aspas duplas diferentes
                .replaceAll("[“]", " “ ")   // Idem com aspas duplas diferentes.
                ;
    }

    private String ajustaTrechoAposSiglas(String texto) {
        return texto
                .replaceAll("([\\s]+[.][\\s]+)([a-zçóáàéíú])", " $2") // tira pontos seguidos de números e letras minúsculas.
                .replaceAll("([.][\\s ]?)+[.]", ".") // Tira os pontos seguintes separados ou não por espaços
                .replaceAll("([.?!;,])", " $1 ") // Coloca espaços entre as pontuações
                ;
    }
}
