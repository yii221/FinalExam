package Controller.Service.ChangeInfo;

import Controller.Dao.ChangeInfoDao.ChangeDao;
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

@WebServlet("/ChangePerson")
public class ChangePersonServlet extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) resp;

        req.setCharacterEncoding("UTF-8");
        //处理跨域问题
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie

        // 设置响应内容类型和编码
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        //String authHeader = httpRequest.getHeader("Authorization");
        //String jwt = authHeader.substring(7);

        /*//提取出token的原用户数据
        Map<String, Object> Originalclaims=new HashMap<>();
        Originalclaims=TokenUtils.parseJWT(jwt);
        String orignalId = (String) Originalclaims.get("id");*/

        //收集前端传回来的修改后的数据
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
        String id = jsonObject.getString("id");
        Map<String,Object> claims=new HashMap<>();
        claims.put("phonenumber",jsonObject.getString("phonenumber"));
        claims.put("name",jsonObject.getString("name"));
        claims.put("email",jsonObject.getString("email"));

        //新数据数据库更新
        ChangeDao changeDao=new ChangeDao();
        User user= new User();
        user=changeDao.ChangeInfo(claims,id);

        //新数据存入token
        Map<String,Object> Newclaims=new HashMap<>();
        Newclaims.put("id",user.getId());
        Newclaims.put("phonenumber",user.getPhonenumber());
        Newclaims.put("name",user.getName());
        Newclaims.put("email",user.getEmail());
        Newclaims.put("avatar_path",user.getAvatar_path());
        Newclaims.put("Creditscore",user.getCreditscore());

        String Newjwt = TokenUtils.generateJWT(claims);

        resp.setContentType("application/json;charset=UTF-8");
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", "信息修改成功！");
        response.put("jwt", Newjwt);

        PrintWriter out = resp.getWriter();
        out.print(response.toJSONString());
        out.flush();
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
