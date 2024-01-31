package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.LessonContent;
import org.example.models.QuizQuestion;
import org.example.utils.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizQuestionDAO implements DAO<QuizQuestion> {
    private final Logger logger = LogManager.getLogger(QuizQuestionDAO.class);

    @Override
    public Optional<QuizQuestion> get(int id) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public List<QuizQuestion> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(QuizQuestion quizQuestion) throws SQLException, ClassNotFoundException {

    }

    @Override
    public void update(QuizQuestion quizQuestion, String[] params) {

    }

    @Override
    public void delete(QuizQuestion quizQuestion) {

    }

    private static final class InstanceHolder {
        private static final QuizQuestionDAO instance = new QuizQuestionDAO();
    }

    public static QuizQuestionDAO getInstance() {

        return QuizQuestionDAO.InstanceHolder.instance;
    }

    public List<QuizQuestion> getQuizQuestionsByLessonId(int id) throws SQLException, ClassNotFoundException {
        try {
            String stm = "select * from quizz_questions q where q.lesson_id = 1";
            ResultSet rs = DBUtil.dbExecuteQuery(stm);

            return getQuizQuestionList(rs);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    private List<QuizQuestion> getQuizQuestionList(ResultSet rs) throws SQLException {
        List<QuizQuestion> quizQuestionList = new ArrayList<>();

        while(rs.next()) {
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.setId(rs.getInt("id"));
            quizQuestion.setCreatedAt(rs.getTimestamp("created_at"));
            quizQuestion.setUpdatedAt(rs.getTimestamp("updated_at"));
            quizQuestion.setA(rs.getString("a"));
            quizQuestion.setB(rs.getString("b"));
            quizQuestion.setC(rs.getString("c"));
            quizQuestion.setD(rs.getString("d"));
            quizQuestion.setQuestion(rs.getString("question"));
            quizQuestion.setCorrectAnswer(rs.getString("correct_answer"));
            quizQuestion.setType(rs.getString("type"));
            quizQuestionList.add(quizQuestion);
        }

        return quizQuestionList;
    }
}
