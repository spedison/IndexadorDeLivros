package br.com.spedison.util;

import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static String[] toToken(String s) {
        String expr = "'\\\"\\{\\}\\[\\]\\\\/*.:,?!&-_=+\\(\\)";
        String repl = org.apache.commons.lang3.StringUtils.repeat(' ',expr.length());
        //System.out.println("Antes  : " + s);
        s = org.apache.commons.lang3.StringUtils.replaceChars(s,expr,repl);
        //System.out.println("Depois : " + s);
        return s.split("[" + " \t" + "]");
    }

    public static String removeAcentos(String texto) {
        return org.apache.commons.lang3.StringUtils.stripAccents(texto);
    }

    public static String[] toToken(String s, int minLength) {
        List<String> tokensList = Arrays.asList(toToken(s));
        return tokensList.stream().map(String::trim).filter(t -> t.length() >= minLength).toArray(String[]::new);
    }

}
