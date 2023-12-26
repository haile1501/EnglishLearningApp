package org.example.daos;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> get(long id);

    List<T> getAll() throws SQLException, ClassNotFoundException;

    void save(T t);

    void update(T t, String[] params);

    void delete(T t);
}
