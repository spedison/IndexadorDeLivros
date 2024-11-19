package br.com.spedison.config;

import br.com.spedison.util.StringUtils;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Getter
@ToString
public class ProcessadorNLPConfig {

    final static Logger log = LoggerFactory.getLogger(ProcessadorNLPConfig.class);

    private String servidor;
    private String lematizar;
    private String radical;
    private String tokenizar;
    private int quantidadeDeRepeticoes;

    public ProcessadorNLPConfig() {
        // Le o arquivo de properties dentro do Jar para pegar as mesmas propriedades.
        try (InputStream inputStream =
                     ProcessadorNLPConfig
                             .class
                             .getResourceAsStream("/br/com/spedison/conf/lematizador.properties");
             BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.isBlank())
                    continue;

                String[] parts = StringUtils.splitComLimite(line, "=",2);

                if (parts.length != 2)
                    continue;

                String key = parts[0].trim();
                String value = parts[1].trim();
                switch (key.toLowerCase()) {
                    case "servidor" -> servidor = value;
                    case "host" -> servidor = value;
                    case "lemmatizar" -> lematizar = value;
                    case "lematizar" -> lematizar = value;
                    case "stem" -> radical = value;
                    case "radical" -> radical = value;
                    case "tokenizar" -> tokenizar = value;
                    case "token" -> tokenizar = value;
                    case "repeticoes" -> quantidadeDeRepeticoes = Integer.parseInt(value);
                    case "repeticao" -> quantidadeDeRepeticoes = Integer.parseInt(value);
                    default -> throw new IllegalArgumentException("Propriedade inválida: " + key);
                }
            }
        } catch (Exception e) {
            log.error("Problemas ao ler configuração : " + e.getMessage());
        }
    }

    public String getUrlToken(){
        return getServidor() + getTokenizar();
    }

    public String getUrlLemanizacao() {
        return getServidor() + getLematizar();
    }

    public String getUrlRadical() {
        return getServidor() + getRadical();
    }
}