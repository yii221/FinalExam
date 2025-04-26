package Controller.Service.LoginAndRegister;

import Controller.Dao.LoginAndRegisterDao.ServletDao;
import Controller.Dao.UtilsDao.FileUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet{

    private static final String UPLOAD_DIRECTORY = "uploads";
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 10;      // 10MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50;   // 50MB

    private ServletDao ServletDao = new ServletDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie

        // 检查是否为多媒体上传
        if (!ServletFileUpload.isMultipartContent(request)) {
            sendError(response, "表单必须包含 enctype=multipart/form-data");
            return;
        }

        // 配置上传参数，创建 DiskFileItemFactory 对象，设置内存缓冲区大小和临时文件存储目录
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        //创建 ServletFileUpload 对象，设置单个文件的最大大小和整个请求的最大大小。
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // 构建上传路径，getServletContext().getRealPath("") 获取 webapp 目录的绝对路径。
        String uploadPath = getServletContext().getRealPath("/") + "uploads" + File.separator;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        Map<String, String> formFields = new HashMap<>();
        String fileName = null;

        try {
            // 解析请求内容，解析请求，获取所有表单字段和文件项。
            List<FileItem> formItems = upload.parseRequest(request);

            // 处理表单字段和文件
            for (FileItem item : formItems) {
                if (item.isFormField()) {
                    // 处理普通表单字段
                    formFields.put(item.getFieldName(), item.getString("UTF-8"));
                } else {
                    // 处理上传文件
                    if (!item.getName().isEmpty()) {
                        String originalFileName = new File(item.getName()).getName();
                        String fileExt = FileUtil.getFileExtension(originalFileName);
                        fileName = UUID.randomUUID().toString() + fileExt;
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        item.write(storeFile);
                    }
                }
            }

            // 验证必填字段
            if (formFields.get("username") == null || formFields.get("password") == null) {
                sendError(response, "用户名和密码不能为空");
                return;
            }

            // 注册用户
            boolean success = ServletDao.registerUser(
                    formFields.get("id"),
                    formFields.get("phonenumber"),
                    formFields.get("email"),
                    formFields.get("username"),
                    formFields.get("password"),
                    fileName
            );

            if (success) {
                sendSuccess(response, "注册成功");
            } else {
                sendError(response, "用户名已存在");
            }

        } catch (Exception ex) {
            sendError(response, "注册失败: " + ex.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"success\": false, \"message\": \"" + message + "\"}");
        out.flush();
    }

    private void sendSuccess(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"success\": true, \"message\": \"" + message + "\"}");
        out.flush();
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
