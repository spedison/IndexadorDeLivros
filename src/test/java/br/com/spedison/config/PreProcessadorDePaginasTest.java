package br.com.spedison.config;

import br.com.spedison.processadores.PreProcessadorDePaginas;
import br.com.spedison.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PreProcessadorDePaginasTest {


    @Test
    public void testProcess() {
        PreProcessadorDePaginas preProcessadorDePaginas = new PreProcessadorDePaginas();
        preProcessadorDePaginas.carregarSiglas();
        String original = """
                Eis que o CNJ 999,99% teve proble-
                mas com um Juiz de S.P .. ..
                Esses que estavam na B.A e ti-
                nham braços em R J. e Bra-
                sília.
                Na pag. 10 temos 50 Itens U.S. spedison-  
                123@gmail.com PA para A U.S P temos problemas B I.D?
                Com pre-Processador-
                DePaginas U FR.R. que tem como fim fina-
                lizando S.ES.T SP.para todos ABONG!
                No fim da linha SI  CONV COVID 19?
                http://www.terra.com.br/gmail.com/-
                p?agora_vai=1
                """;

        String esperado = """
                Eis que o Conselho_Nacional_de_Justiça 999,99% teve problemas com um Juiz de São_Paulo .
                Esses que estavam na Bahia e tinham braços em Rio_de_Janeiro e Brasília .
                Na Página 10 temos 50 Itens Estados_Unidos spedison123@gmail.com Pará para A Universidade_de_São_Paulo temos problemas Banco_Interamericano_de_Desenvolvimento ?
                Com pre-ProcessadorDePaginas Universidade_Federal_de_Roraima que tem como fim finalizando Serviço_Social_de_Transporte São_Paulo . para todos Associação_Brasileira_de_Organizações_Não_Governamentais !
                No fim da linha SI CONV Coronavírus_2019 19 ?
                http://www.terra.com.br/gmail.com/p?agora_vai=1
                """;

        String processado = preProcessadorDePaginas.processaPagina(original);
        processado = StringUtils.trimNasLinhas(processado, true);
        esperado = StringUtils.trimNasLinhas(esperado, false);

        System.out.println(original);
        System.out.println(processado);
        System.out.println(esperado);

        Assertions.assertEquals(esperado, processado);
    }
}