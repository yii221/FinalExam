package Controller.Service.LoginAndRegister;

import Controller.Dao.LoginAndRegisterDao.LoginDao;
import Controller.Dao.UtilsDao.TokenUtils;
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
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       //处理跨域问题
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie

        // 设置响应内容类型和编码
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // 读取和解析JSON
            BufferedReader reader = req.getReader();
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
            String id = jsonObject.getString("id");
            String password = jsonObject.getString("password");

            System.out.println("Received ID: " + id);
            System.out.println("Received psw: " + password);

            //验证数据
            LoginDao userdao = new LoginDao();
            User user= new User();
            user=userdao.getUserByUsernameAndPassword(id,password);

            // 返回响应
            if(user!=null){
                // 创建session并存储用户信息
                /*HttpSession session = req.getSession();
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60 * 60 * 24); // 30分钟过期*/
                Map<String,Object> claims=new HashMap<>();
                claims.put("id",user.getId());
                claims.put("phonenumber",user.getPhonenumber());
                claims.put("name",user.getName());
                claims.put("email",user.getEmail());
                claims.put("avatar_path",user.getAvatar_path());
                claims.put("Creditscore",user.getCreditscore());

                String jwt = TokenUtils.generateJWT(claims);

                resp.setContentType("application/json;charset=UTF-8");
                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("message", "登录成功！");
                response.put("jwt", jwt);

                PrintWriter out = resp.getWriter();
                out.print(response.toJSONString());
                out.flush();
            }else {
                JSONObject response = new JSONObject();
                response.put("status", "false");
                response.put("message", "登录失败，请检查学号和密码！");
                response.put("id", id);
                PrintWriter out = resp.getWriter();
                out.print(response.toJSONString());
            }




        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            e.printStackTrace();
        }
    }

    // 处理预检请求
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


