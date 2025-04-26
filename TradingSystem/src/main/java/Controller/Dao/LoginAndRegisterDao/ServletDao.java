package Controller.Dao.LoginAndRegisterDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.*;

public class ServletDao {
    public boolean registerUser(String id, String phonenumber,String email,String username, String password, String avatarPath) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String checkSql = "SELECT id FROM user WHERE id = ?";
        String insertSql = "INSERT INTO user (id,phonenumber,email,name, pwd, avatar_path) VALUES (?,?,?,?, ?, ?)";

        try {
            conn = JDBCutils.getConnection();
            // 检查用户名是否已存在
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, id);

            rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false;
            }

            // 插入新用户
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, id);
                insertStmt.setString(2, phonenumber);
                insertStmt.setString(3, email);
                insertStmt.setString(4, username);
                insertStmt.setString(5, password); // 注意: 实际应用中应该加密密码
                insertStmt.setString(6, avatarPath);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
