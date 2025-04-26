package Controller.Dao.ChangeInfoDao;

import Controller.Dao.UtilsDao.JDBCutils;
import Module.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ChangeDao {
    public User ChangeInfo(Map<String, Object> claims, String id){
        //System.out.println(id);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;
        String phonenumber = (String) claims.get("phonenumber");
        String email = (String) claims.get("email");
        String name = (String) claims.get("name");
        try{
            conn = JDBCutils.getConnection();

            String sql = "UPDATE `user` SET `phonenumber`=? , `email`=?, `name`=?  WHERE `id` = ? ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, phonenumber);
            stmt.setString(2, email);
            stmt.setString(3, name);
            stmt.setString(4, id);

            int i = stmt.executeUpdate();

            if(i>0){
                String selectSql = "SELECT * FROM `user` WHERE id = ? ";
                stmt = conn.prepareStatement(selectSql);
                stmt.setString(1, id);
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }

        return user;
    }
}
