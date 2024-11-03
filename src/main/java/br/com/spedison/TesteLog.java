package br.com.spedison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TesteLog {
    private static final Logger logger = LoggerFactory.getLogger(TesteLog.class);

    public static void main(String[] args) {
        logger.error("Teste de log simples");
    }
}
