package Controller.Service.LoginAndRegister;

import Controller.Dao.LoginAndRegisterDao.ResetPasswordDao;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ResetPassword")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置字符编码和跨域请求头
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 设置响应类型
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // 读取请求体中的 JSON 数据
        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
        String email = jsonObject.getString("email").trim();
        String code = jsonObject.getString("code").trim();
        String newPassword = jsonObject.getString("newPassword").trim();

        try {
            // 1. 验证验证码
            if (!ForgotPasswordServlet.verifyCode(email, code)) {
                out.print("{\"status\":\"error\",\"message\":\"验证码错误或已过期\"}");
                return;
            }

            // 2. 更新密码
            ResetPasswordDao resetPasswordDao = new ResetPasswordDao();
            boolean success = resetPasswordDao.updatePassword(email, newPassword);  // 密码加密存储建议在这里处理

            if (success) {
                // 清除已使用的验证码
                ForgotPasswordServlet.removeVerificationCode(email);
                out.print("{\"status\":\"success\",\"message\":\"密码重置成功\"}");
            } else {
                out.print("{\"status\":\"error\",\"message\":\"密码重置失败\"}");
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
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 统一设置跨域响应头
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
