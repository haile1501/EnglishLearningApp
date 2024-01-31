package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.LessonContent;
import org.example.utils.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LessonContentDAO implements DAO<LessonContent> {
    private final Logger logger = LogManager.getLogger(LessonContentDAO.class);
    @Override
    public Optional<LessonContent> get(int id) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public List<LessonContent> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(LessonContent lessonContent) throws SQLException, ClassNotFoundException {

    }

    @Override
    public void update(LessonContent lessonContent, String[] params) {

    }

    @Override
    public void delete(LessonContent lessonContent) {

    }

    private static final class InstanceHolder {
        private static final LessonContentDAO instance = new LessonContentDAO();
    }

    public static LessonContentDAO getInstance() {

        return LessonContentDAO.InstanceHolder.instance;
    }

    public List<LessonContent> getLessonContentsByLessonId(int id) throws SQLException, ClassNotFoundException {
        try {
            String stm = "select * from contents c where c.lesson_id = 1 order by type desc";
            ResultSet rs = DBUtil.dbExecuteQuery(stm);

            return getLessonContentList(rs);
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    private List<LessonContent> getLessonContentList(ResultSet rs) throws SQLException {
        List<LessonContent> lessonContentList = new ArrayList<>();

        while (rs.next()) {
            LessonContent lessonContent = new LessonContent();
            lessonContent.setId(rs.getInt("id"));
            lessonContent.setCreatedAt(rs.getTimestamp("created_at"));
            lessonContent.setUpdatedAt(rs.getTimestamp("updated_at"));
            lessonContent.setName(rs.getString("name"));
            lessonContent.setMeaning(rs.getString("meaning"));
            lessonContent.setType(rs.getString("type"));
            lessonContentList.add(lessonContent);
        }

        return lessonContentList;
    }
}
