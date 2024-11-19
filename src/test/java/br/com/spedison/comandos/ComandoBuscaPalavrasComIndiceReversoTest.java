package br.com.spedison.comandos;

import br.com.spedison.comandos.buscas.mariadb.ComandoBuscaPalavrasComIndiceReverso;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ComandoBuscaPalavrasComIndiceReversoTest {

    private static final Logger log = LoggerFactory.getLogger(ComandoBuscaPalavrasComIndiceReversoTest.class);

    @Test
    void montaConsultaUsandoBusca() {

        ComandoBuscaPalavrasComIndiceReverso comando = new ComandoBuscaPalavrasComIndiceReverso();
        String busca = "Gato Subiu no Telhado da cAsa";
        String[] buscaEsperado = {"gato", "subiu", "telhado", "casa"};
        String sql = comando.montaConsultaUsandoBusca(busca, 7);

        log.info("Consulta: \n{}", sql);
        String[] buscaEfetuadas = comando.getBusca();
        Assertions.assertEquals(4, buscaEfetuadas.length);
        Assertions.assertArrayEquals(buscaEsperado, buscaEfetuadas);

    }
}
