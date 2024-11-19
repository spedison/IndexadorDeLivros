package br.com.spedison.processadores;

import br.com.spedison.processadores.dto.ResultadoRadical;
import br.com.spedison.processadores.dto.ResultadoTokens;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class ProcessadorNLPTest {

    private static Logger log = LoggerFactory.getLogger(ProcessadorNLPTest.class);

    @Test
    public void testGetTokensParagrafo(){
        ProcessadorNLP processadorNLP = new ProcessadorNLP();
        String texto = """
                "Aqui está um
                teste para tokenizar.
                Espero que venha 2 ou mais
                linhas. Mas
                se vier     3 ou 4 não
                terá problema.
                """;
        ResultadoTokens tokens = processadorNLP.getTokensParagrafo(texto);
        assertEquals(3, tokens.getCount(), 3);
        //assertNull(tokens.getOriginal());
        assertEquals(3, tokens.getTokens().size());
    }


    @Test
    public void testGettextoRadicais(){
        ProcessadorNLP processadorNLP = new ProcessadorNLP();
        ResultadoRadical tokens = processadorNLP.getRadical("Aqui está um \nteste para tokenizar. \t\nEspero que venha 2 ou mais \nlinhas. Mas \rse vier \t3 ou 4 não \nterá problema.");
        log.info("Resultado " + tokens);
        assertNull(tokens.getOriginal());
    }


}