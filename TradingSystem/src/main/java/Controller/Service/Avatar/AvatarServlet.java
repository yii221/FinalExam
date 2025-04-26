package Controller.Service.Avatar;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/avatar/*")
public class AvatarServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie

        String filename = request.getPathInfo().substring(1);
        // 直接使用 webapp 下的 uploads 目录
        String uploadPath = getServletContext().getRealPath("/") + "uploads/";
        File file = new File(uploadPath, filename);

        System.out.println("尝试访问文件路径: " + file.getAbsolutePath()); // 调试用

        if (!file.exists()) {
            response.sendError(404, "头像文件不存在: " + filename);
            return;
        }
        }
    }

