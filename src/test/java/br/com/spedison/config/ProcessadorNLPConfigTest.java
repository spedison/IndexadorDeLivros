package br.com.spedison.config;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class ProcessadorNLPConfigTest {

    static Logger log = LoggerFactory.getLogger(ProcessadorNLPConfigTest.class);

    @Test
    public void testContrutor(){
        ProcessadorNLPConfig config = new ProcessadorNLPConfig();
        assertNotNull(config.getServidor());
        assertNotNull(config.getLematizar());
        assertNotNull(config.getRadical());
        assertNotNull(config.getTokenizar());
        log.info(config.toString());
    }

}