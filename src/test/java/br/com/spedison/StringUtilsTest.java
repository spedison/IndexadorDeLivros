package br.com.spedison;

import br.com.spedison.util.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class StringUtilsTest {

    @Test
    public void testRemoveAcentos() {
        String textoParaParse = " Aqui está! Um texto, que deve (certo) ser Separado  [colchetes] {chaves}  de qualquer, coisa errada! Ou não? QUem sabe: com um dia falou: \" Aqui vou \" \'Aqui Estou\'";
        String[] ret;
        ret = StringUtils.toToken(textoParaParse,1) ;

        System.out.println(Arrays.toString(ret));
        //assert textoSemAcentos.equals("AEIOU");
    }

}
