package br.com.spedison.comandos.outros;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.processadores.ProcessadorNLP;
import br.com.spedison.processadores.dto.ResultadoRadical;
import br.com.spedison.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ComandoRadicalizarPalavras implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoRadicalizarPalavras.class);

    @Override
    public void execute(String[] args) {
        ProcessadorNLP nlp = new ProcessadorNLP();
        ResultadoRadical radical = nlp.getRadical(args[1]);

        log.info("Entrada : " + args[1]);
        log.info("Saída   : " + radical.getRadical());
    }


    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando para radicalizar a palavras: -radicalizar <frase> ou -r <frase>
                Exemplo: java -jar %s -radicalizar "programar no fim de semana é interessante."
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 2 && (
                args[0].equalsIgnoreCase("-radicalizar") ||
                        args[0].equalsIgnoreCase("-r"));
    }
}
