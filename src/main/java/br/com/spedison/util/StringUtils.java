package br.com.spedison.util;

import br.com.spedison.config.Preposicoes;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

public class StringUtils {

    static final Preposicoes preposicoes = new Preposicoes();

    public static String[] toToken(String s) {
        // Caracteres não imprimíveis comuns serão removidos.
        s = s.replaceAll("[^\\p{L}\\p{N}\\s!@#\\$%\\^&*()_+\\-=\\[\\]{}|;:'\",.<>?/\\\\~`]", " ");

        String expr = "'\\\"\\{\\}\\[\\]\\\\/*?!&=+\\(\\)";
        String repl = org.apache.commons.lang3.StringUtils.repeat(' ', expr.length());

        //Fiz isso para preservar números, e-mails e sites.
        s = org.apache.commons.lang3.StringUtils.replaceChars(s, expr, repl);
        s = s.replaceAll("([^\\p{L}\\d])([.:_,-])([^\\p{L}\\d])","$1 $3");
        s = s.replaceAll("([\\p{L}\\d])([.:_,-])([^\\p{L}\\d])","$1 $3");
        s = s.replaceAll("([^\\p{L}\\d])([.:_,-])([\\p{L}\\d])","$1 $3");
        //System.out.println("Depois : " + s);
        return s.split("[ \t]+");
    }

    public static String removeAcentos(String texto) {
        return org.apache.commons.lang3.StringUtils.stripAccents(texto);
    }

    public static String[] toToken(String s, int minLength, Predicate<String> filtro) {
        List<String> tokensList = Arrays.asList(toToken(s));
        if (Objects.isNull(filtro))
            return tokensList
                    .stream()
                    .map(String::trim)
                    .filter(t -> t.length() >= minLength)
                    .toArray(String[]::new);
        else
            return tokensList
                    .stream()
                    .map(String::trim)
                    .filter(t -> t.length() >= minLength)
                    .filter(filtro)
                    .toArray(String[]::new);
    }

    public static String unelinhasAjustandoPalavraQuebradas(String linhaprocessada) {

        StringBuffer ret = new StringBuffer();
        String[] linhas = linhaprocessada.split("\n");

        for (String linha : linhas) {

            // Pula as linhas em branco
            if (linha.isBlank())
                continue;

            unelinhasAjustandoPalavraQuebradas(linha.trim(), ret);
        }
        return ret.toString();
    }

    public static void unelinhasAjustandoPalavraQuebradas(String linhaprocessada, StringBuffer resultado) {
        linhaprocessada = linhaprocessada.trim();

        if (resultado.isEmpty()) {
            resultado.append(linhaprocessada);
            return;
        }

        String ultimoCaracter = resultado.substring(resultado.length() - 1, resultado.length());

        if (ultimoCaracter.equals("-") || ultimoCaracter.equals("_")) {
            //Apaga o ultimo caracter.
            resultado.delete(resultado.length() - 1, resultado.length());
        } else {
            resultado.append("\n");
        }

        resultado.append(linhaprocessada);
    }

    // Define o formato brasileiro
    static private NumberFormat formatter = NumberFormat.getInstance(Locale.of("pt", "BR"));

    public static String formataNumero(Long longValue) {
        return formatter.format(longValue);
    }

    public static String formataNumero(Integer intValue) {
        return formatter.format(intValue);
    }

    public static String[] splitComLimite(String str, String regex, int numMaxTokens) {
        return org.apache.commons.lang3.StringUtils.split(str, regex, numMaxTokens);
    }

    public static String getRegExpPontuacao() {
        return "[\\,\\.\\?\\!\\;\\ ]";
    }

    public static String trimNasLinhas(String linhas) {
        return trimNasLinhas(linhas,false);
    }

    public static String trimNasLinhas(String linhas, boolean removeLinhasEmBranco) {

        Predicate<String> filtro = (str) ->
                removeLinhasEmBranco ? !str.trim().isBlank() : true;

        return
                org.apache.commons.lang3.StringUtils
                        .joinWith("\n",
                                Arrays
                                        .stream(linhas.split("\n"))
                                        .map(String::trim)
                                        .filter(filtro)
                                        .toArray());
    }


}
