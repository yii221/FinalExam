package Controller.Dao.OrdersDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateOrderStatusDao {
        public boolean UpdateStatus(int orderid,String status) {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try{
                conn = JDBCutils.getConnection();

                String sql = "UPDATE `orders` SET `status`=? WHERE orderid = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, status);
                stmt.setInt(2, orderid);
                int i = stmt.executeUpdate();

                if(i>0){
                    return true;
                }else{
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                JDBCutils.release(conn, stmt, rs);
            }
            return false;
        }
}

