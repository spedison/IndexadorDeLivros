package br.com.spedison.processadores;

import br.com.spedison.util.FileUtils;
import br.com.spedison.vo.Livro;

import java.io.File;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class ProcessaLivro {

    private final Conexoes conexoes;
    private Livro livro;

    public ProcessaLivro(Conexoes conexoes, String caminhoLivro) {
        this.conexoes = conexoes;
        this.livro = getLivro(caminhoLivro);
    }

    public ProcessaLivro(Conexoes conexoes, Livro livro) {
        this.conexoes = conexoes;
        this.livro = livro;
    }

    private String calculaHashLivro() {
        return calculaHashLivro(livro.getCaminhoCompletoArquivo());
    }

    private String calculaHashLivro(String caminhoCompletoLivro) {
        return FileUtils.calculaHashSHA256(caminhoCompletoLivro);
    }

    public Livro adicionaOuAtualizaLivro(String caminhoCompletoLivro) {

        // Pega o livro pelo nome do arquivo.
        livro = getLivro(caminhoCompletoLivro);
        String hash = calculaHashLivro(caminhoCompletoLivro);

        // Achou um livro... e ele tem um hash igual (é o mesmo livro pode retornar ele mesmo)
        if (livro != null && livro.getHash().equals(hash)) {
            // Atualiza o caminho completo, se necessário.
            livro.setCaminhoCompletoArquivo(caminhoCompletoLivro);
            livro.setDataHoraInicial(Instant.now());
            conexoes.grava(livro);
            return livro;
        }

        String caminhoLivro = new File(caminhoCompletoLivro).getName();

        // Cria um novo livro para trabalhar.
        Livro novoLivro = new Livro();
        novoLivro.setDataHoraInicial(Instant.now());
        novoLivro.setCaminhoArquivo(caminhoLivro);
        novoLivro.setCaminhoCompletoArquivo(caminhoCompletoLivro);
        novoLivro.setHash(hash);
        novoLivro.setPaginas(new LinkedList<>());

        // Grava no banco e retorna.
        conexoes.grava(novoLivro);
        livro = novoLivro;
        return livro;
    }

    public void atualizaHorarioProcessamento(){
        conexoes.beginTransaction();
        conexoes
                .getEntityManager()
                .createQuery("""
                        update Livro l
                        set l.dataHoraFinal = :dataHoraFinal
                        where l = :livro
                        """)
                .setParameter("dataHoraFinal", Instant.now())
                .setParameter("livro", livro)
                .executeUpdate();
        conexoes.commitTransaction();
    }

    public Livro getLivro() {
        return livro;
    }

    public Livro getLivro(String caminhoCompletoLivro) {
        File file = new File(caminhoCompletoLivro);
        String caminhoLivro = file.getName();

        List<Livro> livros =
                conexoes
                        .getEntityManager()
                        .createQuery("""
                                SELECT
                                    l
                                from
                                    Livro l
                                where
                                    l.caminhoArquivo = :nomeArquivo or
                                    l.caminhoArquivo = :nomeArquivo
                                """, Livro.class)
                        .setParameter("nomeArquivo", caminhoLivro)
                        .getResultList();

        if (livros.isEmpty()) return null;

        livro = livros.getFirst();
        return livro;
    }

    public void apagaLivro() {

        conexoes.beginTransaction();
        conexoes.delete(livro);
        /*conexoes
                .getEntityManager()
                .createQuery("""
                        delete
                            Livro l
                        where
                            l = :livroAtual
                        """, Livro.class)
                .setParameter("livroAtual", livro)
                .executeUpdate();*/
        conexoes.commitTransaction();
    }

    public List<Livro> getLivros() {
        return
                conexoes
                        .getEntityManager()
                        .createQuery("""
                                            select l from Livro l
                                            """, Livro.class)
                        .getResultList();
    }
}
