package Controller.Service.GoodsServlet;

import Controller.Dao.ProDao.EditProDao;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/EditProServlet")
public class EditProServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("SendSuccess!");

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
        String proname=jsonObject.getString("proname");
        String description=jsonObject.getString("description");
        String price=jsonObject.getString("price");
        String category=jsonObject.getString("category");
        String stock=jsonObject.getString("stock");
        String status=jsonObject.getString("status");
        int proid= Integer.parseInt(jsonObject.getString("proid"));
        System.out.println(proid);

        //进行数据库内的数据修改
        EditProDao editProDao = new EditProDao();
        boolean success=false;
        success= editProDao.EditProInfo(proid,proname,description,status,stock,category,price);
        System.out.println(success);

        //传输数据回前端
        if(success){
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("message", "信息修改成功！");
            PrintWriter out = resp.getWriter();
            out.print(response.toJSONString());
            out.flush();
        }
        else{
            JSONObject response = new JSONObject();
            response.put("status", "error");
            response.put("message", "信息修改失败，请稍后重试！");
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

}
