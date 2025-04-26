package Controller.Dao.LoginAndRegisterDao;

import Controller.Dao.UtilsDao.JDBCutils;
import Module.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {
    public User getUserByUsernameAndPassword(String id, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "SELECT * FROM user WHERE id = ? AND pwd = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPhonenumber(rs.getString("phonenumber"));
                user.setEmail(rs.getString("email"));
                user.setAvatar_path(rs.getString("avatar_path"));
                user.setCreditscore(rs.getString("CreditScore"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }

        if(user == null){
            try{
                conn = JDBCutils.getConnection();

                String sql = "SELECT * FROM user WHERE phonenumber = ? AND pwd = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, id);
                stmt.setString(2, password);

                rs = stmt.executeQuery();

                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getString("id"));
                    user.setPhonenumber(rs.getString("phonenumber"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("pwd"));
                    user.setAvatar_path(rs.getString("avatar_path"));
                    user.setCreditscore(rs.getString("CreditScore"));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                JDBCutils.release(conn, stmt, rs);
            }
        }

        if(user == null){
            try{
                conn = JDBCutils.getConnection();

                String sql = "SELECT * FROM user WHERE email = ? AND pwd = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, id);
                stmt.setString(2, password);

                rs = stmt.executeQuery();

                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getString("id"));
                    user.setPhonenumber(rs.getString("phonenumber"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("pwd"));
                    user.setAvatar_path(rs.getString("avatar_path"));
                    user.setCreditscore(rs.getString("CreditScore"));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                JDBCutils.release(conn, stmt, rs);
            }
        }
        return user;
    }
}
