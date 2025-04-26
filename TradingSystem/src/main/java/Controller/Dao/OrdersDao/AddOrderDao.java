package Controller.Dao.OrdersDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddOrderDao {
    public boolean AddOrder(int proid, String buyername, String phone, String address,String sellerid,int quantity,int total,String payment,String buyerid,String proname,String proImage) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();
            conn.setAutoCommit(false);

            // 1. 检查并锁定库存（悲观锁，使用SELECT ... FOR UPDATE在查询时直接加锁）
            String lockSql = "SELECT stock FROM product WHERE proid = ? FOR UPDATE";
            PreparedStatement lockPs = conn.prepareStatement(lockSql);
            lockPs.setInt(1, proid);
            rs = lockPs.executeQuery();
            if(rs.next()){
                int stock = rs.getInt("stock");
                if (stock < quantity) { // 检查库存是否足够
                    conn.rollback();
                    return false;
                }
            }

            // 2. 扣减库存
            String updateSql = "UPDATE product SET stock = stock - ? WHERE proid = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, quantity);
            updatePs.setInt(2, proid);
            updatePs.executeUpdate();

            String sql = "INSERT INTO orders (proid,proname,sellerid,quantity,total,payment,buyername,phone,address,buyerid,proImage) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, proid);
            stmt.setString(2, proname);
            stmt.setString(3, sellerid);
            stmt.setInt(4, quantity);
            stmt.setInt(5, total);
            stmt.setString(6, payment);
            stmt.setString(7, buyername);
            stmt.setString(8, phone);
            stmt.setString(9, address);
            stmt.setString(10, buyerid);
            stmt.setString(11, proImage);

            int i = stmt.executeUpdate();

            if(i>0){
                conn.commit();
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // 尝试回滚
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx); // 将回滚异常附加到主异常
            }
            e.printStackTrace();
            return false;
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
    }
}

