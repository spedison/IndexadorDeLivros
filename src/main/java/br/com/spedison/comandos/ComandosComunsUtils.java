package br.com.spedison.comandos;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ComandosComunsUtils {

    static private Logger log = LoggerFactory.getLogger(ComandosComunsUtils.class);

    public static boolean comparaFase(String[] args, int fase, int quantParametros) {
        return args.length == quantParametros &&
                (args[0].equalsIgnoreCase("-idx") ||
                        args[0].equalsIgnoreCase("-indexar") ||
                        args[0].equalsIgnoreCase("-index")
                )
                &&
                (args[1].equalsIgnoreCase("-fase%d".formatted(fase)) ||
                        args[1].equalsIgnoreCase("-fs%d".formatted(fase)) ||
                        args[1].equalsIgnoreCase("-f%d".formatted(fase))
                );

    }

    public static String montaParametrosIdx(int fase) {
        return "-idx ou -indexar ou -index  e  -fase%d  ou -f%d -fs%d ".
                formatted(fase, fase, fase);
    }

    public static String montaParametrosExemplo(int fase) {
        return "-idx -fase%d".formatted(fase);
    }

    public static boolean ajusteDiretorio(File diretorioIndexador) {
        try {
            if (diretorioIndexador.exists()) {
                FileUtils.deleteDirectory(diretorioIndexador);
                log.info("Todos os diretórios e arquivos em [{}] foram apagados!", diretorioIndexador);
            }
            return diretorioIndexador.mkdirs();
        } catch (IOException e) {
            log.error("Erro ao apagar diretórios e arquivos: {}", e.getMessage());
            return false;
        }
    }
}
