package org.example.repository;

import org.example.data.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserRepository extends BaseRepository<User> {
    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public Optional<User> save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            return Optional.of(user);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Error occurred while saving account", e);
            return Optional.empty();
        }
    }
    @Override
    public Optional<User> getById (long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            LOGGER.error("Error occurred while get by id account",e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAll() {
        return null;
    }


    @Override
    public User update(User entity) {
        return null;
    }
}