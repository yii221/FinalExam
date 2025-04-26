package Controller.Service.GoodsServlet;

import Controller.Dao.ProDao.ProAddDao;
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

@WebServlet("/AddProductServlet")
public class AddProductServlet extends HttpServlet {
    private ProAddDao ProAddDao= new ProAddDao();

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

        //收集前端传回来的修改后的数据
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
        //String proid = jsonObject.getString("id");
        String category=jsonObject.getString("category");
        String proname=jsonObject.getString("proname");
        String description=jsonObject.getString("description");
        String price=jsonObject.getString("price");
        int stock=jsonObject.getInteger("stock");
        int status=jsonObject.getInteger("status");



        //从token提取用户id
        String authHeader = httpRequest.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            JSONObject response = new JSONObject();
            response.put("ProcessStatus", "false");
            response.put("message", "认证信息失败，请重新登录！");

            PrintWriter out = resp.getWriter();
            out.print(response.toJSONString());
            out.flush();
            return;
        }
        String jwt = authHeader.substring(7);

        Map<String, Object> claims=new HashMap<>();
        claims= TokenUtils.parseJWT(jwt);
        String userid = (String) claims.get("id");

        //传入数据库
        int proid=ProAddDao.ProAdd(userid,proname,description, category, price,stock,status);
        System.out.println(proid);
        if(proid!=0){
            JSONObject response = new JSONObject();
            response.put("ProcessStatus", "success");
            response.put("message", "商品添加成功！");
            response.put("proid", proid);

            PrintWriter out = resp.getWriter();
            out.print(response.toJSONString());
            out.flush();
        }else{
            JSONObject response = new JSONObject();
            response.put("ProcessStatus", "false");
            response.put("message", "商品添加失败，请稍后重试！");

            PrintWriter out = resp.getWriter();
            out.print(response.toJSONString());
            out.flush();
        }

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
