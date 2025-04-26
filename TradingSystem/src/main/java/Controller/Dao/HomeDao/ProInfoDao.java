package Controller.Dao.HomeDao;

import Controller.Dao.UtilsDao.JDBCutils;
import Module.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProInfoDao {
    public Product fetchProduct(int proid) {
        Product product = new Product();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "SELECT * FROM product WHERE proid =?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, proid);

            rs = stmt.executeQuery();

            if (rs.next()) {

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

                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return product;
    }
}
