package Controller.Dao.ChangeInfoDao;

import Controller.Dao.UtilsDao.JDBCutils;
import Module.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordDao {
    public User ChangePassword(String id, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;

        try{
            conn = JDBCutils.getConnection();

            String sql = "UPDATE `user` SET `pwd`=?  WHERE `id` = ? ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, password);
            stmt.setString(2, id);

            int i = stmt.executeUpdate();

            if(i>0){
                    user = new User();
                    user.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return user;
    }
}
