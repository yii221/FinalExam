package Controller.Service.OrdersServlet;

import Controller.Dao.OrdersDao.AddOrderDao;
import Controller.Dao.UtilsDao.TokenUtils;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@WebServlet("/AddOrderServlet")
public class AddOrderServlet extends HttpServlet {

    private Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        //处理跨域问题
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie

        // 设置响应内容类型和编码
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        //从请求头获取 JWT 并解析用户ID
        String authHeader = req.getHeader("Authorization");
        System.out.println(authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请重新登录！");
            return;
        }
        String jwt = authHeader.substring(7);
        Map<String, Object> claims = TokenUtils.parseJWT(jwt);
        String buyerid = (String) claims.get("id");

        //收集前端传回来的修改后的数据
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
        int proid=Integer.parseInt(jsonObject.getString("proid"));
        String proname=jsonObject.getString("proname");
        String sellerid=jsonObject.getString("sellerid");
        int quantity=Integer.parseInt(jsonObject.getString("quantity"));
        int total=Integer.parseInt(jsonObject.getString("total"));
        String payment=jsonObject.getString("payment");
        String buyername=jsonObject.getString("buyername");
        String phone=jsonObject.getString("phone");
        String address=jsonObject.getString("address");
        String proImage=jsonObject.getString("proImage");

        AddOrderDao addOrderDao = new AddOrderDao();
        boolean success = false;
        success=addOrderDao.AddOrder(proid, buyername,phone,address,sellerid,quantity,total,payment,buyerid,proname,proImage);
        if (success) {
            resp.getWriter().write(gson.toJson(success));
        }
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

    private void sendErrorResponse(HttpServletResponse resp, int code, String message)
            throws IOException {
        resp.setStatus(code);
        JsonObject error = new JsonObject();
        error.addProperty("code", code);
        error.addProperty("message", message);
        resp.getWriter().write(gson.toJson(error));
    }
}
