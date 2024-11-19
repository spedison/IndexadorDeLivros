package br.com.spedison.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class StringUtilsTest {

    static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    @Test
    public void testUnelinhasAjustandoPalavraQuebradas() {
        String[] varEntrada = """
                Aqui é uma linha que será,
                junto com essa.
                Mas diferente dessa outra, ficará
                só pois não tem evidência para uní-
                -las. Essa linha sim.
                """.split("[\n]");
        StringBuffer buf = new StringBuffer();
        buf.append(varEntrada[0].trim());
        for (int i = 1; i < varEntrada.length; i++) {
            StringUtils.unelinhasAjustandoPalavraQuebradas(varEntrada[i], buf);
        }

        String strCompara = """
                Aqui é uma linha que será, 
                junto com essa.
                Mas diferente dessa outra, ficará
                só pois não tem evidência para uní-las. Essa linha sim.""";
        Assertions.assertEquals(strCompara, buf.toString());

        System.out.println(buf);
    }

    @Test
    public void testRemoveAcentos() {
        String textoParaParse = " Aqui está! Um texto, que deve (certo) ser Separado  [colchetes] {chaves}  de qualquer, coisa errada! Ou não? QUem sabe: com um dia falou: \" Aqui vou \" 'Aqui Estou'";
        String[] ret;
        ret = StringUtils.toToken(textoParaParse, 1, null);

        System.out.println(ret.length);
        System.out.println(Arrays.toString(ret));

        Assertions.assertEquals(27, ret.length);

        Assertions.assertEquals(ret[6], "certo");

        log.info(Arrays.toString(ret));

        Assertions.assertTrue(true);

        //assert textoSemAcentos.equals("AEIOU");
    }

    @Test
    public void testTokenize() {
        String t = "esse é um teste, de token, usando vírgulas, para testar;";
        String [] tt = StringUtils.toToken(t);
        System.out.println(Arrays.toString(tt));
    }

}
