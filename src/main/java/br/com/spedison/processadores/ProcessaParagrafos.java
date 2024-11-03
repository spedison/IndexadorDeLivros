package br.com.spedison.processadores;

import br.com.spedison.vo.Livro;
import br.com.spedison.vo.Pagina;
import br.com.spedison.vo.Paragrafo;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProcessaParagrafos {
    private final Conexoes conexoes;
    Livro livro;


    public ProcessaParagrafos(Conexoes conexoes,Livro livro) {
        this.livro = livro;
        this.conexoes = conexoes;
    }


    public void processaParagrafos() {
        List<Pagina> paraProcessar = conexoes
                .getEntityManager()
                .createQuery("SELECT p FROM Pagina p WHERE p.livro = :livro", Pagina.class)
                .setParameter("livro", livro)
                .getResultList();

        EntityManager entityManager = conexoes.getEntityManager();

        for (Pagina pagina : paraProcessar) {
            int contaParagrafos = 1;

            // Extrai os parágrafos da página
            String[] paragrafos = pagina.getConteudo().split("[\n]{1,2}");

            entityManager.getTransaction().begin();

            // Processa o conteúdo da página
            for (String paragrafo : paragrafos) {

                if (paragrafo.trim().isBlank() )
                    continue;

                Paragrafo paragrafoGravado = new Paragrafo();
                paragrafoGravado.setPagina(pagina);
                paragrafoGravado.setConteudo(paragrafo);
                paragrafoGravado.setPosicaoParagrafo(contaParagrafos);
                entityManager.persist(paragrafoGravado);
                contaParagrafos++;
            }
            entityManager.getTransaction().commit();
        }
    }
    public void apagaParagrafosDoLivro() {
        conexoes.beginTransaction();
        conexoes.getEntityManager().createQuery(
                        """
                                    delete from Paragrafo p
                                    where p.pagina.livro = :livro
                                """, Paragrafo.class)
                .setParameter("livro", livro)
                .executeUpdate();
        conexoes.commitTransaction();
    }
}