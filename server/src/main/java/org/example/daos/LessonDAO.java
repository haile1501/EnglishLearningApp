package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Lesson;
import org.example.utils.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LessonDAO implements DAO<Lesson> {

    private final Logger logger = LogManager.getLogger(LessonDAO.class);

    private static final class InstanceHolder {
        private static final LessonDAO instance = new LessonDAO();
    }

    public static LessonDAO getInstance() {

        return LessonDAO.InstanceHolder.instance;
    }

    @Override
    public Optional<Lesson> get(int id) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public List<Lesson> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(Lesson lesson) throws SQLException, ClassNotFoundException {

    }

    @Override
    public void update(Lesson lesson, String[] params) {

    }

    @Override
    public void delete(Lesson lesson) {

    }

    public List<Lesson> getLessonListByTopicAndLevel(String topic, String level) throws SQLException, ClassNotFoundException {
        try {
            String stm = STR."select l.*, topics.name, levels.name from lessons l, topics, levels where l.topic_id = topics.id and l.level_id = levels.id and lower(topics.name) like '%\{topic.toLowerCase()}%'and levels.name = '\{level}'";
            ResultSet rs = DBUtil.dbExecuteQuery(stm);

            return getLessonList(rs);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    private List<Lesson> getLessonList(ResultSet rs) throws SQLException {
        List<Lesson> lessonList = new ArrayList<>();

        while (rs.next()) {
            Lesson lesson = new Lesson();
            lesson.setId(rs.getInt("id"));
            lesson.setCreatedAt(rs.getTimestamp("created_at"));
            lesson.setUpdatedAt(rs.getTimestamp("updated_at"));
            lesson.setAudioUrl(rs.getString("audio_url"));
            lesson.setVideoUrl(rs.getString("video_url"));
            lesson.setTopic(rs.getString(8));
            lesson.setLevel(rs.getString(9));
            lessonList.add(lesson);
        }

        return lessonList;
    }
}
