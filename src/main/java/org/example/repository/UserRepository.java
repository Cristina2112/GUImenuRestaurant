package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.model.Role;
import org.example.model.User;

import java.util.List;
import java.util.Optional;

public class UserRepository {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RestaurantPU");

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username AND u.password = :password",
                    User.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.getResultList().stream().findFirst();
        } finally {
            em.close();
        }
    }

    public void addUser(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    public List<User> getAllStaff() {
        EntityManager em = emf.createEntityManager();
        try {
            // returnăm doar angajații (STAFF)
            return em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                    .setParameter("role", Role.STAFF)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteUser(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                // datorita cascade=CascadeType.ALL din User.java comenzile se șterg automat
                em.remove(user);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Eroare la ștergerea utilizatorului.");
        } finally {
            em.close();
        }
    }
}