package br.com.spedison;

import br.com.spedison.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class StringUtilsTest {

    static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    @Test
    public void testRemoveAcentos() {
        String textoParaParse = " Aqui está! Um texto, que deve (certo) ser Separado  [colchetes] {chaves}  de qualquer, coisa errada! Ou não? QUem sabe: com um dia falou: \" Aqui vou \" \'Aqui Estou\'";
        String[] ret;
        ret = StringUtils.toToken(textoParaParse, 1, null);
        Assertions.assertEquals(27, ret.length);

        Assertions.assertEquals(ret[6], "certo");

        log.info(Arrays.toString(ret));

        Assertions.assertTrue(true);

        //assert textoSemAcentos.equals("AEIOU");
    }

}
