package br.com.spedison.comandos;

public interface ComandoInterface {
    void execute(String [] args);
    StringBuilder showHelp(StringBuilder help);
    boolean aceitoComando(String [] args);
}
