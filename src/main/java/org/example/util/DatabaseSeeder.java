package org.example.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.*;

public class DatabaseSeeder {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RestaurantPU");

    public void ensureDatabaseIsPopulated() {
        EntityManager em = emf.createEntityManager();
        try {
            // verif produsele
            Long productCount = em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
            if (productCount == 0) {
                System.out.println("⚠️ Meniul este gol. Se introduc produsele...");
                populateProducts();
            }

            // verif utilizatorii
            Long userCount = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
            if (userCount == 0) {
                System.out.println("⚠️ Nu există utilizatori. Se creează conturile implicite...");
                populateUsers();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void populateProducts() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Mâncare
            em.persist(new Food("Pizza Quatro Formaggi", 45.0, 500));
            em.persist(new Food("Paste Carbonara", 52.5, 400));
            em.persist(new Food("Desert Tiramisu", 35.0, 150));
            em.persist(new Food("Burger Vita", 38.0, 350));

            // Băuturi
            em.persist(new Drinks("Cola Zero", 12.0, 330, null));
            em.persist(new Drinks("Apa Plata", 8.0, 500, null));
            em.persist(new Drinks("Vin Rosu", 25.0, 750, "12%"));
            em.persist(new Drinks("Bere Corona", 12.0, 330, "4.5%"));
            em.persist(new Drinks("Limonada", 15.0, 400, null));

            em.getTransaction().commit();
            System.out.println("✅ Produsele au fost inserate.");
        } finally {
            em.close();
        }
    }

    private void populateUsers() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // manager (Admin)
            User admin = new User("admin", "admin123", Role.ADMIN);
            em.persist(admin);

            // ospătar (Staff)
            User waiter = new User("ospatar", "1234", Role.STAFF);
            em.persist(waiter);

            em.getTransaction().commit();
            System.out.println("✅ Utilizatorii (admin, ospatar) au fost creați.");
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}