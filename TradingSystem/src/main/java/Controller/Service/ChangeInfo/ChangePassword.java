package Controller.Service.ChangeInfo;

import Controller.Dao.ChangeInfoDao.ChangePasswordDao;
import Controller.Dao.LoginAndRegisterDao.LoginDao;
import Module.User;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/ChangePassword")
public class ChangePassword extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie

        // 设置响应内容类型和编码
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");


        try {
            //提取前端传回来的数据
            BufferedReader reader = req.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
            String id = jsonObject.getString("id");
            String oldPassword = jsonObject.getString("oldPassword");
            String newPassword = jsonObject.getString("newPassword");

            //验证旧密码正确性
            LoginDao userdao = new LoginDao();
            User user= new User();
            user=userdao.getUserByUsernameAndPassword(id,oldPassword);

            if(user==null){
                JSONObject response = new JSONObject();
                response.put("status", "false");
                response.put("message", "密码修改失败，请检查原密码是否错误！");

                PrintWriter out = resp.getWriter();
                out.print(response.toJSONString());
                out.flush();
            }
            else{
                ChangePasswordDao changePasswordDao = new ChangePasswordDao();
                User Newuser = new User();
                Newuser=changePasswordDao.ChangePassword(id, newPassword);
                if(Newuser!=null){
                    JSONObject response = new JSONObject();
                    response.put("status", "success");
                    response.put("message", "密码修改成功");

                    PrintWriter out = resp.getWriter();
                    out.print(response.toJSONString());
                    out.flush();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
