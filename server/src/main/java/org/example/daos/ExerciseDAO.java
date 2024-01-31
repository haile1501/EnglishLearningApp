package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.example.models.Exercise;
import org.example.utils.DBUtil;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseDAO implements DAO<Exercise>{
    private final Logger logger = LogManager.getLogger(ExerciseDAO.class);

    @Override
    public Optional<Exercise> get(int id) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public List<Exercise> getAll() throws SQLException, ClassNotFoundException {
        try {
            ResultSet rs = DBUtil.dbExecuteQuery("select * from exercises");
            List<Exercise> ExList = new ArrayList<>();

            while (rs.next()) {
                Exercise ex = new Exercise();
                ex.setId(rs.getInt("id"));
                ex.setContent(rs.getString("content"));
                ex.setCreatedAt(rs.getTimestamp("created_at"));
                ex.setUpdatedAt(rs.getTimestamp("updated_at"));
                ex.setType(rs.getString("type"));
                ExList.add(ex);
            }
            return ExList;
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public void save(Exercise ex) {

    }

    @Override
    public void update(Exercise ex, String[] params) {

    }

    @Override
    public void delete(Exercise ex) {

    }

    private static final class InstanceHolder {
        private static final ExerciseDAO instance = new ExerciseDAO();
    }

    public static ExerciseDAO getInstance() {

        return ExerciseDAO.InstanceHolder.instance;
    }

    public List<Exercise> getRewriteExercisesByLessonId(Integer lessonId) throws SQLException, ClassNotFoundException {
        try {
            String stm = "select * from exercises e where e.lesson_id = 1 and e.type = 'rewrite'";
            ResultSet rs = DBUtil.dbExecuteQuery(stm);
            List<Exercise> ExList = new ArrayList<>();

            while (rs.next()) {
                Exercise ex = new Exercise();
                ex.setId(rs.getInt("id"));
                ex.setContent(rs.getString("content"));
                ex.setCreatedAt(rs.getTimestamp("created_at"));
                ex.setUpdatedAt(rs.getTimestamp("updated_at"));
                ex.setType(rs.getString("type"));
                ExList.add(ex);
            }
            return ExList;
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    public Exercise getOneExercise(Integer lessonId, String type) throws SQLException, ClassNotFoundException {
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(STR."select * from exercises e where e.lesson_id = 1 and e.type ='\{type}'");
            List<Exercise> ExList = new ArrayList<>();
            while (rs.next()) {
                Exercise ex = new Exercise();
                ex.setId(rs.getInt("id"));
                ex.setContent(rs.getString("content"));
                ex.setCreatedAt(rs.getTimestamp("created_at"));
                ex.setUpdatedAt(rs.getTimestamp("updated_at"));
                ex.setType(rs.getString("type"));
                ExList.add(ex);
            }
            return ExList.getFirst();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }
}
