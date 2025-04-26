package Controller.Dao.LoginAndRegisterDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResetPasswordDao {
    public boolean updatePassword(String email, String newPassword) {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = JDBCutils.getConnection();
            String sql = "UPDATE user SET pwd = ? WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword); // 实际项目中应该加密存储
            stmt.setString(2, email);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected>0)
                return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }

    }
}
