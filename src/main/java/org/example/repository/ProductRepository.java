package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.Product;

import java.util.List;
import java.util.Optional;


//repository pentru gestionarea entităților Product în baza de date PostgreSQL

public class ProductRepository {
    // IMPORTANT: Trebuie să se potrivească cu persistence-unit name din persistence.xml!
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RestaurantPU");

    public void addProduct(Product product) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();
            System.out.println("✅ Produs salvat: " + product.getName());
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Eroare la salvarea produsului: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Product> getAllProducts() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p ORDER BY p.name", Product.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Product> findProductByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Product> results = em.createQuery(
                    "SELECT p FROM Product p WHERE p.name = :name", Product.class)
                    .setParameter("name", name)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public void deleteProduct(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Product product = em.find(Product.class, id);
            if (product != null) {
                em.remove(product);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Eroare la ștergerea produsului: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    public void updateProduct(Product product) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // merge = actualizează datele existente
            em.merge(product);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
