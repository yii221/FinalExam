package Controller.Dao.ProDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProAddDao {
    public int ProAdd(String userid, String name,String description, String category, String price, int stock, int status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String insertSql = "INSERT INTO product (userid,proname,description,price,category,stock,status) VALUES (?,?,?,?, ?,?,?)";
        int proid = 0;
        try {
            conn = JDBCutils.getConnection();
            // 检查用户名是否已存在
            stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, userid);
            stmt.setString(2, name);
            stmt.setString(3, description);
            stmt.setString(4, price);
            stmt.setString(5, category);
            stmt.setInt(6, stock);
            stmt.setInt(7, status);

            int i = stmt.executeUpdate();
            if (i > 0) {
                String selectSql = "SELECT * FROM product WHERE userid=? AND proname=? AND description=? AND price=? AND category=? ";
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setString(1, userid);
                selectStmt.setString(2, name);
                selectStmt.setString(3, description);
                selectStmt.setString(4, price);
                selectStmt.setString(5, category);
                rs = selectStmt.executeQuery();
                if (rs.next()) {
                    proid = rs.getInt("proid");
                    return proid;
                }

            } else {
                return proid;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return proid;
        }
     return proid;    }
}
