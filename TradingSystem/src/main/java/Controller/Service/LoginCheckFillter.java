package Controller.Service;

import Controller.Dao.UtilsDao.TokenUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// LoginCheckFilter.java
@WebFilter("/intercept") // 拦截所有请求
public class LoginCheckFillter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            System.out.println("请求已被拦截！");
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // 设置CORS头（与LoginServlet一致）
            httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization");

            //预检请求
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                chain.doFilter(request, response);
                return;
            }
            //1.获取请求的url
            String url = httpRequest.getParameter("targetUrl");

            //2.判断请求url中是否含有login
            if(url != null && (url.contains("login") || url.contains("register") || url.contains("uploads"))){
                chain.doFilter(request, response);
                return;
            }

            //3.获取请求头中的token
            String authHeader = httpRequest.getHeader("Authorization");
            //System.out.println(authHeader);

            // 检查Authorization头是否存在且格式正确
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                httpResponse.setContentType("application/json;charset=UTF-8");
                /*JSONObject responses = new JSONObject();
                responses.put("status", "unsuccess");
                responses.put("message", "未登录，请先登录！");

                PrintWriter out = httpResponse.getWriter();
                out.print(responses.toJSONString());
                out.flush();*/
                httpResponse.getWriter().write("{\"allow\": false}");
                return; // 确保后续代码不会执行
            }

            // 提取JWT令牌
            String jwt = authHeader.substring(7);
            //System.out.println(jwt);

            // 4.判断令牌是否存在，如果不存在则返回错误结果.检查JWT是否为空
            if (jwt.trim().isEmpty()) {
                httpResponse.setContentType("application/json;charset=UTF-8");
                //JSONObject responses = new JSONObject();
                //responses.put("status", "unsuccess");
                //responses.put("message", "令牌无效！");

                httpResponse.getWriter().write("{\"allow\": false}");
                //PrintWriter out = httpResponse.getWriter();
                //out.print(responses.toJSONString());
                //out.flush();
                return; // 确保后续代码不会执行
            }


            //5.解析token，如果解析失败了返回错误的结果
            try {
                TokenUtils.parseJWT(jwt);
            } catch (Exception e) {//jwt解析失败
                e.printStackTrace();
                httpResponse.setContentType("application/json;charset=UTF-8");
                //JSONObject responses = new JSONObject();
                //responses.put("status", "unsuccess");
                //responses.put("message", "未登录，请先登录！");

                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"allow\": false}");
                //PrintWriter out = httpResponse.getWriter();
                //out.print(responses.toJSONString());
                //out.flush();
                return;
            }

            //6.放行
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"allow\": true}");
            //chain.doFilter(request, response);

        }


        private void sendJsonError(HttpServletResponse response, String message, int status)
                throws IOException {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(status);
            response.getWriter().write("{\"error\":\"" + message + "\"}");
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {}

        @Override
        public void destroy() {}
    }

