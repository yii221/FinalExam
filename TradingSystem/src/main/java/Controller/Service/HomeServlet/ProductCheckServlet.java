package Controller.Service.HomeServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ProductCheckServlet")
public class ProductCheckServlet extends HttpServlet {
    // 数据库连接信息
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tradingsystem?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "060221";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        //处理跨域问题
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie
        // 设置响应内容类型为JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 获取请求参数
        String category = request.getParameter("category");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String statusStr = request.getParameter("status");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        // 验证用户登录状态
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            sendError(response, "未授权访问", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 解析参数
        Double minPrice = null;
        Double maxPrice = null;
        Integer status = null;

        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = Double.parseDouble(minPriceStr);
            }
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceStr);
            }
            if (statusStr != null && !statusStr.isEmpty()) {
                status = Integer.parseInt(statusStr);
            }
        } catch (NumberFormatException e) {
            sendError(response, "参数格式错误", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 构建SQL查询
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT p.*, u.name AS sellerName " +
                        "FROM product p " +
                        "JOIN user u ON p.userid = u.id " +
                        "WHERE 1=1"
        );

        // 添加筛选条件
        List<Object> params = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            sqlBuilder.append(" AND p.category = ?");
            params.add(category);
        }
        if (minPrice != null) {
            sqlBuilder.append(" AND p.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sqlBuilder.append(" AND p.price <= ?");
            params.add(maxPrice);
        }
        if (status != null) {
            sqlBuilder.append(" AND p.status = ?");
            params.add(status);
        }

        // 添加排序条件
        if (sortField != null && !sortField.isEmpty()) {
            String validSortField = getValidSortField(sortField);
            if (validSortField != null) {
                sqlBuilder.append(" ORDER BY ").append(validSortField);
                if ("desc".equalsIgnoreCase(sortOrder)) {
                    sqlBuilder.append(" DESC");
                } else {
                    sqlBuilder.append(" ASC");
                }
            }
        }

        // 执行查询
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            // 执行查询
            ResultSet rs = stmt.executeQuery();

            // 构建JSON响应
            JSONArray products = new JSONArray();
            while (rs.next()) {
                JSONObject product = new JSONObject();
                product.put("proid", rs.getInt("proid"));
                product.put("proname", rs.getString("proname"));
                product.put("category", rs.getString("category"));
                product.put("description", rs.getString("description"));
                product.put("price", rs.getDouble("price"));
                product.put("primaryImage", rs.getString("primaryImage"));
                product.put("status", rs.getInt("status"));
                product.put("stock", rs.getInt("stock"));
                product.put("proscore", rs.getDouble("proscore"));
                product.put("userid", rs.getString("userid"));

                products.put(product);
            }

            // 发送响应
            PrintWriter out = response.getWriter();
            out.print(products.toString());
            out.flush();

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, "数据库错误", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 验证排序字段是否有效，防止SQL注入
    private String getValidSortField(String field) {
        switch (field) {
            case "price":
                return "price";
            case "proscore":
                return "proscore";
            default:
                return null;
        }
    }

    private void sendError(HttpServletResponse response, String message, int status)
            throws IOException {
        response.setStatus(status);
        JSONObject error = new JSONObject();
        error.put("error", message);
        PrintWriter out = response.getWriter();
        out.print(error.toString());
        out.flush();
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