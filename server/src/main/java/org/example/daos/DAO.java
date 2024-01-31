package org.example.daos;

import org.example.models.dtos.ExerciseDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {

    Optional<T> get(int id) throws SQLException, ClassNotFoundException;

    List<T> getAll() throws SQLException, ClassNotFoundException;
  
    void save(T t) throws SQLException, ClassNotFoundException;

    void update(T t, String[] params);

    void delete(T t);
}
