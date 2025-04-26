package Controller.Dao.OrdersDao;

import Controller.Dao.UtilsDao.JDBCutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubmitReviewDao {
    public boolean submitrReview(int proid,String sellerid,int orderid,String comment,String buyerid,int sellerscore,int proscore) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            conn = JDBCutils.getConnection();

            String sql = "INSERT INTO `score` (`orderid`,`sellerid`,`buyerid`,`proscore`,`sellerscore`,`comment`,`proid`) VALUES (?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderid);
            stmt.setString(2, sellerid);
            stmt.setString(3, buyerid);
            stmt.setInt(4, proscore);
            stmt.setInt(5, sellerscore);
            stmt.setString(6, comment);
            stmt.setInt(7, proid);

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

    public boolean ChangeCredit(String sellerid,int sellerscore) {
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;
        double sumscore = 0;
        double num=0;
        double CreditScore=0;
        try{
            conn = JDBCutils.getConnection();

            String sql = "SELECT `sellerscore` FROM score WHERE `sellerid`=?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, sellerid);


            rs= stmt.executeQuery();

           while(rs.next()){
               double score = rs.getDouble("sellerscore");
               sumscore += score;
               num++;
           }

           CreditScore=sumscore/num;

            String sql2 = "UPDATE `user` SET `CreditScore`=? WHERE `id` = ? ";
            stmt2 = conn.prepareStatement(sql2);
            stmt2.setDouble(1, CreditScore);  // 第一个参数
            stmt2.setString(2, sellerid);     // 第二个参数

            int i = stmt2.executeUpdate();
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

    public boolean ChangeProscore(int proid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;
        int sumscore = 0;
        int num=0;
        int proscore=0;
        try{
            conn = JDBCutils.getConnection();

            String sql = "SELECT `proscore` FROM score WHERE `proid`=?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, proid);


            rs= stmt.executeQuery();

            while(rs.next()){
                int score = rs.getInt("proscore");
                sumscore += score;
                num++;
            }
            if (num == 0) {
                // 处理无评分的情况（例如设为默认值或抛出业务异常）
                proscore = 0; // 或其他默认值
            } else {
                proscore = sumscore / num;
            }


            String sql2 = "UPDATE `product` SET `proscore`=? WHERE `proid` = ? ";
            stmt2 = conn.prepareStatement(sql2);
            stmt2.setInt(1, proscore);
            stmt2.setInt(2, proid);

            int i = stmt2.executeUpdate();
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

