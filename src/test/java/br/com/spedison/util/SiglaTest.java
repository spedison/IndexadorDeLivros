package br.com.spedison.util;

import br.com.spedison.processadores.PreProcessadorDePaginas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SiglaTest {


    @Test
    public void testSigla() {

        PreProcessadorDePaginas pp = new PreProcessadorDePaginas();
        pp.carregarSiglas();
        List<PreProcessadorDePaginas.Sigla> siglas = pp.getSiglas();
        siglas.stream().forEach(System.out::println);
        Assertions.assertTrue(siglas.size() > 0);

    }

}
