package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.Order;

import java.util.List;

public class OrderRepository {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RestaurantPU");

    public void saveOrder(Order order) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (order.getId() == null) {
                em.persist(order);
            } else {
                em.merge(order);
            }
            em.getTransaction().commit();
            System.out.println("✅ Comandă salvată cu succes! ID: " + order.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Eroare la salvarea comenzii.");
        } finally {
            em.close();
        }
    }

    // istoric Global (Manager)
    public List<Order> getAllOrders() {

        //întârziere artificială de 2 secunde pt iteratia 8
        try {
            System.out.println("[DEBUG] Simulare conexiune lentă la baza de date...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // istoric Personal (Staff)
    public List<Order> getOrdersByUser(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT o FROM Order o WHERE o.user.id = :uid ORDER BY o.orderDate DESC", Order.class)
                    .setParameter("uid", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}