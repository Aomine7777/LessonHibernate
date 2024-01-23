package org.example.repository;



import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserRepository {
    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }
    public void save(User user){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e){
            if ((transaction != null)) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public User findByID(Long userId){
        try(Session session = sessionFactory.openSession()){
            return session.get(User.class, userId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public User findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
