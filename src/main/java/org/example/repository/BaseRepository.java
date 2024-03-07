package org.example.repository;

import org.example.data.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T> {

    static final Logger LOGGER = LoggerFactory.getLogger(BaseRepository.class);

    public abstract Optional<User> getById(long id);

    public abstract List<T> getAll();

    public abstract Optional<User> save(User user);

    public abstract T update(T entity);

}
