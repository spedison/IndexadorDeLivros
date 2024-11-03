package br.com.spedison.comandos;

public interface ComandoInterface {
    void execute(String [] args);
    StringBuffer showHelp(StringBuffer help);
    boolean aceitoComando(String [] args);
}
