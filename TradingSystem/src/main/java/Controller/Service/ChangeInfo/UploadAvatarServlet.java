package Controller.Service.ChangeInfo;

import Controller.Dao.UtilsDao.JDBCutils;
import Module.User;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Controller.Dao.UtilsDao.TokenUtils.generateJWT;

@WebServlet("/UploadAvatar")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 2,      // 2 MB
        maxRequestSize = 1024 * 1024 * 10   // 10 MB
)
public class UploadAvatarServlet extends HttpServlet {
    // 数据库连接信息
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tradingsystem";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "060221";

    // 上传目录
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        //处理跨域问题
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie


        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            // 获取用户ID
            String userId = request.getParameter("userId");
            if (userId == null || userId.isEmpty()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "用户ID不能为空");
                out.print(jsonResponse.toString());
                return;
            }

            // 获取上传的文件
            Part filePart = request.getPart("avatar");
            if (filePart == null || filePart.getSize() == 0) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "请选择要上传的文件");
                out.print(jsonResponse.toString());
                return;
            }

            // 获取文件扩展名
            String fileName = getFileName(filePart);
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));

            // 生成唯一的文件名
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // 获取上传目录的绝对路径
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

            // 如果目录不存在则创建
            File uploadDir = new File(uploadFilePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            String filePath = uploadFilePath + File.separator + newFileName;
            try (InputStream fileContent = filePart.getInputStream()) {
                Files.copy(fileContent, Paths.get(filePath));
            }

            // 更新数据库中的头像路径
            String relativePath =  newFileName;
            User user = new User();
            user=updateAvatarInDatabase(userId, relativePath);
            if (user!=null) {
                // 生成新的JWT（如果需要包含头像信息）
                Map<String,Object> claims=new HashMap<>();
                claims.put("id",user.getId());
                claims.put("phonenumber",user.getPhonenumber());
                claims.put("name",user.getName());
                claims.put("email",user.getEmail());
                claims.put("avatar_path",user.getAvatar_path());
                claims.put("Creditscore",user.getCreditscore());

                String newJwt = generateJWT(claims);

                jsonResponse.put("status", "success");
                jsonResponse.put("message", "头像上传成功");
                jsonResponse.put("jwt", newJwt);
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "更新数据库失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "服务器错误: " + e.getMessage());
        } finally {
            out.print(jsonResponse.toString());
            out.close();
        }
    }

    private User updateAvatarInDatabase(String userId, String avatarPath) {
        Connection conn = null;
        PreparedStatement stmt = null;
        User user = new User();
        ResultSet rs = null;
        try {
            /*// 加载JDBC驱动
            Class.forName("com.mysql.jdbc.Driver");

            // 建立数据库连接
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);*/
            conn = JDBCutils.getConnection();
            // 准备SQL语句
            String sql = "UPDATE user SET avatar_path = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, avatarPath);
            stmt.setString(2, userId);

            // 执行更新
            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0) {
                String selectSql = "SELECT * FROM user WHERE id = ?";
                stmt = conn.prepareStatement(selectSql);
                stmt.setString(1, userId);
                rs = stmt.executeQuery();
                if(rs.next()) {
                    user.setId(rs.getString("id"));
                    user.setAvatar_path(rs.getString("avatar_path"));
                    user.setEmail(rs.getString("email"));
                    user.setName(rs.getString("name"));
                    user.setPhonenumber(rs.getString("phonenumber"));
                    user.setCreditscore(rs.getString("CreditScore"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(conn, stmt, rs);
        }
        return user;
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename=")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "unknown_" + System.currentTimeMillis() + ".jpg";
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

}
