package Controller.Dao.ProDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditProDao
{
    public boolean EditProInfo(int proid,String proname,String description,String status,String stock,String category, String price ){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "UPDATE `product` SET `proname`=? , `description`=?, `status`=? ,`stock`=?, `category`=?, `price`=? WHERE `proid` = ? ";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, proname);
            stmt.setString(2, description);
            stmt.setString(3, status);
            stmt.setString(4, stock);
            stmt.setString(5, category);
            stmt.setString(6, price);
            stmt.setInt(7, proid);
            int i = stmt.executeUpdate();

            if(i>0){
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return false;
    }
}
