package br.com.spedison.processadores;

import br.com.spedison.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

class PreProcessadorDePaginasTest {

    @Test
    public void testaAjustaConteudoPagina() {
        String paginaSimulada = """
                Aqui está um teste pa-
                ra que eu seja :
                "Um faz e o outro vê".
                'mais um tes_
                te de aspas'
                Aqui temos uma solução :
                Vamos tentar e ao tentar pode-
                
                mos avaliar a solu-
                ção. Ao solucionar temos uma
                saída,
                
                pois a saida é nunca mais pa-
                rar.Não pararemos, nunca, ja-
                mais,  para que eles nunca sai-
                am impunes.
                 . Quem que-
                bra palavra nem sempre sabe o que que-
                bra.""";

        PreProcessadorDePaginas processaPaginas = new PreProcessadorDePaginas();
        processaPaginas.carregarSiglas();
        String resultadoEsperado = """               
                Aqui está um teste para que eu seja :
                " Um faz e o outro vê "  .
                ' mais um teste de aspas '
                Aqui temos uma solução :
                Vamos tentar e ao tentar podemos avaliar a solução . Ao solucionar temos uma
                saída ,
                pois a saida é nunca mais parar . Não pararemos , nunca , jamais , para que eles nunca saiam impunes . Quem quebra palavra nem sempre sabe o que quebra .""";
        String resultadoObtido = processaPaginas.processaPagina(paginaSimulada);
        resultadoObtido = StringUtils.trimNasLinhas(resultadoObtido);
        System.out.println(resultadoObtido);
        assertEquals(resultadoEsperado, resultadoObtido);
        assertTrue(true);
    }


    @Test
    public void testePagina2() {
        String paginaDeTeste = """
                AGRADECIMENTOS
                Nos últimos anos de minha vida, tenho procurado desenvolver e cultivar a “gratidão”.
                Quando resolvi sair da casa de minha mãe para cursar a graduação em História (UFMS/CPTL),
                no ano de 2004, dezenas de pessoas especiais passaram por minha vida. Cada uma delas, em
                algum momento, me orientou e estimulou para que continuasse na caminhada. Entretanto,
                o ato de agradecer pode se tornar “cru-
                el”, pois na medida em que lembro de algumas pessoas,
                posso esquecer-me de outras. Por isso, agradeço a todos e todas que, direta ou indiretamente,
                contribuíram para a construção deste livro, seja com contribuições acadêmicas ou com a com-
                panhia e conversas no dia a dia.
                Não posso abster-me de men-
                cionar algumas pessoas que foram mar-
                cantes no decorrer de
                toda essa “trajetória” (ainda em construção), fundamentais para a concretização dessa pesquisa
                e na minha vida enquanto ser social.""";

        String esperado = """
                AGRADECIMENTOS
                Nos últimos anos de minha vida , tenho procurado desenvolver e cultivar a  “ gratidão ”  .
                Quando resolvi sair da casa de minha mãe para cursar a graduação em História ( Universidade_Federal_de_Mato_Grosso_do_Sul /CPTL) ,
                no ano de 2004 , dezenas de pessoas especiais passaram por minha vida . Cada uma delas , em
                algum momento , me orientou e estimulou para que continuasse na caminhada . Entretanto ,
                o ato de agradecer pode se tornar  “ cruel ”  , pois na medida em que lembro de algumas pessoas ,
                posso esquecer-me de outras . Por isso , agradeço a todos e todas que , direta ou indiretamente ,
                contribuíram para a construção deste livro , seja com contribuições acadêmicas ou com a companhia e conversas no dia a dia .
                Não posso abster-me de mencionar algumas pessoas que foram marcantes no decorrer de
                toda essa  “ trajetória ”  (ainda em construção) , fundamentais para a concretização dessa pesquisa
                e na minha vida enquanto ser social .""";

        PreProcessadorDePaginas processaPaginas = new PreProcessadorDePaginas();
        processaPaginas.carregarSiglas();
        String resultadoObtido = processaPaginas.processaPagina(paginaDeTeste);
        resultadoObtido = StringUtils.trimNasLinhas(resultadoObtido);

        //assertEquals(esperado, resultadoObtido);

        System.out.println("(1)<<<" + resultadoObtido + ">>>");
        //System.out.println("(2)<<<" + paginaDeTeste + ">>>");
    }

    @Test
    public void testePagina3() {
        String paginaEntrada = """
                24
                fotografias, torna-se necessário procurar outras fontes que possam transmitir informações mais\s
                detalhadas acerca do que foi registrado em dado momento histórico.
                O método de análise sobre imagens fotográficas descrito na obra de Boris Kossoy se\s
                mostrou interessante na análise das fotografias em relação à prática da mística, bem como de\s
                outras imagens. O autor propõe um método interpretativo que consiste na “análise iconográ-
                fica” e “interpretação iconológica” (2001, p. 97-121), de que me vali para refletir para além\s
                daquilo que os meus olhos podiam ver, interpretando os diversos sentidos que são investidos na\s
                produção das imagens. Nas análises, sempre que possível, procurei entrecruzar as fontes. Nesta\s
                direção, informações como o conhecimento de quem produziu as imagens, o contexto em que\s
                foram elaboradas, o tipo de produção, as finalidades, onde foram divulgadas etc., são relevantes\s
                para auxiliar o pesquisador em sua análise.\s
                As fontes orais também foram fundamentais. Por meio dessas fontes, analisei como era\s
                desenvolvida a mística no acampamento Madre Cristina, e também como os sujeitos visuali-
                zavam essa prática e quais eram os significados e sentidos da mesma para suas vidas. Ao todo,\s
                foram analisadas vinte e quatro entrevistas10.\s
                Em relação aos sujeitos que vive-
                ram no acampamento Madre Cristina, as entrevistas\s
                foram realizadas em dois momentos distintos e com grupos que viveram no acampamento\s
                em tempos diferentes. Parte (doze entrevistas) dos sujeitos que compõem a rede de entrevistas\s
                foram entrevistados no ano de 2007, ainda quando cursava a graduação. Como as entrevistas\s
                contemplavam a problemática central do traba-
                lho, optei por utilizá-las. Outra parte (oito)\s
                das entrevistas realizei nos anos de 2008 e 2009, com sujeitos que viveram no acampamento\s
                Madre Cristina entre os anos de 2003 e 2004, ainda quan-
                do este se chamava Lagoão, e que se\s
                encontravam assentados no assentamento Estrela da Ilha, no município de Ilha Solteira – SP,\s
                criado em fevereiro de 2005. A opção por entrevistar sujeitos que viveram no acampamento\s
                pesquisado e que se encontravam assentados foi pro-
                posital, pois assim teria a possibilidade de\s
                observar se os sujeitos desenvolveriam a mística no espaço do acampamento, e se continuariam\s
                com sua prática no assentamento.\s
                Isso Acontece no PR e E quando puder se SE deixar em SP .
                10 Uma delas, do professor Rogério, militante do MST na região do Pontal do Paranapanema – SP, rea-
                lizada pela pesquisadora Maria Celma Borges. Outras três entrevistas realizadas por mim com dirigentes\s
                do Movimento em Andradina – SP, sendo duas com Renê, em momentos distintos, e uma com Lourival.                
                """;

        PreProcessadorDePaginas preProcessadorDePaginas = new PreProcessadorDePaginas();
        preProcessadorDePaginas.addSigla("PR", "Paraná");
        preProcessadorDePaginas.addSigla("SE", "Sergipe");
        preProcessadorDePaginas.addSigla("SP", "São Paulo");
        System.out.println(preProcessadorDePaginas.getSiglas().get(0).getExpressaoRegular());
        String ret = preProcessadorDePaginas.processaPagina(paginaEntrada);
        System.out.println(ret);
    }

    @Test
    public void testPagina4() {
        String pagina = """
                69
                cristãos em geral uma visão da mensagem evangélica que impele ao compromisso de cada um\s
                com seus irmãos, com a igualdade e com a justiça social” (1987, p. 35). Essas duas dimensões\s
                eram encaradas como se a luta dos trabalhadores fosse uma marcha que conduzia ao “Plano de\s
                Deus e ao Reino da Justiça”.
                Os sujeitos, em meio às lutas sociais, criavam representações sobre os acontecimentos e\s
                sobre si mesmos. Para a reelaboração de sentidos, recorriam a “matrizes discursivas”36 constituí-
                das, de onde extraíam “modalidades de nomeação do vivido” (SADER, 1988, p. 142). Grande\s
                parte dos movimentos sociais da época recorria às matrizes discursivas para (re) produzir seus\s
                discursos. No caso, o contato dos integrantes do MST com as CEBs e a CPT fez com que os\s
                seus princípios libertadores religiosos se configurassem como uma espécie de “matriz discursi-
                va” para os discursos que moviam as ações do Movimento. Nessa perspectiva, muitos discursos\s
                religiosos eram apropriados, objetivando orientar e legitimar suas ações. Sobre essa questão, é\s
                preciso observar que, por vezes, alguns sujeitos que integravam o MST também tinham repre-
                sentações tanto nas CEBs como na CPT, assim o diálogo se dava com mais recorrência. \s
                Diversos movimentos sociais, no campo e na cidade, emergidos em fins dos anos de\s
                1970 e na década seguinte, filtraram discursos de matrizes discursivas provindas de Pastorais\s
                Populares e de organismos progressistas dentro da Igreja. Assim, incorporaram novos elemen-
                tos aos seus discursos. É significativo ressaltar que os novos discursos incorporados não pode-
                riam ser estranhos à realidade dos sujeitos, caso contrário não produziriam efeitos. A partir\s
                de uma realidade particular, os movimentos sociais, inclusive o MST, incorporavam variados\s
                discursos provindos de matrizes inerentes à Igreja e, por vezes, também os ressignificavam para\s
                suas ações e anseios.\s
                meus_testes@testes.com.br      www.teste.com    http://www.bb.com.br  https://www1.meustestes.com.br
                Da mesma forma que nas CEBs, no que diz respeito à metodologia de ação, a CPT se\s
                utilizava de uma constante imbricação de conteúdos religiosos e políticos. As analogias bíblicas\s
                36 Ao pensar que as CEBs e a CPT, orientadas pela Teologia da Libertação, foram “matrizes discursivas”\s
                de muitos movimentos sociais, sobretudo, entre as décadas de 1970/80, aproprio-me das ideias de Sader,\s
                quando diz que as “matrizes discursivas” devem ser entendidas como “modos de abordagem da realida-
                de”, que implicam em diversas atribuições de significados, dependendo dos lugares e práticas materiais\s
                spedison@gmai..com.br   e   teste@gmail.com   ou maria@teste.com.br www.meu_teste.com.br http://www.testes.com.br\s
                onde são emitidas as falas (1988, p. 143).\s
                """;

        PreProcessadorDePaginas preProcessadorDePaginas = new PreProcessadorDePaginas();
        preProcessadorDePaginas.carregarSiglas();
        String paginaProcessada = preProcessadorDePaginas.processaPagina(pagina);

        System.out.println("<<<" + pagina + ">>>");
        System.out.println("<<<" + paginaProcessada + ">>>");
    }


    @Test
    public void testPagina5() {
        String pagina ="""
                  íNDICE
                Para além da necropolítica:...
                21
                                    
                nosso capitalismo produzindo crises cíclicas para manejo do poder. A Caixa de \\s
                
                
                Pandora, desobstruída em 2014, encorajou o homem médio (ibid) a vislumbrar\s
                com a participação na governamentalidade neoliberal, materializando-se na\s

                
                ascensão de Bolsonaro à cadeira presidencial. Uma figura desajustada aos símbolos\s
                estabelecidos pelas elites nacionais e – por consequência, transnacionais – que,\s
                em pouco ou nada, compartilhava dos seus repertórios de distinção. Elementos\s
                
                                        
                distantes deste universo, mas ameaçadoramente próximos da derrocada: uma\s
                heterogeneidade transversal de sujeitos livres brancos na ordem capitalista. Sua\s
                formação acadêmica, ofícios, experiências e ausência de herança (econômica ou\s
                social) se mostram permanentemente insuficientes quando comparadas às de\s
                                
                seus pares, a despeito das inúmeras diferenças entre eles. O único repertório\s
                comum acessado por este grupo é o do mérito pessoal como recompensa à\s
                labuta moral imposta pelo constrangimento incessante da própria precariedade.\s
                “É o tipo social incapaz de se manter em posições privilegiadas ou a elas ascender\s
                sem o peso das estruturas que reproduzem a tradição. Tendo de se submeter a\s
                mecanismos de competição e seleção […], o homem médio não pode prescindir\s
                da inércia para encontrar formas seguras de sobrevivência e satisfação das\s
                expectativas.” (ibid).                
                Não à toa, a fragilidade de suas bases sociais de integração condiciona a\s
                criação de recursos epistemológicos para fabulação de um mundo que se quer\s
                estático e construído a partir desta própria dinâmica. A busca por estabilidade\s
                e segurança atravessa a construção lógica de mundos facilmente classificáveis\s
                e preferencialmente inertes, mesmo arquitetado sob o signo do conflito e do\s
                esforço pessoal. São os elos frágeis de suas posições sociais que conformam o\s
                mundo fleumático desejado para si, ainda que apenas em suas especulações. Neste\s
                sentido forjam as narrativas da própria existência desestabilizando repertórios\s
                estabelecidos por outros grupos sociais. Ao criarem as próprias fronteiras\s
                – discursivas e imagéticas –, separam-se de condenados da terra, enquanto\s
                acreditam tocar o poder a partir da elaboração rumos das próprias existências.\s
                Seu lema é, portanto, exclusão para inclusão. Estas são apostas contínuas que\s                
                """;

        PreProcessadorDePaginas preProcessadorDePaginas = new PreProcessadorDePaginas();
        preProcessadorDePaginas.carregarSiglas();
        String paginaProcessada = preProcessadorDePaginas.processaPagina(pagina);
        System.out.println("<<<"+paginaProcessada+">>>");
    }

}