import axios from 'axios'
import router from '@/router'

const http = axios.create({
  baseURL:'http://localhost:8080',
  withCredentials: true // 必须携带cookie
})

// 响应拦截器: 处理未登录情况
http.interceptors.response.use(config => {
    const token = localStorage.getItem("authjwt");
    console.log('当前 token:', token);
    if (token) {
        console.log(token);
      config.headers.Authorization = `Bearer ${token}`;
      console.log('请求头已添加 Authorization:', config.headers); // 调试：打印请求头
    }
    return config;
  },
    response => response,
    error => {
        if (error.response) {
            switch (error.response.status) {
              case 401: // Token 失效或未认证
                // 清除失效的 Token
                localStorage.removeItem('authjwt')
                // 跳转到登录页并携带当前路径
                router.replace({
                  path: '/login',
                  query: { redirect: router.currentRoute.fullPath }
                })
                break
              case 403: // 权限不足
                alert('权限不足，请联系管理员')
                break
            }
          }
      return Promise.reject(error);
    }
  );

export default http