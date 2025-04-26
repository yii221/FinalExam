package Controller.Dao.ProDao;


import Controller.Dao.UtilsDao.JDBCutils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProImageUploadDao {
    public static boolean ProImageUploadDao(List<String> savedFiles, int proid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = JDBCutils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 插入所有图片到proimage表
            String sql = "INSERT INTO proimage (productid, imageurl) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);

            for (String fileName : savedFiles) {
                String imagePath = "ProImage/" + fileName.replace("\\","/");
                stmt.setInt(1, proid);
                stmt.setString(2, imagePath);
                stmt.addBatch(); // 添加到批处理
            }

            int[] batchResults = stmt.executeBatch(); // 执行批处理

            // 检查批处理是否全部成功
            boolean allBatchSuccess = true;
            for (int result : batchResults) {
                if (result <= 0) {
                    allBatchSuccess = false;
                    break;
                }
            }
            System.out.println(allBatchSuccess);

            if (allBatchSuccess && !savedFiles.isEmpty()) {
                // 2. 更新product表的primaryImage字段
                String updateSql = "UPDATE product SET primaryImage = ? WHERE proid = ?";
                stmt = conn.prepareStatement(updateSql);
                String firstFileName = savedFiles.get(0);
                String primaryImage = "ProImage/" + firstFileName;
                stmt.setString(1, primaryImage);
                stmt.setInt(2, proid);


                int updateResult = stmt.executeUpdate();
                if (updateResult > 0) {
                    conn.commit(); // 提交事务
                    success = true;
                } else {
                    conn.rollback(); // 回滚事务
                }
            } else {
                conn.rollback(); // 回滚事务
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // 出错时回滚
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // 恢复自动提交
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JDBCutils.release(conn, stmt, rs);
        }
        return success;
    }
}