<template>
    <div class="auth-container">
      <h1 class="auth-title">管理员登录</h1>
      <div class="auth-card">
        <form @submit.prevent="handleLogin" class="auth-form">
          <div class="form-group">
            <label for="admin-username">管理员账号</label>
            <input
              id="admin-username"
              v-model="form.username"
              type="text"
              placeholder="请输入管理员账号"
              required
            >
          </div>
  
          <div class="form-group">
            <label for="admin-password">密码</label>
            <input
              id="admin-password"
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              required
            >
          </div>
  
          <button type="submit" class="submit-btn">登录</button>
          
          <div class="back-to-user-login">
            <a href="#" @click.prevent="goToUserLogin">返回用户登录</a>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'AdminLogin',
    data() {
      return {
        form: {
          username: '',
          password: ''
        }
      }
    },
    methods: {
      async handleLogin() {
        try {
          const resp = await this.$http.post('/TradingSystem/AdminLogin', {
            username: this.form.username,
            password: this.form.password
          }, {
            headers: {
              'Content-Type': 'application/json'
            }
          });
          
          if(resp.data.status === "success") {
            // 存储管理员token
            localStorage.setItem('adminAuthjwt', resp.data.jwt);
            
            // 跳转到管理员后台
            const redirectPath = this.$route.query.redirect || '/AdminHome'
            this.$router.replace(redirectPath)
          } else {
            alert(resp.data.message);
          }
        } catch (error) {
          alert('登录失败，请检查账号密码');
          console.error("管理员登录失败:", error);
        }
      },
      goToUserLogin() {
        this.$router.push('/login');
      }
    }
  }
  </script>
  
  <style scoped>
  /* 复用原有的样式，可以添加一些管理员特有的样式 */
  .auth-container {
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .auth-title {
    text-align: center;
    margin-bottom: 20px;
    color: #333;
  }
  
  .auth-card {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .auth-form {
    display: flex;
    flex-direction: column;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 500;
  }
  
  .form-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
  }
  
  .submit-btn {
    background-color: #409EFF;
    color: white;
    padding: 10px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    margin-top: 10px;
  }
  
  .submit-btn:hover {
    background-color: #66b1ff;
  }
  
  .back-to-user-login {
    margin-top: 15px;
    text-align: center;
  }
  
  .back-to-user-login a {
    color: #666;
    font-size: 14px;
    text-decoration: none;
  }
  
  .back-to-user-login a:hover {
    color: #409EFF;
    text-decoration: underline;
  }
  
  .error {
    color: #f56c6c;
    font-size: 12px;
    margin-top: 5px;
    display: block;
  }
  </style>