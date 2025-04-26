package Controller.Service.AdminServlet;

import Controller.Dao.AdminDao.AdminLoginDao;
import Controller.Dao.UtilsDao.TokenUtils;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/AdminLogin")
public class AdminLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 处理跨域问题
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // 获取前端传来的 JSON 数据
        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        // 解析 JSON 数据
        JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
        String username = jsonObject.getString("username").trim();
        String password = jsonObject.getString("password").trim();
        System.out.println(username);
        System.out.println(password);

        try {
            // 调用 AdminLoginDao 验证用户名和密码
            AdminLoginDao adminLoginDao = new AdminLoginDao();
            boolean isValid = adminLoginDao.validateAdminLogin(username, password);

            // 返回结果
            if (isValid) {
                Map<String,Object> claims=new HashMap<>();
                claims.put("adminid",username);
                claims.put("adminpassword",password);
                String jwt = TokenUtils.generateJWT(claims);


                out.print("{\"status\":\"success\",\"message\":\"登录成功\",\"jwt\":\""+jwt+"\"}");
            } else {
                out.print("{\"status\":\"error\",\"message\":\"用户名或密码错误\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"服务器错误\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
