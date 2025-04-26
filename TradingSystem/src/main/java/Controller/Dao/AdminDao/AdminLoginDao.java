package Controller.Dao.AdminDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class AdminLoginDao {
    public boolean validateAdminLogin(String username, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isValid = false;

        try {
            conn = JDBCutils.getConnection();  // 获取数据库连接
            String sql = "SELECT * FROM adminuser WHERE adminid = ? AND adminpwd = ?";  // 假设数据库表为 admin
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);  // 设置用户名
            stmt.setString(2, password);  // 设置密码

            rs = stmt.executeQuery();

            // 如果查询到一条记录，则表示用户名和密码匹配
            if (rs.next()) {
                isValid = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;  // 重新抛出异常，方便 Servlet 处理
        } finally {
            // 关闭数据库连接
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return isValid;
 }

}
