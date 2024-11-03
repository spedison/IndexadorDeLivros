package br.com.spedison.vo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_pessoa_fake")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PessoaFake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Define a coluna como auto-incremento
    private Integer id;

    private String nome;
    private String endereco;
    private int idade;
}