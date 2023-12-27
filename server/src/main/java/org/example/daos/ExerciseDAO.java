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
    private final Logger logger = LogManager.getLogger(RoleDAO.class);

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
                ex.setLessonId(rs.getInt("lesson_id"));
                String type = rs.getString("type");
                switch (type){
                    case "REWRITE":
                        ex.setType(Exercise.EX_TYPE.REWRITE);
                        break;
                    case "PARAGRAPH":
                        ex.setType(Exercise.EX_TYPE.PARAGRAPH);
                        break;
                    case "SPEAKING":
                        ex.setType(Exercise.EX_TYPE.SPEAKING);
                        break;
                }
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
}
