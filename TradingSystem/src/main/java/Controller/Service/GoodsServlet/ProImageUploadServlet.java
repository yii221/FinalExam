package Controller.Service.GoodsServlet;

import Controller.Dao.ProDao.ProImageUploadDao;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/ProImageUploadServlet")
public class ProImageUploadServlet extends HttpServlet {

    // 上传配置
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5;      // 5MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 20; // 20MB

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 处理跨域问题
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie

        // 设置响应内容类型和编码
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 检查请求是否为multipart/form-data
        if (!ServletFileUpload.isMultipartContent(request)) {
            JSONObject resp = new JSONObject();
            resp.put("status", "false");
            resp.put("message", "请选择图片！");

            PrintWriter out = response.getWriter();
            out.print(resp.toJSONString());
            out.flush();
            return;
        }

        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // 构建上传路径
        String uploadPath = getServletContext().getRealPath("/ProImage");
        if (!uploadPath.endsWith(File.separator)) {
            uploadPath += File.separator;
        }

        // 若不存在，创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // 解析请求内容
        List<FileItem> formItems = null;
        try {
            formItems = upload.parseRequest(request);
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }

        int proid = 0;
        List<String> savedFiles = new ArrayList<>();

        // 处理每个表单项
        for (FileItem item : formItems) {
            if (item.isFormField()) {
                // 处理普通字段
                if ("proid".equals(item.getFieldName())) {
                    proid = Integer.parseInt(item.getString());
                    System.out.println(proid);
                }
            } else {
                // 处理上传文件
                if (!item.getName().isEmpty()) {
                    String fileName = generateUniqueFileName(item.getName());
                    String filePath = uploadPath + File.separator + fileName;
                    File storeFile = new File(filePath);

                    // 保存文件到磁盘
                    try {
                        item.write(storeFile);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    savedFiles.add(fileName);
                }
            }
        }

        for (String fileName : savedFiles) {
            String imagePath = "ProImage/" + fileName.replace("\\","/");
            System.out.println(imagePath);
        }


        if (savedFiles.isEmpty()) {
            JSONObject resp = new JSONObject();
            resp.put("status", "false");
            resp.put("message", "请至少上传一张图片");

            PrintWriter out = response.getWriter();
            out.print(resp.toJSONString());
            out.flush();
            return;
        }
        
        // 保存到数据库
        boolean success = ProImageUploadDao.ProImageUploadDao(savedFiles, proid);

        // 返回成功响应
        JSONObject resp = new JSONObject();
        if (success) {
            resp.put("status", "success");
            resp.put("message", "商品图片添加成功");
        } else {
            resp.put("status", "false");
            resp.put("message", "商品图片添加失败");
        }

        PrintWriter out = response.getWriter();
        out.print(resp.toJSONString());
        out.flush();
    }

    private String generateUniqueFileName(String originalName) {
        String ext = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            ext = originalName.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + ext;
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization" +
                "");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}