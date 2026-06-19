package dao;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Generic DAO interface for CRUD operations.
 * @param <T> the entity type
 * @param <K> the primary key type
 * @author D00276269
 */
public interface Dao<T, K> {

    List<T> getAll();

    Optional<T> getById(K id);

    T insert(T entity);

    T update(K id, T entity);

    boolean deleteById(K id);

    List<T> findByFilter(Predicate<T> filter);
}