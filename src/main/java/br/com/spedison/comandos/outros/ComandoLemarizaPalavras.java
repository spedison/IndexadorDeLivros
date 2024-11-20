package br.com.spedison.comandos.outros;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.processadores.ProcessadorNLP;
import br.com.spedison.processadores.dto.ResultadoLematizador;
import br.com.spedison.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ComandoLemarizaPalavras implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoLemarizaPalavras.class);

    @Override
    public void execute(String[] args) {
        ProcessadorNLP nlp = new ProcessadorNLP();
        ResultadoLematizador lematizador = nlp.getLemanizacao(args[1]);

        log.info("Entrada : " + args[1]);
        log.info("Saída   : " + lematizador.getLematizado());
    }


    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando para lemmatizar palavras: -lematize <frase>
                Exemplo: java -jar %s -lematize "programar no fim de semana é interessante."
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 2 && (
                args[0].equalsIgnoreCase("-lematize") ||
                        args[0].equalsIgnoreCase("-l"));
    }
}
