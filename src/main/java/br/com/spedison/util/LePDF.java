package br.com.spedison.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class LePDF {


    @AllArgsConstructor
    @Data
    static public class PaginaLida {
        private int numeroPagina;
        private String conteudoPagina;
    }

    static public int lerPDF(String caminhoArquivoPDF, Consumer<PaginaLida> consumer) {
        int numeroDePaginasLidas = 0;
        try (PDDocument document = PDDocument.load(new File(caminhoArquivoPDF))) {
            if (!document.isEncrypted()) {
                // Instancia o stripper para extração do texto
                PDFTextStripper pdfStripper = new PDFTextStripper();
                int totalPages = document.getNumberOfPages();
                for (int i = 1; i <= totalPages; i++) {
                    pdfStripper.setStartPage(i);
                    pdfStripper.setEndPage(i);
                    String text = pdfStripper.getText(document);
                    consumer.accept(new PaginaLida(i, text));
                    numeroDePaginasLidas++;
                }
            } else {
                System.out.println("O documento está encriptado e não pode ser lido.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numeroDePaginasLidas;
    }
}
