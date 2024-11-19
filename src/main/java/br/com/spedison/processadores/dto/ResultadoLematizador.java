package br.com.spedison.processadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultadoLematizador {
    private String original;
    private String lematizado;
    private String estado;
}
