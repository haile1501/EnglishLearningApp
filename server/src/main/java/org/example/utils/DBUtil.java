package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.*;
import java.sql.*;

public class DBUtil {
    private static final Logger logger = LogManager.getLogger(DBUtil.class);
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;
    private static final String connStr = System.getenv("DBUrl");
    public static void dbConnect() throws SQLException, ClassNotFoundException {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            logger.error(e.getStackTrace());
            throw e;
        }
        try {
            String username = System.getenv("DBUsername");
            String password = System.getenv("DBPassword");
            conn = DriverManager.getConnection(connStr, username, password);
        } catch (SQLException e) {
            logger.error(e.getStackTrace());
            throw e;
        }
    }
    public static void dbDisconnect() throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e){
            logger.error(e.getStackTrace());
            throw e;
        }
    }
    public static ResultSet dbExecuteQuery(String queryStmt) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        ResultSet resultSet = null;
        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        try {
            dbConnect();
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(queryStmt);
            crs.populate(resultSet);
        } catch (SQLException e) {
            logger.error(e.getStackTrace());
            throw e;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            dbDisconnect();
        }
        return crs;
    }
    public static void dbExecuteUpdate(String sqlStmt) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        try {
            dbConnect();
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlStmt);
        } catch (SQLException e) {
            logger.error(e);
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            dbDisconnect();
        }
    }

    public static int dbExecuteUpdateWithReturn(String sqlStmt) throws SQLException, ClassNotFoundException {
        int generatedKey = -1;
        Statement stmt = null;
        try {
            dbConnect();
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlStmt, Statement.RETURN_GENERATED_KEYS);

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedKey = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error(e.getStackTrace());
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            dbDisconnect();
        }
        return generatedKey;
    }

}
