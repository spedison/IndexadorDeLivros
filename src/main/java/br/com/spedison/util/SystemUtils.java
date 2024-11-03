package br.com.spedison.util;

import java.io.File;
import java.net.URISyntaxException;
import java.time.Instant;

public class SystemUtils {
    static public String getJarUsado() {
        try {
            var jarPath = SystemUtils.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();
            return new File(jarPath).getName();
        } catch (URISyntaxException e) {
            System.err.println(Instant.now() + " - " + e.getMessage());
            e.printStackTrace();
            return "<nome do jar>.jar";
        }
    }
}
