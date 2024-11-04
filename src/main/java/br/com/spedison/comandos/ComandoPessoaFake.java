package br.com.spedison.comandos;

import br.com.spedison.processadores.Conexoes;
import br.com.spedison.vo.PessoaFake;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Random;
import java.util.stream.LongStream;

public class ComandoPessoaFake implements ComandoInterface {

    static final Logger log = LoggerFactory.getLogger(ComandoPessoaFake.class);

    @Override
    public void execute(String[] args) {
        Long quantidadeRegistros = Long.parseLong(args[1]) * 1_000_000;
        Faker faker = new Faker(Locale.of("pt", "BR"));
        final Random random = new Random();
        Conexoes conexoes = new Conexoes();
        conexoes.beginTransaction();

        LongStream
                .range(0, quantidadeRegistros)
                .forEach(i ->
                {
                    PessoaFake pessoaFake = new PessoaFake();
                    pessoaFake.setEndereco(faker.address().streetAddress() + "," + faker.address().buildingNumber());
                    pessoaFake.setIdade(faker.number().numberBetween(1, 103));
                    boolean mn = random.nextDouble() >= .5;
                    pessoaFake.setNome(
                            faker.name().fullName() + " " + (mn ? faker.name().fullName().split(" ")[1] + " " : "") + faker.name().lastName());

                    conexoes.grava(pessoaFake);

                    if (i % 10_003L == 0) {
                        conexoes.commitTransaction();
                        conexoes.beginTransaction();
                        log.info("Commitado " + i);
                    }
                });

        conexoes.commitTransaction();
        conexoes.terminaConexao();
    }

    @Override
    public StringBuilder showHelp(StringBuilder help) {
        return help.append("""
                Comando   : -pessoa-fake ou -pf
                Descrição : Cria uma pessoa fictícia com nome, endereço e idade.
                Argumentos: Milhes de registros gerados.
                Exemplo   : java -jar %s pessoa-fake 10
                """);
    }

    @Override
    public boolean aceitoComando(String[] args) {
        return args.length == 2 &&
                (args[0].equalsIgnoreCase("-pessoa-fake") ||
                        args[0].equalsIgnoreCase("-pf"));
    }
}