package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.example.models.ExerciseWork;
import org.example.models.Feedback;
import org.example.models.dtos.ExerciseDto;
import org.example.models.dtos.Submission;
import org.example.utils.DBUtil;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseWorkDAO implements DAO<ExerciseWork> {
    private final Logger logger = LogManager.getLogger(ExerciseWorkDAO.class);

    @Override
    public Optional<ExerciseWork> get(int id) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public List<ExerciseWork> getAll() throws SQLException, ClassNotFoundException {
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
                ExList.add(ex);
            }
            return ExList;
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        }
    }

    private static final class InstanceHolder {
        private static final ExerciseWorkDAO instance = new ExerciseWorkDAO();
    }

    public static ExerciseWorkDAO getInstance() {

        return ExerciseWorkDAO.InstanceHolder.instance;
    }

    @Override
    public void save(ExerciseWork exerciseWork) throws SQLException, ClassNotFoundException {

    }

    public void save(ExerciseDto exerciseDto, int studentId) throws SQLException {
        try {
            DBUtil.dbExecuteUpdate(STR."INSERT INTO exercise_work (`work`, `exercise_id`, `student_id`) VALUES ('\{exerciseDto.getStudentWork()}', '\{exerciseDto.getId()}', '\{studentId}');");
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

    public ExerciseWork getWork(int studentId, int exerciseId) throws SQLException, ClassNotFoundException {
        ResultSet rs = DBUtil.dbExecuteQuery(STR."select * from exercise_work where student_id = \{studentId} and exercise_id = \{exerciseId}");
        List<ExerciseWork> exerciseWorks = new ArrayList<>();
        while (rs.next()) {
            ExerciseWork exerciseWork = new ExerciseWork();
            exerciseWork.setId(rs.getInt("id"));
            exerciseWork.setCreatedAt(rs.getTimestamp("created_at"));
            exerciseWork.setUpdatedAt(rs.getTimestamp("updated_at"));
            exerciseWork.setWork(rs.getString("work"));
            exerciseWorks.add(exerciseWork);
        }
        return !exerciseWorks.isEmpty() ? exerciseWorks.getFirst() : null;
    }

    public List<Submission> getSubmissionList(String exType) throws SQLException, ClassNotFoundException {
        ResultSet rs = DBUtil.dbExecuteQuery(STR."select name, loginId, f.type, lesson_id, student_id from feedback f, accounts a, lessons l, topics t where f.lesson_id = l.id and f.student_id = a.id and l.topic_id = t.id and f.type = '\{exType}'");
        List<Submission> submissionList = new ArrayList<>();
        while (rs.next()) {
            Submission submission = new Submission();
            submission.setLesson(rs.getString("name"));
            submission.setStudent(rs.getString("loginId"));
            submission.setType(rs.getString("type"));
            submission.setStudentId(rs.getInt("student_id"));
            submission.setLessonId(rs.getInt("lesson_id"));
            submissionList.add(submission);
        }

        return submissionList;
    }
}
