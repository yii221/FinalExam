package Controller.Service.HomeServlet;

import Controller.Dao.HomeDao.ProInfoDao;
import Module.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/ProInfoServlet")
public class ProInfoServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 统一设置响应头
        setCorsHeaders(resp);
        resp.setContentType("application/json;charset=UTF-8");

        try {
            // 读取并解析JSON请求体
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            if (jsonObject == null || !jsonObject.has("proid")) {
                sendErrorResponse(resp, 400, "缺少proid参数");
                return;
            }

            int proid = jsonObject.get("proid").getAsInt();
            //System.out.println("proid:" + proid);
            Product product = new ProInfoDao().fetchProduct(proid);

            if (product == null) {
                sendErrorResponse(resp, 404, "未找到指定商品");
                return;
            }

            // 统一使用Gson序列化
            resp.getWriter().write(gson.toJson(product));

        } catch (Exception e) {
            sendErrorResponse(resp, 500, "服务器错误: " + e.getMessage());
        }
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }

    private void sendErrorResponse(HttpServletResponse resp, int code, String message)
            throws IOException {
        resp.setStatus(code);
        JsonObject error = new JsonObject();
        error.addProperty("code", code);
        error.addProperty("message", message);
        resp.getWriter().write(gson.toJson(error));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}