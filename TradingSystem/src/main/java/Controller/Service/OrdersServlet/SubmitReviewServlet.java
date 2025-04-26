package Controller.Service.OrdersServlet;

import Controller.Dao.OrdersDao.SubmitReviewDao;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/SubmitReviewServlet")
public class SubmitReviewServlet extends HttpServlet {
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

        //收集前端传回来的修改后的数据
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
        int orderid = jsonObject.getInteger("orderid");
        int proid = jsonObject.getInteger("proid");
        String sellerid=jsonObject.getString("sellerid");
        String buyerid=jsonObject.getString("buyerid");
        int proscore = jsonObject.getInteger("productRating");
        int sellerscore = jsonObject.getInteger("sellerRating");
        String comment = jsonObject.getString("comment");

        SubmitReviewDao submitReviewDao = new SubmitReviewDao();
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        flag1= submitReviewDao.submitrReview(proid,sellerid,orderid,comment,buyerid,sellerscore,proscore);
        flag2=submitReviewDao.ChangeCredit(sellerid,sellerscore);
        flag3=submitReviewDao.ChangeProscore(proid);
        if(flag1 && flag2 && flag3){
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("message", "评价成功！");

            PrintWriter out = resp.getWriter();
            out.print(response.toJSONString());
            out.flush();
        }
        else{
            JSONObject response = new JSONObject();
            response.put("status", "false");
            response.put("message", "评价失败，请稍后重试！");

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
