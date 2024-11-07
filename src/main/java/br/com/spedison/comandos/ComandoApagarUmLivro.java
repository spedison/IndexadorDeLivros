package br.com.spedison.comandos;

import br.com.spedison.processadores.*;
import br.com.spedison.util.SystemUtils;
import br.com.spedison.vo.Livro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComandoApagarUmLivro implements ComandoInterface {

    static final Logger log = LoggerFactory.getLogger(ComandoApagarUmLivro.class);

    @Override
    public void execute(String[] args) {

        try (Conexoes conexoes = new Conexoes()) {

            ProcessaLivro procLivro = new ProcessaLivro(conexoes, args[1]);

            Livro livro = procLivro.getLivro();
            if (livro == null) {
                log.error(String.format(args[1]));
            }

            procLivro.apagaLivro();
            log.info(String.format(args[1]));

        } catch (Exception e) {
            log.error("Erro ao apagar livro: {}", e.getMessage());
        }
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -apagar-livro ou -al
                Descrição : Apaga um livro da base de dados.
                Argumentos: O nome do livro a ser apagado.
                Exemplo   : java -jar %s -apagar-livro "Livro_do_Cinema.pdf"
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 2 &&
                (args[0].equalsIgnoreCase("-apagar-livro") ||
                        args[0].equalsIgnoreCase("-al"));
    }
}
