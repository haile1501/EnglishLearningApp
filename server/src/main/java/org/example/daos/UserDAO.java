package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Role;
import org.example.models.User;
import org.example.utils.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAO<User> {
    private final Logger logger = LogManager.getLogger(RoleDAO.class);

    private UserDAO() {

    }

    private static final class InstanceHolder {
        private static final UserDAO instance = new UserDAO();
    }

    public static UserDAO getInstance() {

        return InstanceHolder.instance;
    }

    @Override
    public Optional<User> get(int id) throws SQLException, ClassNotFoundException {
        try {
            String stm = STR."select * from accounts a inner join roles r on a.role_id = r.id where a.id = \{id}";
            ResultSet rs = DBUtil.dbExecuteQuery(stm);

            return Optional.ofNullable(getUserList(rs).getFirst());
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public List<User> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(User user) throws SQLException, ClassNotFoundException {
        RoleDAO roleDAO = RoleDAO.getInstance();
        Role role = roleDAO.getRoleByName(user.getRole()).get();
        String stm = STR."insert into accounts(loginId, password, role_id) values('\{user.getLoginId()}', '\{user.getPassword()}', '\{role.getId()}')";
        DBUtil.dbExecuteUpdate(stm);
    }

    @Override
    public void update(User user, String[] params) {

    }

    @Override
    public void delete(User user) {
    }

    public Optional<User> getUserByLoginId(String loginId) throws SQLException, ClassNotFoundException {
        try {
            String stm = STR."select * from accounts a inner join roles r on a.role_id = r.id where a.loginId = '\{loginId}'";
            ResultSet rs = DBUtil.dbExecuteQuery(stm);

            List<User> users = getUserList(rs);
            return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.getFirst());
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        }
    }

    public List<User> getUsers() throws SQLException, ClassNotFoundException {
        ResultSet rs = DBUtil.dbExecuteQuery("select * from accounts a inner join roles r on a.role_id = r.id");
        return getUserList(rs);
    }

    private List<User> getUserList(ResultSet rs) throws SQLException {
        List<User> userList = new ArrayList<>();

        while (rs.next()) {
            User user = new User();
            user.setRole(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLoginId(rs.getString("loginId"));
            user.setId(rs.getInt("id"));
            user.setCreatedAt(rs.getTimestamp("created_at"));
            user.setUpdatedAt(rs.getTimestamp("updated_at"));
            userList.add(user);
        }

        return userList;
    }
}