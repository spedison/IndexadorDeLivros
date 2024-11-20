package br.com.spedison.comandos.buscas.mariadb.pessoa;

import br.com.spedison.vo.PessoaFake;

public class ComandoPessoaUtils {
    static public void mostraUmRegistro(String busca, PessoaFake pessoa) {
        System.out.println(
                        """
                        Busca: [["%s"]] [%8d] - Nome: %-70s  [%3d]  End:%-70s"""
                        .formatted(
                                busca,
                                pessoa.getId(),
                                pessoa.getNome(),
                                pessoa.getIdade(),
                                pessoa.getEndereco()
                        )
        );
    }
}
