package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Conversation;
import org.example.utils.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ConversationDAO implements DAO<Conversation> {
    private final Logger logger = LogManager.getLogger(ConversationDAO.class);

    private static final class InstanceHolder {
        private static final ConversationDAO instance = new ConversationDAO();
    }

    public static ConversationDAO getInstance() {
        return InstanceHolder.instance;
    }

    public Optional<Conversation> getConversationByUserId(Long firstUserId, Long secondUserId) throws SQLException, ClassNotFoundException {
        try {
            String stm = STR."SELECT ";
            ResultSet rs = DBUtil.dbExecuteQuery(stm);
            return  Optional.of(getConversation(rs));
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        }
    }

    private Conversation getConversation(ResultSet rs) throws SQLException {
        Conversation conversation = new Conversation();
        conversation.setId(rs.getInt("id"));
        conversation.setCreatedAt(rs.getTimestamp("created_at"));
        conversation.setUpdatedAt(rs.getTimestamp("update_at"));
        conversation.setFirstUserId(rs.getLong("first_user_id"));
        conversation.setSecondUserId(rs.getLong("second_user_id"));

        return conversation;
    }

    @Override
    public Optional<Conversation> get(int id) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public List<Conversation> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(Conversation conversation) throws SQLException, ClassNotFoundException {
        String stm = STR."INSERT INTO conversations(first_user_id, second_user_id) VALUES (\{conversation.getFirstUserId()}, \{conversation.getSecondUserId()})";
        DBUtil.dbExecuteUpdate(stm);
    }

    public Long save(Long firstUserId, Long secondUserId) throws SQLException, ClassNotFoundException {
        String insert = STR."INSERT INTO conversations(first_user_id, second_user_id) VALUES (\{firstUserId}, \{secondUserId})";
        DBUtil.dbExecuteUpdate(insert);
        String query = STR."SELECT c.id FROM conversations AS c WHERE (c.first_user_id = \{firstUserId} AND c.second_user_id = \{secondUserId}) OR (c.second_user_id = \{firstUserId} AND c.first_user_id = \{secondUserId})";
        ResultSet rs = DBUtil.dbExecuteQuery(query);
        return rs.getLong("id");
    }

    @Override
    public void update(Conversation conversation, String[] params) {

    }

    @Override
    public void delete(Conversation conversation) {

    }
}
