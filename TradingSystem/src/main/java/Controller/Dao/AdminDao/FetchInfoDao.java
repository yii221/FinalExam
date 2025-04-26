package Controller.Dao.AdminDao;

import Controller.Dao.UtilsDao.JDBCutils;
import Module.Order;
import Module.Product;
import Module.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FetchInfoDao {
    // 查询所有用户
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM user"; // 你的用户表名

        try (Connection conn = JDBCutils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhonenumber(rs.getString("phonenumber"));
                user.setPassword(rs.getString("pwd"));
                user.setCreditscore(rs.getString("CreditScore"));
                user.setAvatar_path(rs.getString("avatar_path"));
                userList.add(user);
            }
        }
        return userList;
    }

    // 查询所有商品
    public List<Product> getAllProducts() throws SQLException {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM product"; // 你的商品表名

        try (Connection conn = JDBCutils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setProid(rs.getInt("proid"));
                product.setProname(rs.getString("proname"));
                product.setUserid(rs.getString("userid"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getString("price"));
                product.setCategory(rs.getString("category"));
                product.setPrimaryImage(rs.getString("primaryImage"));
                product.setStock(rs.getInt("stock"));
                product.setStatus(rs.getInt("status"));
                product.setProscore(rs.getInt("proscore"));
                productList.add(product);
            }
        }
        return productList;
    }

    public List<Order> getAllOrders() throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Connection conn = JDBCutils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setOrderid(rs.getInt("orderid"));
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
                order.setProImage(rs.getString("proimage"));
                order.setCreatedtime(rs.getTimestamp("createdtime"));
                orderList.add(order);
            }
        }
        return orderList;
    }
}
