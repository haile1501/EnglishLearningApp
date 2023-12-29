package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Conversation;
import org.example.models.Message;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MessageDAO implements DAO<Message> {

    private final Logger logger = LogManager.getLogger(MessageDAO.class);

    private static final class InstanceHolder {
        private static final MessageDAO instance = new MessageDAO();
    }

    public static MessageDAO getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public Optional<Message> get(int id) throws SQLException, ClassNotFoundException {
        return Optional.empty();
    }

    @Override
    public List<Message> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(Message message) throws SQLException, ClassNotFoundException {
    }

    @Override
    public void update(Message message, String[] params) {

    }

    @Override
    public void delete(Message message) {

    }
}
