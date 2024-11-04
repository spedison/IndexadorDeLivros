package br.com.spedison.processadores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessaPaginasTest {

    @Test
    public void testaAjustaConteudoPagina(){
        String paginaSimulada = """
                Aqui está um teste para
                que eu seja :
                "Um faz e o outro vê".
                Aqui temos uma solução :
                Vamos tentar e ao tentar pode-
                
                mos avaliar a solução.
                Ao solucionar temos uma saída,
                
                pois a saida é nunca mais parar.
                Não pararemos, nunca, jamais,
                para que eles nunca saim imunes.
                """;

        ProcessaPaginas processaPaginas = new ProcessaPaginas(null,null);
        String resultadoEsperado = """
                Aqui está um teste para que eu seja : "Um faz e o outro vê".
                Aqui temos uma solução : Vamos tentar e ao tentar podemos avaliar a solução.
                Ao solucionar temos uma saída, pois a saida é nunca mais parar.
                Não pararemos, nunca, jamais, para que eles nunca saim imunes.""";
        String resultadoObtido = processaPaginas.ajustaConteudoPagina(paginaSimulada);
        assertEquals(resultadoEsperado, resultadoObtido);
        assertTrue(true);
    }

}