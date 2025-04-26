package Controller.Service.GoodsServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/ProImage/*")
public class ProImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie


        try {
            // 从URL中提取图片文件名
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image ID not provided");
                return;
            }

            String imageId = pathInfo.substring(1); // 去掉前面的斜杠

            // 获取ServletContext
            ServletContext context = getServletContext();

            // 构建图片的完整路径
            String fullPath = context.getRealPath("/ProImage/" + imageId);
            if (fullPath == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image directory not found");
                return;
            }

            Path imagePath = Paths.get(fullPath);

            // 检查文件是否存在
            if (!Files.exists(imagePath) || !Files.isRegularFile(imagePath)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
                return;
            }

            // 设置响应头
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "image/*"; // 默认类型
            }

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=\"" + imageId + "\"");

            // 流式传输文件内容
            try (FileInputStream in = new FileInputStream(imagePath.toFile());
                 OutputStream out = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving image");
            e.printStackTrace();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}