package br.com.spedison.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegExpTest {

    @Test
    public void testAspasComRegExp() {

        String strTeste = """
                          <">
                          <'>
                          <a>
                          <b>
                          <'a'>
                          <"b">
                          """;

        String strTesteEsperado = """
                                  < " >
                                  < ' >
                                  <a>
                                  <b>
                                  < ' a ' >
                                  < " b " >
                                  """;

        String teste = strTeste
                .replaceAll("[\"]"," \" ")
                .replaceAll("[']"," ' ")
                ;

        System.out.println(strTeste);
        System.out.println(teste);

        Assertions.assertEquals(strTesteEsperado, teste);
    }
}
