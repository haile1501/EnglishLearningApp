package org.example.daos;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> get(int id) throws SQLException, ClassNotFoundException;

    abstract List getAll() throws SQLException, ClassNotFoundException;
  
    void save(T t) throws SQLException, ClassNotFoundException;

    void update(T t, String[] params);

    void delete(T t);
}
