package Controller.Dao.OrdersDao;

import Controller.Dao.UtilsDao.JDBCutils;
import Module.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FetchSoldDao {
    public List<Order> fetchSold(String sellerid) {
        List<Order> orders = new ArrayList<Order>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "SELECT * FROM orders WHERE sellerid = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, sellerid);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setBuyerid(rs.getString("buyerid"));
                order.setProid(rs.getInt("proid"));
                order.setProname(rs.getString("proname"));
                order.setSellerid(rs.getString("sellerid"));
                order.setQuantity(rs.getInt("quantity"));
                order.setTotal(rs.getInt("total"));
                order.setPayment(rs.getString("payment"));
                order.setBuyername(rs.getString("buyername"));
                order.setPhone(rs.getString("phone"));
                order.setAddress(rs.getString("address"));
                order.setBuyerid(rs.getString("buyerid"));
                order.setStatus(rs.getString("status"));
                order.setProImage(rs.getString("proImage"));
                order.setCreatedtime(rs.getTimestamp("createdtime"));
                order.setOrderid(rs.getInt("orderid"));
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return orders;
    }
}
