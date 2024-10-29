package br.com.spedison.processadores;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Data;

@Data
public class Conexoes {

    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public Conexoes() {
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
        entityManager = entityManagerFactory.createEntityManager();
    }

    public void terminaConexao() {
        entityManager.close();
        entityManagerFactory.close();
    }

}
