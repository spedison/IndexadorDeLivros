package br.com.spedison.comandos;

import br.com.spedison.processadores.Conexoes;
import br.com.spedison.util.SystemUtils;
import jakarta.persistence.Lob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ComandoApagarTodosLivros implements ComandoInterface {

    private static final Logger log = LoggerFactory.getLogger(ComandoApagarTodosLivros.class);

    private boolean confirmaExecucao() {
        System.out.println("Confirma a execução do comando para apagar todos os livros? (S/N)");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String linha = br.readLine();
            return linha.toLowerCase().startsWith("s");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void execute(String[] args) {
        // Confirma a execução do comando

        if (!confirmaExecucao()) return;

        try (Conexoes conexoes = new Conexoes()) {
            conexoes.beginTransaction();
            conexoes.getEntityManager().createQuery("delete from Palavra ").executeUpdate();
            conexoes.getEntityManager().createQuery("delete from Paragrafo ").executeUpdate();
            conexoes.getEntityManager().createQuery("delete from Pagina ").executeUpdate();
            conexoes.getEntityManager().createQuery("delete from Livro ").executeUpdate();
            conexoes.commitTransaction();
        } catch (Exception e) {
            log.error("Problemas ao realizar os comandos de deleção na base: ", e);
        }
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -apagar-todos
                Descrição : Apaga todos os livros da base de dados.
                Argumentos: Não possui argumentos.
                Exemplo   : java -jar %s -apagar-todos
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 1 &&
                (args[0].equalsIgnoreCase("-apagar-todos") ||
                        args[0].equalsIgnoreCase("-at") ||
                        args[0].equalsIgnoreCase("-del"));
    }
}