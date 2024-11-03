package br.com.spedison.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {

    public static String calculaHashSHA256(String path){
        return calculaHash(path, "SHA-256");
    }
    public static String calculaHashMD5(String path){
        return calculaHash(path, "MD5");
    }

    public static String calculaHash(String nomeArquivo, String algorithm){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithm);
            byte[] fileBytes = Files.readAllBytes(Path.of(nomeArquivo));  // Lê o conteúdo do arquivo
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02X", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
