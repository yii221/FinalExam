package Controller.Service.LoginAndRegister;


import Controller.Dao.LoginAndRegisterDao.ForgotPasswordDao;
import com.alibaba.fastjson2.JSONObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import java.util.Properties;
import java.util.Random;

@WebServlet("/ForgotPassword")
public class ForgotPasswordServlet extends HttpServlet {

    // 存储验证码和生成时间
    public static final Map<String, CodeInfo> verificationCodes = new HashMap<>();

    // 验证码有效时间（毫秒），15分钟
    private static final long CODE_VALIDITY_PERIOD = 15 * 60 * 1000;

    // 小类：封装验证码和生成时间
    public static class CodeInfo {
        String code;
        long timestamp;

        public CodeInfo(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
        String email = jsonObject.getString("email").trim(); // 加上trim
        System.out.println("接收到邮箱：" + email);

        try {
            ForgotPasswordDao forgotPasswordDao = new ForgotPasswordDao();
            boolean emailExists = forgotPasswordDao.checkEmailExists(email);

            if (!emailExists) {
                out.print("{\"status\":\"error\",\"message\":\"该邮箱未注册\"}");
                return;
            }

            String code = generateVerificationCode();
            verificationCodes.put(email, new CodeInfo(code, System.currentTimeMillis())); // 存验证码和时间

            sendVerificationEmail(email, code);

            out.print("{\"status\":\"success\",\"message\":\"验证码已发送\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"发送验证码失败\"}");
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendVerificationEmail(String toEmail, String code) throws MessagingException {
        String host = "smtp.qq.com";
        String username = "3168707230@qq.com";
        String password = "rujqvqzzyjpxdeif"; // 注意正式环境不要硬编码密码！

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("密码重置验证码");
        message.setText("您的密码重置验证码是: " + code + "\n\n验证码有效期为15分钟。");

        Transport.send(message);
    }

    // 验证验证码是否正确
    public static boolean verifyCode(String email, String code) {
        CodeInfo codeInfo = verificationCodes.get(email);
        if (codeInfo == null) {
            return false;
        }

        // 判断是否过期
        long currentTime = System.currentTimeMillis();
        if (currentTime - codeInfo.timestamp > CODE_VALIDITY_PERIOD) {
            verificationCodes.remove(email); // 过期清除
            return false;
        }

        return codeInfo.code.equals(code);
    }

    // 验证成功后清除验证码
    public static void removeVerificationCode(String email) {
        verificationCodes.remove(email);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
