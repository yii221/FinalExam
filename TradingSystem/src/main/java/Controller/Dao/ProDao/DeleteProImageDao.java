package Controller.Dao.ProDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteProImageDao {
    public boolean DeleteProImage(int proid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "DELETE FROM proimage WHERE productid =? ";
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
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return false;
    }
}
