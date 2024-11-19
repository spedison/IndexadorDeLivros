package br.com.spedison.comandos.outros;

import br.com.spedison.comandos.ComandoInterface;
import br.com.spedison.util.ConexaoConfigJDBC;
import br.com.spedison.util.StringUtils;
import br.com.spedison.util.SystemUtils;

import java.io.PrintWriter;
import java.sql.*;

public class ComandoConectUsandoJDBC implements ComandoInterface {
    @Override
    public void execute(String[] args) {

        ConexaoConfigJDBC config = new ConexaoConfigJDBC();
        try {
            //Class.forName(config.getDriver());

            // Para que o DriverManager log na tela.
            DriverManager.setLogWriter(new PrintWriter(System.out));

            // Abre a conexao.
            Connection conn = DriverManager
                    .getConnection(config.getUrl(),
                            config.getUsuario(),
                            config.getSenha());

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("""
                    SELECT count(1) contagem , 'Livro' nome FROM tb_livro
                    union 
                    SELECT count(1) contagem , 'Pagina' nome FROM tb_pagina
                    union
                    SELECT count(1) contagem , 'Paragrafo' nome FROM tb_paragrafo
                    union
                    SELECT count(1) contagem , 'Palavra' nome FROM tb_palavra
                    union
                    SELECT count(1) contagem , 'Pessoa' nome FROM tb_pessoa_fake
                    """);
            while (rs.next()) {
                System.out.printf("Total de %-11s: %s\n",
                        rs.getString(2),
                        StringUtils.formataNumero(rs.getInt(1)));
            }
            rs.close();
            stmt.close();
            conn.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } //catch (ClassNotFoundException e) {
        //  throw new RuntimeException(e);
        //}
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -conecta-com-JDBC ou -ccj
                Descrição : Exemplo de conexão JDBC contando os registros.
                Argumentos:
                          <Sem argumentos>
                Exemplo   : java -jar %s --conecta-com-JDBC
                """.formatted(SystemUtils.getJarUsado()));
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 1 &&
                (args[0].equalsIgnoreCase("-conectaComJDBC") ||
                        args[0].equalsIgnoreCase("-cCJ"));
    }

//    public static void main(String[] args) {
//        ComandoConectUsandoJDBC comando = new ComandoConectUsandoJDBC();
//        comando.execute(args);
//    }

}
