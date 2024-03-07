package org.example.repository;

import org.example.data.entity.User;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class TransactionRepository extends BaseRepository {
    private final SessionFactory sessionFactory;

    public TransactionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> getById(long id) {
        return null;
    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public Optional<User> save(User user) {
        return null;
    }

    @Override
    public Object update(Object entity) {
        return null;
    }
}