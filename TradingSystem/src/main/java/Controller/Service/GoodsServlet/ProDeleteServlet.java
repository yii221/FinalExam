package Controller.Service.GoodsServlet;

import Controller.Dao.ProDao.ProDeleteDao;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/DeleteProServlet")
public class ProDeleteServlet extends HttpServlet {
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


        //获取json数据
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        //获取数据
        JSONObject jsonObject = JSONObject.parseObject(jsonBuilder.toString());
        int proid=0;
        proid = Integer.parseInt(jsonObject.getString("proid"));
        //System.out.println(proid);
        ProDeleteDao proDeleteDao = new ProDeleteDao();

        //删除图片文件，避免占用过多的空间
        String serverRealPath = req.getServletContext().getRealPath("/"); // 获取 webapp 根目

        //获取数据库中图片的url
        List<String> ImagePath = new ArrayList<String>();
        try {
            ImagePath=proDeleteDao.GetImagePath(proid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //删除文件夹下的file
        boolean delete=true;
        if(ImagePath.size()==0){
            delete=false;
        }
        for (String imagePath : ImagePath) {
            String fullPath = serverRealPath + imagePath;
            File file = new File(fullPath);
            if (file.exists() && file.delete()) {
                delete=true;
            }
            else{
                delete=false;
            }
        }

        //删除proimage表中的图片地址
        boolean deleteImage=false;
        try {
            deleteImage= proDeleteDao.DeleteImagePath(proid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //删除product表中的数据
        boolean success = false;
        success= proDeleteDao.DeletePro(proid);

        if(delete && deleteImage && success) {
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("message", "删除成功！");

            PrintWriter out = resp.getWriter();
            out.print(response.toJSONString());
        }
        else{
            JSONObject response = new JSONObject();
            response.put("status", "false");
            response.put("message", "删除失败，请稍后重试！");

            PrintWriter out = resp.getWriter();
            out.print(response.toJSONString());
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
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
