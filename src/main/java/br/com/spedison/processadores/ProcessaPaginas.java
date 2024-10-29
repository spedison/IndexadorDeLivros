package br.com.spedison.processadores;

import br.com.spedison.util.LePDF;
import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Pagina;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.function.Consumer;

public class ProcessaPaginas implements Consumer<LePDF.PaginaLida> {

    private final Conexoes conexoes;
    int contaPaginas;

    Livro livroAtual;
    int commitBook = 50;

    public ProcessaPaginas(Conexoes conexoes) {
        this.conexoes = conexoes;
    }

    public Livro getLivroAtual() {
        return livroAtual;
    }

    public void processaArquivo (String nomeArquivoPDF) {
        livroAtual = new Livro();
        livroAtual.setNomeLivro(nomeArquivoPDF);
        livroAtual.setPaginas(new java.util.ArrayList<>());

        EntityManager entityManager = conexoes.getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(livroAtual);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        LePDF.lerPDF(nomeArquivoPDF, this);
        entityManager.getTransaction().commit();
    }


    @Override
    public void accept(LePDF.PaginaLida paginaLida) {

        contaPaginas++;
        Pagina pagina = new Pagina();
        pagina.setLivro(livroAtual);
        pagina.setNumeroPagina(paginaLida.getNumeroPagina());
        pagina.setConteudo(paginaLida.getConteudoPagina());

        EntityManager entityManager = conexoes.getEntityManager();

        livroAtual.getPaginas().add(pagina);
        entityManager.persist(pagina);

        if (contaPaginas % commitBook == 0) {
            entityManager.getTransaction().commit();
            entityManager.getTransaction().begin();
        }
    }

}
