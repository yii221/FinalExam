
<template>
    <div class="auth-container">
     <h1 class="auth-title">登录系统</h1>
      <div class="auth-card"> 
        <div class="auth-tabs">
          <button 
            @click="activeTab = 'login'"
            :class="{ 'active': activeTab === 'login' }"
          >
            登录
          </button>
          <button 
            @click="activeTab = 'register'"
            :class="{ 'active': activeTab === 'register' }"
          >
            注册
          </button>
        </div>
  
        <!-- 登录表单 -->
        <form 
          v-if="activeTab === 'login'" 
          @submit.prevent="handleLogin"
          class="auth-form"
        >
          <div class="form-group">
            <label for="login-id">学号/手机号/邮箱</label>
            <input
              id="login-id"
              v-model="login.id"
              type="text"
              placeholder="请输入学号/手机号/邮箱"
            >
            <span class="error" v-if="loginErrors.id">{{ loginErrors.id }}</span>
          </div>
  
          <div class="form-group">
            <label for="login-password">密码</label>
            <input
              id="login-password"
              v-model="login.password"
              type="password"
              placeholder="请输入密码"
              @blur="validateLoginPassword"
            >
            <span class="error" v-if="loginErrors.password">{{ loginErrors.password }}</span>
          </div>
  
          <button type="submit" class="submit-btn">登录</button>
          <!-- 添加忘记密码链接 -->
          <div class="forgot-password">
            <a href="#" @click.prevent="showForgotPassword = true">忘记密码?</a>
          </div>
          <div class="admin-login-link" v-if="activeTab === 'login'">
          <a href="#" @click.prevent="goToAdminLogin">管理员登录</a>
          </div>
        </form>
  
        <!-- 注册表单 -->
        <form 
          v-else 
          @submit.prevent="handleRegister"
          class="auth-form"
        > <div class="form-group"> 
            <label for="register-id">学号</label>
            <input
              id="register-id"
              v-model="register.id"
              type="id"
              placeholder="请输入学号"
              @blur="validateId"
            >
            <span class="error" v-if="registerErrors.id">{{ registerErrors.id }}</span>
          </div>
           
          <div class="form-group"> 
            <label for="register-name">名字</label>
            <input
              name="register-name"
              v-model="register.name"
              type="name"
              placeholder="请输入姓名"
              @blur="validateName"
            >
            <span class="error" v-if="registerErrors.name">{{ registerErrors.name }}</span>
          </div>

          <div class="form-group"> 
            <label for="register-email">邮箱</label>
            <input
              email="register-email"
              v-model="register.email"
              type="email"
              placeholder="请输入邮箱"
              @blur="validateEmail"
            >
            <span class="error" v-if="registerErrors.email">{{ registerErrors.email }}</span>
          </div>
  
          <div class="form-group">
            <label for="register-phone">手机号</label>
            <input
              phone="register-phone"
              v-model="register.phone"
              type="tel"
              placeholder="请输入手机号"
              @blur="validatePhone"
            >
            <span class="error" v-if="registerErrors.phone">{{ registerErrors.phone }}</span>
          </div>
  
          <div class="form-group">
            <label for="register-password">密码</label>
            <input
              id="register-password"
              v-model="register.password"
              type="password"
              placeholder="请输入密码"
              @blur="validatePassword"
            >
            <span class="error" v-if="registerErrors.password">{{ registerErrors.password }}</span>
          </div>
  
          <div class="form-group">
            <label for="register-confirm-password">确认密码</label>
            <input
              id="register-confirm-password"
              v-model="register.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              @blur="validateConfirmPassword"
            >
            <span class="error" v-if="registerErrors.confirmPassword">{{ registerErrors.confirmPassword }}</span>
          </div>

          <div class="form-group">
            <label>头像</label>
             <avatar-upload v-model="register.avatar"></avatar-upload>
          </div>
            
          
            <button type="submit" class="submit-btn">注册</button>
        
        </form>


        <!-- 忘记密码表单 -->
        <div v-if="showForgotPassword" class="forgot-password-modal">
          <div class="modal-content">
            <h2>找回密码</h2>
            <form @submit.prevent="handleForgotPassword">
              <div class="form-group">
                <label for="forgot-email">注册邮箱</label>
                <input
                  id="forgot-email"
                  v-model="forgotPassword.email"
                  type="email"
                  placeholder="请输入注册时使用的邮箱"
                  @blur="validateForgotEmail"
                >
                <span class="error" v-if="forgotPasswordErrors.email">{{ forgotPasswordErrors.email }}</span>
              </div>
              
              <div class="form-group" v-if="showResetForm">
                <label for="reset-code">验证码</label>
                <input
                  id="reset-code"
                  v-model="forgotPassword.code"
                  type="text"
                  placeholder="请输入收到的验证码"
                >
                <span class="error" v-if="forgotPasswordErrors.code">{{ forgotPasswordErrors.code }}</span>
              </div>
              
              <div class="form-group" v-if="showResetForm">
                <label for="new-password">新密码</label>
                <input
                  id="new-password"
                  v-model="forgotPassword.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  @blur="validateNewPassword"
                >
                <span class="error" v-if="forgotPasswordErrors.newPassword">{{ forgotPasswordErrors.newPassword }}</span>
              </div>
              
              <div class="form-group" v-if="showResetForm">
                <label for="confirm-new-password">确认新密码</label>
                <input
                  id="confirm-new-password"
                  v-model="forgotPassword.confirmNewPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  @blur="validateConfirmNewPassword"
                >
                <span class="error" v-if="forgotPasswordErrors.confirmNewPassword">{{ forgotPasswordErrors.confirmNewPassword }}</span>
              </div>
              
              <div class="button-group">
                <button 
                  type="button" 
                  class="cancel-btn"
                  @click="showForgotPassword = false"
                >
                  取消
                </button>
                <button 
                  type="submit" 
                  class="submit-btn"
                >
                  {{ showResetForm ? '重置密码' : '发送验证码' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import AvatarUpload from '@/components/AvatarUpload.vue'
  import { register } from '@/api/user'
  

  export default {
    name:'TheLogin',
    components: {
      AvatarUpload
    },
    mounted() {
    // 从URL获取redirect参数
    const redirect = this.$route.query.redirect;
    if (redirect) {
      // 存储起来，登录成功后使用
      this.redirectPath = redirect;
      }
    },
    //name: 'AuthForm',
    data() {
      return {
        showForgotPassword: false,  
        showResetForm: false,      
        forgotPassword: {
          email: '',
          code: '',
          newPassword: '',
          confirmNewPassword: ''
        },
        forgotPasswordErrors: {
          email: '',
          code: '',
          newPassword: '',
          confirmNewPassword: ''
        },
        activeTab: 'login',
        login: {
          id: '',
          password: '',
          result:null
        },
        register: {
          id:'',
          name:'',
          email: '',
          phone: '',
          password: '',
          confirmPassword: '',
          avatar: null
        },
        loginErrors: {
          email: '',
          password: ''
        },
        registerErrors: {
          id:'',
          name:'',
          email: '',
          phone: '',
          password: '',
          confirmPassword: ''
        },
        rules:{
          avatar: [
              { required: true, message: '请上传头像', trigger: 'change' }
            ] 
          }
      }
    },
    
    methods: {
      goToAdminLogin() {
            this.$router.push('/AdminLogin');
        },
        // 新增忘记密码方法
        async handleForgotPassword() {
          if (!this.showResetForm) {
            // 第一步：发送验证码
            if (this.validateForgotEmail()) {
              try {
                const resp = await this.$http.post('/TradingSystem/ForgotPassword', {
                  email: this.forgotPassword.email
                }, {
                  headers: {
                    'Content-Type': 'application/json'
                  }
                });
                
                if (resp.data.status === "success") {
                  this.showResetForm = true;
                  alert('验证码已发送到您的邮箱，请查收');
                } else {
                  alert(resp.data.message);
                }
              } catch (error) {
                alert('发送验证码失败，请稍后重试');
                console.error(error);
              }
            }
          } else {
            // 第二步：重置密码
            if (this.validateResetPasswordForm()) {
              try {
                const resp = await this.$http.post('/TradingSystem/ResetPassword', {
                  email: this.forgotPassword.email,
                  code: this.forgotPassword.code,
                  newPassword: this.forgotPassword.newPassword
                }, {
                  headers: {
                    'Content-Type': 'application/json'
                  }
                });
                
                if (resp.data.status === "success") {
                  alert('密码重置成功，请使用新密码登录');
                  this.showForgotPassword = false;
                  this.showResetForm = false;
                  this.resetForgotPasswordForm();
                } else {
                  alert(resp.data.message);
                }
              } catch (error) {
                alert('密码重置失败，请稍后重试');
                console.error(error);
              }
            }
          }
        },

        validateForgotEmail() {
          let isValid = true;
          if (!this.forgotPassword.email) {
            this.forgotPasswordErrors.email = '请输入邮箱';
            isValid = false;
          } else if (!this.isValidEmail(this.forgotPassword.email)) {
            this.forgotPasswordErrors.email = '邮箱格式不正确';
            isValid = false;
          } else {
            this.forgotPasswordErrors.email = '';
          }
          return isValid;
        },
        
        validateNewPassword() {
          let isValid = true;
          if (!this.forgotPassword.newPassword) {
            this.forgotPasswordErrors.newPassword = '请输入新密码';
            isValid = false;
          } else if (this.forgotPassword.newPassword.length < 6) {
            this.forgotPasswordErrors.newPassword = '密码长度不能少于6位';
            isValid = false;
          } else {
            this.forgotPasswordErrors.newPassword = '';
          }
          return isValid;
        },
        
        validateConfirmNewPassword() {
          let isValid = true;
          if (!this.forgotPassword.confirmNewPassword) {
            this.forgotPasswordErrors.confirmNewPassword = '请确认新密码';
            isValid = false;
          } else if (this.forgotPassword.newPassword !== this.forgotPassword.confirmNewPassword) {
            this.forgotPasswordErrors.confirmNewPassword = '两次输入的密码不一致';
            isValid = false;
          } else {
            this.forgotPasswordErrors.confirmNewPassword = '';
          }
          return isValid;
        },
        
        validateResetPasswordForm() {
          let isValid = this.validateForgotEmail() && 
                       this.validateNewPassword() && 
                       this.validateConfirmNewPassword();
          
          if (!this.forgotPassword.code) {
            this.forgotPasswordErrors.code = '请输入验证码';
            isValid = false;
          } else {
            this.forgotPasswordErrors.code = '';
          }
          
          return isValid;
        },
        
        resetForgotPasswordForm() {
          this.forgotPassword = {
            email: '',
            code: '',
            newPassword: '',
            confirmNewPassword: ''
          };
          this.forgotPasswordErrors = {
            email: '',
            code: '',
            newPassword: '',
            confirmNewPassword: ''
          };
        },
        async handleLogin(){
            try{
                if (this.validateLoginForm())
                {
                     const resp = await this.$http.post('/TradingSystem/login',{
                        id:this.login.id,
                        password:this.login.password
                     }   ,{
                     headers: {
                            'Content-Type': 'application/json'
                        },
                    
                     });
                    this.result=resp.data; 
                    if(resp.data.status === "success") {
                        alert(resp.data.message);
                        //存储token到localStorage
                        localStorage.setItem('authjwt', resp.data.jwt);
                        console.log(localStorage.getItem);
                        
                        const redirectPath = this.$route.query.redirect || '/TheHome'
                        this.$router.replace(redirectPath)
                    } else {
                            alert(resp.data.message);
                       }
                    // 登录成功后跳转到首页
                }
            }
            catch (error)  {
                let errorMessage = "登录失败";
                if (error.response) {
                // 服务器返回了错误（如 401、500）
                 errorMessage = error.response.data?.message || `服务器错误: ${error.response.status}`;
                } else if (error.request) {
                        // 请求已发送但无响应（如网络问题）
                         errorMessage = "无法连接到服务器";
                    } else {
                        // 其他错误（如代码异常）
                        errorMessage = error.message;
                        alert(errorMessage);
                        console.error("登录失败详情:", error);
                    }
            }
      },
      async handleRegister() {
        if (this.validateRegisterForm()) {
            try {
            const formData = new FormData()
            formData.append('username', this.register.name)
            formData.append('phonenumber', this.register.phone)
            formData.append('email', this.register.email)
            formData.append('id', this.register.id)
            formData.append('password', this.register.password)
            formData.append('avatar', this.register.avatar)
            
            await register(formData)
            alert("注册成功，请返回登录页面登录！")
          } catch (error) {
            this.$message.error(error.message || '注册失败')
          }
          
        }
      },
      validateLoginForm() {
        let isValid = true;
        
        if (!this.login.id) {
          this.loginErrors.id = '请输入学号/邮箱/手机号';
          isValid = false;
        } else {
          this.loginErrors.id= '';
        }
        
        if (!this.login.password) {
          this.loginErrors.password = '请输入密码';
          isValid = false;
        } else {
          this.loginErrors.password = '';
        }
        
        return isValid;
      },
      validateRegisterForm() {
        let isValid = true;
        if (!this.register.id) {
          this.registerErrors.id = '请输入学号';
          isValid = false;
        } else {
          this.registerErrors.id = '';
        }

        if (!this.register.name) {
          this.registerErrors.name = '请输入名字';
          isValid = false;
        } else {
          this.registerErrors.name = '';
        }

        if (!this.register.email) {
          this.registerErrors.email = '请输入邮箱';
          isValid = false;
        } else if (!this.isValidEmail(this.register.email)) {
          this.registerErrors.email = '邮箱格式不正确';
          isValid = false;
        } else {
          this.registerErrors.email = '';
        }
        
        if (!this.register.phone) {
          this.registerErrors.phone = '请输入手机号';
          isValid = false;
        } else if (!this.isValidPhone(this.register.phone)) {
          this.registerErrors.phone = '手机号格式不正确';
          isValid = false;
        } else {
          this.registerErrors.phone = '';
        }
        
        if (!this.register.password) {
          this.registerErrors.password = '请输入密码';
          isValid = false;
        } else if (this.register.password.length < 6) {
          this.registerErrors.password = '密码长度不能少于6位';
          isValid = false;
        } else {
          this.registerErrors.password = '';
        }
        
        if (!this.register.confirmPassword) {
          this.registerErrors.confirmPassword = '请确认密码';
          isValid = false;
        } else if (this.register.password !== this.register.confirmPassword) {
          this.registerErrors.confirmPassword = '两次输入的密码不一致';
          isValid = false;
        } else {
          this.registerErrors.confirmPassword = '';
        }
        
        return isValid;
      },
      validateLoginEmail() {
        if (!this.login.email) {
          this.loginErrors.email = '请输入邮箱或手机号';
        } else {
          this.loginErrors.email = '';
        }
      },
      validateLoginPassword() {
        if (!this.login.password) {
          this.loginErrors.password = '请输入密码';
        } else {
          this.loginErrors.password = '';
        }
      },
      validateId() {
        if (!this.register.id) {
          this.registerErrors.id = '请输入学号';
        }  else {
          this.registerErrors.id = '';
        }
      },
      validateEmail() {
        if (!this.register.email) {
          this.registerErrors.email = '请输入邮箱';
        } else if (!this.isValidEmail(this.register.email)) {
          this.registerErrors.email = '邮箱格式不正确';
        } else {
          this.registerErrors.email = '';
        }
      },
      validateName(){
        if (!this.register.name) {
          this.registerErrors.name = '请输入名字';
        } else {
          this.registerErrors.name = '';
        }
      },
      validatePhone() {
        if (!this.register.phone) {
          this.registerErrors.phone = '请输入手机号';
        } else if (!this.isValidPhone(this.register.phone)) {
          this.registerErrors.phone = '手机号格式不正确';
        } else {
          this.registerErrors.phone = '';
        }
      },
      validatePassword() {
        if (!this.register.password) {
          this.registerErrors.password = '请输入密码';
        } else if (this.register.password.length < 6) {
          this.registerErrors.password = '密码长度不能少于6位';
        } else {
          this.registerErrors.password = '';
        }
      },
      validateConfirmPassword() {
        if (!this.register.confirmPassword) {
          this.registerErrors.confirmPassword = '请确认密码';
        } else if (this.register.password !== this.register.confirmPassword) {
          this.registerErrors.confirmPassword = '两次输入的密码不一致';
        } else {
          this.registerErrors.confirmPassword = '';
        }
      },
      isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
      },
      isValidPhone(phone) {
        const re = /^1[3-9]\d{9}$/;
        return re.test(phone);
      },
      
      
    }
}
  </script>
  
  <style scoped>
  .auth-container {
    display: flex;
    flex-direction: column; /* 改为垂直布局 */
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background-color: #f5f5f5;
    font-family: 'Arial', sans-serif;
    padding: 20px; /* 添加内边距 */
  }
  
  .auth-title {
    font-size: 28px;
    font-weight: bold;
    color: #333;
    margin-bottom: 30px;
    text-align: center;
  }
  
  .auth-card {
    width: 100%;
    max-width: 500px; /* 增大卡片宽度 */
    background: white;
    border-radius: 12px; /* 增大圆角 */
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); /* 增强阴影 */
    overflow: hidden;
  }
  
  .auth-tabs {
    display: flex;
    border-bottom: 1px solid #eee;
  }
  
  .auth-tabs button {
    flex: 1;
    padding: 18px; /* 增大内边距 */
    border: none;
    background: none;
    cursor: pointer;
    font-size: 18px; /* 增大字体 */
    font-weight: 600;
    color: #666;
    transition: all 0.3s ease;
  }
  
  .auth-tabs button.active {
    color: #2bbbcb;
    border-bottom: 3px solid #2bbbcb; /* 加粗激活状态边框 */
  }
  
  .auth-form {
    padding: 30px; /* 增大内边距 */
  }
  
  .form-group {
    margin-bottom: 25px; /* 增大间距 */
  }
  
  .form-group label {
    display: block;
    margin-bottom: 10px; /* 增大间距 */
    font-size: 16px; /* 增大字体 */
    color: #555;
  }
  
  .form-group input {
    width: 100%;
    padding: 12px; /* 增大内边距 */
    border: 1px solid #ddd;
    border-radius: 6px; /* 增大圆角 */
    font-size: 16px; /* 增大字体 */
    transition: border-color 0.3s;
  }
  
  .form-group input:focus {
    border-color: #2bbbcb;
    outline: none;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2); /* 添加聚焦阴影 */
  }
  
  .error {
    color: #f56c6c;
    font-size: 14px; /* 增大字体 */
    margin-top: 8px; /* 增大间距 */
    display: block;
  }
  
  .submit-btn {
    width: 100%;
    padding: 14px; /* 增大内边距 */
    background-color: #2bbbcb;
    color: white;
    border: none;
    border-radius: 6px; /* 增大圆角 */
    font-size: 18px; /* 增大字体 */
    cursor: pointer;
    transition: all 0.3s; /* 改为所有属性过渡 */
  }
  
  .submit-btn:hover {
    background-color: #0fd5d9;
    transform: translateY(-2px); /* 添加悬停上浮效果 */
    box-shadow: 0 4px 12px rgba(102, 177, 255, 0.3); /* 添加悬停阴影 */
  }

  .forgot-password {
  margin-top: 15px;
  text-align: right;
}

.forgot-password a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password a:hover {
  color: #409eff;
}

.forgot-password-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  width: 400px;
  max-width: 90%;
}

.modal-content h2 {
  margin-top: 0;
  margin-bottom: 20px;
  text-align: center;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.cancel-btn {
  background-color: #f56c6c;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn:hover {
  background-color: #f78989;
}
</style>