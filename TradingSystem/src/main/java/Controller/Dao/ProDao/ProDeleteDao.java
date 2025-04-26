package Controller.Dao.ProDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProDeleteDao {
    public boolean DeletePro(int proid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "DELETE FROM `product` WHERE proid = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, proid);

            int i = stmt.executeUpdate();
            if(i>0){
                return true;
            }
            else {
                return false;
            }

    } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
    }

    public List<String> GetImagePath(int proid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> ImagePath = new ArrayList<String>();
        try {
            conn = JDBCutils.getConnection();
            String sql = "SELECT * FROM proimage WHERE productid =?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, proid);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String path = rs.getString("imageurl");

                ImagePath.add(path);

            }
            return ImagePath;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }

        return ImagePath;
    }

    public boolean DeleteImagePath(int proid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "DELETE FROM `proimage` WHERE productid = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, proid);

            int i = stmt.executeUpdate();
            if(i>0){
                return true;
            }
            else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
    }
}

