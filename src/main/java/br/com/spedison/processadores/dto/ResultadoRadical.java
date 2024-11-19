package br.com.spedison.processadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultadoRadical {
    private String original;
    private String radical;
    private String estado;
}
