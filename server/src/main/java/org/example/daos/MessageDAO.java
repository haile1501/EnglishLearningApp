package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Message;
import org.example.utils.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    private final Logger logger = LogManager.getLogger(MessageDAO.class);

    private static final class InstanceHolder {
        private static final MessageDAO instance = new MessageDAO();
    }

    public static MessageDAO getInstance() {

        return  MessageDAO.InstanceHolder.instance;
    }

    public List<Message> getMessageList(int userId1, int userId2) throws SQLException, ClassNotFoundException {
        String stm = STR."select * from messages where (sender_id = \{userId1} and receiver_id = \{userId2}) or (sender_id = \{userId2} and receiver_id = \{userId1}) order by created_at asc";
        ResultSet rs = DBUtil.dbExecuteQuery(stm);
        List<Message> messageList = new ArrayList<>();

        while (rs.next()) {
            Message message = new Message();
            message.setId(rs.getInt("id"));
            message.setCreatedAt(rs.getTimestamp("created_at"));
            message.setUpdatedAt(rs.getTimestamp("updated_at"));
            message.setContent(rs.getString("content"));
            message.setSenderId(rs.getInt("sender_id"));
            message.setReceiverId(rs.getInt("receiver_id"));
            messageList.add(message);
        }

        return messageList;
    }

    public void insertMessage(int senderId, int receiverId, String content) throws SQLException, ClassNotFoundException {
        DBUtil.dbExecuteUpdate(STR."insert into messages(sender_id, receiver_id, content) values (\{senderId}, \{receiverId}, '\{content}')");
    }
}
