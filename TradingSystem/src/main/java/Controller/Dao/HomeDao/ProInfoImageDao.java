package Controller.Dao.HomeDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProInfoImageDao {
    public List<String> fetchImage(int proid) {
        List<String> proImage = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "SELECT imageurl FROM proimage WHERE productid =?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, proid);

            rs = stmt.executeQuery();

            while (rs.next()) {
                String imageurl = rs.getString("imageurl");
                proImage.add(imageurl);

            }
            return proImage;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return proImage;
    }
}
