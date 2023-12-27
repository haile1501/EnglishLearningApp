package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.example.models.Exercise;
import org.example.models.ExerciseWork;
import org.example.utils.DBUtil;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ExerciseWorkDAO implements DAO<ExerciseWork> {
    private final Logger logger = LogManager.getLogger(RoleDAO.class);

    @Override
    public Optional<ExerciseWork> get(long id) {
        return Optional.empty();
    }

    @Override
    public List getAll() throws SQLException, ClassNotFoundException {
        try {
            ResultSet rs = DBUtil.dbExecuteQuery("select * from exercise_work");
            List<ExerciseWork> ExList = new ArrayList<>();

            while (rs.next()) {
                ExerciseWork ex = new ExerciseWork();
                ex.setId(rs.getInt("id"));
                ex.setWork(rs.getString("work"));
                ex.setCreatedAt(rs.getTimestamp("created_at"));
                ex.setUpdatedAt(rs.getTimestamp("updated_at"));
                ex.setExerciseId(rs.getInt("exercise_id"));
                ex.setStudentId(rs.getInt("student_id"));
                ex.setTeacherId(rs.getInt("teacher_id"));
                ex.setScore(rs.getDouble("score"));
                ex.setFeedback(rs.getString("feedback"));
                ExList.add(ex);
            }
            return ExList;
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public void save(ExerciseWork exerciseWork) throws SQLException {
        try {
//            Date createdDate = new Date();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String createdDate = dtf.format(now);
            System.out.println(dtf.format(now));
//            System.out.println("INSERT INTO exercise_work (`id`, `created_at`, `updated_at`, `work`, `feedback`, `score`, `exercise_id`, `student_id`, `teacher_id`) VALUES (" + exerciseWork.getId() + ", '" + createdDate + "', '"+createdDate+"', '" + exerciseWork.getWork() + "', NULL, NULL, '" + exerciseWork.getExerciseId() + "', " + exerciseWork.getStudentId() + ", " + exerciseWork.getTeacherId() + ");");
            DBUtil.dbExecuteUpdate("INSERT INTO exercise_work (`created_at`, `updated_at`, `work`, `feedback`, `score`, `exercise_id`, `student_id`, `teacher_id`) VALUES ('" + createdDate + "', '"+createdDate+"', '" + exerciseWork.getWork() + "', NULL, NULL, '" + exerciseWork.getExerciseId() + "', " + exerciseWork.getStudentId() + ", " + exerciseWork.getTeacherId() + ");");
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ExerciseWork exerciseWork, String[] params) {

    }

    @Override
    public void delete(ExerciseWork exerciseWork) {

    }
}
