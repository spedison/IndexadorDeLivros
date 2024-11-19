package br.com.spedison.processadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultadoTokens {
    private String original;
    private List<String> tokens;
    private int count;
    private String estado;
}
