package org.example.daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Role;
import org.example.utils.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleDAO implements DAO<Role> {

    private final Logger logger = LogManager.getLogger(RoleDAO.class);
    @Override
    public Optional<Role> get(int id) {
        return Optional.empty();
    }

    private RoleDAO() {

    }

    private static final class InstanceHolder {
        private static final RoleDAO instance = new RoleDAO();
    }

    public static RoleDAO getInstance() {

        return RoleDAO.InstanceHolder.instance;
    }

    @Override
    public List<Role> getAll() throws SQLException, ClassNotFoundException {
        try {
            ResultSet rs = DBUtil.dbExecuteQuery("select * from roles");
            List<Role> roleList = new ArrayList<>();

            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                role.setType(rs.getString("type"));
                role.setUpdatedAt(rs.getTimestamp("updatedAt"));
                role.setCreatedAt(rs.getTimestamp("createdAt"));
                roleList.add(role);
            }

            return roleList;
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public void save(Role role) {

    }

    @Override
    public void update(Role role, String[] params) {

    }

    @Override
    public void delete(Role role) {

    }

    public Optional<Role> getRoleByName(String name) throws SQLException, ClassNotFoundException {
        try {
            String stm = STR."select * from roles where name = '\{name}'";
            ResultSet rs = DBUtil.dbExecuteQuery(stm);

            List<Role> roles = getRoleList(rs);
            return roles.isEmpty() ? Optional.empty() : Optional.ofNullable(roles.getFirst());
        } catch (SQLException | ClassNotFoundException e) {
            logger.error(e);
            throw e;
        }
    }

    private List<Role> getRoleList(ResultSet rs) throws SQLException {
        List<Role> roleList = new ArrayList<>();

        while (rs.next()) {
            Role role = new Role();
            role.setId(rs.getInt("id"));
            role.setName(rs.getString("name"));
            role.setType(rs.getString("type"));
            role.setUpdatedAt(rs.getTimestamp("updated_at"));
            role.setCreatedAt(rs.getTimestamp("created_at"));
            roleList.add(role);
        }

        return roleList;
    }
}
