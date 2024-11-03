package br.com.spedison.util;

import br.com.spedison.config.Preposicoes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class StringUtils {

    static final Preposicoes preposicoes = new Preposicoes();

    public static String[] toToken(String s) {
        String expr = "'\\\"\\{\\}\\[\\]\\\\/*.:,?!&-_=+\\(\\)";
        String repl = org.apache.commons.lang3.StringUtils.repeat(' ', expr.length());
        //System.out.println("Antes  : " + s);
        s = org.apache.commons.lang3.StringUtils.replaceChars(s, expr, repl);
        //System.out.println("Depois : " + s);
        return s.split("[" + " \t" + "]");
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

    public static boolean uneLinhas(String linha1, String linha2, StringBuffer resultado) {
        linha1 = linha1.trim();
        linha2 = linha2.trim();

        if (linha1.endsWith("-") || linha1.endsWith("_")) {
            //Apaga o ultimo caracter.
            resultado.delete(resultado.length() - 1, resultado.length());
            resultado.append(linha2);
            return true;
        }

        if (linha1.endsWith(",") ||
                linha1.endsWith(";") ||
                linha1.endsWith(":")||
                preposicoes.linhaTerminaComPreposicao(linha1)) {
            resultado.append(" ");
            resultado.append(linha2);
            return true;
        }

        return false;
    }
}