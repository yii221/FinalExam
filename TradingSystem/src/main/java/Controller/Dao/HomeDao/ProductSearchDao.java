package Controller.Dao.HomeDao;

import Controller.Dao.UtilsDao.JDBCutils;
import Module.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductSearchDao {
    public List<Product> SearchProduct(String keyword) {
        List<Product> productList = new ArrayList<Product>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "SELECT * FROM product WHERE proname LIKE ? OR description LIKE ? AND status = 0";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();

                product.setUserid(rs.getString("userid"));
                product.setProid(rs.getInt("proid"));
                product.setProname(rs.getString("proname"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getString("price"));
                product.setCategory(rs.getString("category"));
                product.setPrimaryImage(rs.getString("primaryImage"));
                product.setStock(rs.getInt("stock"));
                product.setStatus(rs.getInt("status"));
                product.setProscore(rs.getInt("proscore"));

                productList.add(product);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return productList;
    }
    }

