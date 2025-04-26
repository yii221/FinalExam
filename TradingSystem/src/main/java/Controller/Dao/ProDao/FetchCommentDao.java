package Controller.Dao.ProDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FetchCommentDao {
    public List<String> FetchComment(int proid){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> commentlist = new ArrayList<String>();
        try{
            conn = JDBCutils.getConnection();

            String sql = "SELECT * FROM `score` WHERE proid = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, proid);

            rs = stmt.executeQuery();

            while(rs.next()){
                String comment = rs.getString("comment");
                commentlist.add(comment);

            }
            return commentlist;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return commentlist;
    }
}

