package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.ExerciseWork;
import org.example.models.Feedback;
import org.example.utils.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FeedbackDAO implements DAO<Feedback> {
    private final Logger logger = LogManager.getLogger(FeedbackDAO.class);

    private static final class InstanceHolder {
        private static final FeedbackDAO instance = new FeedbackDAO();
    }

    public static FeedbackDAO getInstance() {

        return FeedbackDAO.InstanceHolder.instance;
    }
    @Override
    public Optional<Feedback> get(int id) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public List<Feedback> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(Feedback feedback) throws SQLException, ClassNotFoundException {

    }


    @Override
    public void update(Feedback feedback, String[] params) {

    }

    @Override
    public void delete(Feedback feedback) {

    }

    public void initFeedback(int studentId, int lessonId, String type) throws SQLException {
        try {
            DBUtil.dbExecuteUpdate(STR."INSERT INTO feedback (`student_id`, `lesson_id`, `type`, `status`) VALUES ('\{studentId}', '\{lessonId}', '\{type}', 'waiting');");
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Feedback findFeedback(int studentId, int lessonId, String type) throws SQLException, ClassNotFoundException {
            ResultSet rs = DBUtil.dbExecuteQuery(STR."select * from feedback where student_id = \{studentId} and lesson_id = \{lessonId} and type = '\{type}'");
            List<Feedback> feedbacks = new ArrayList<>();
            while (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setId(rs.getInt("id"));
                feedback.setCreatedAt(rs.getTimestamp("created_at"));
                feedback.setUpdatedAt(rs.getTimestamp("updated_at"));
                feedback.setType(rs.getString("type"));
                feedback.setStatus(rs.getString("status"));
                feedback.setScore(rs.getInt("score"));
                feedback.setComment(rs.getString("comment"));
                feedback.setTeacherId(rs.getInt("teacher_id"));
                feedbacks.add(feedback);
            }
            return !feedbacks.isEmpty() ? feedbacks.getFirst() : null;
    }

    public void giveFeedback(Feedback feedback) throws SQLException {
        try {
            DBUtil.dbExecuteUpdate(STR."update feedback set score = \{feedback.getScore()}, comment = '\{feedback.getComment()}', status = 'evaluated' where id = \{feedback.getId()}");
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
