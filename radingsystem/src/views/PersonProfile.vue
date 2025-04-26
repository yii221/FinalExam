<template>
    <div class="profile-container">
        <!-- 添加返回按钮 -->
      <button @click="goToHome" class="btn-back">
        <i class="el-icon-back"></i> 返回主页
      </button>
      <div class="profile-header">
        <div class="avatar-section">
          <AvatarDisplay/>
          <input 
            type="file" 
            ref="avatarInput" 
            @change="handleAvatarChange" 
            accept="image/*" 
            style="display: none;"
          >
          <button @click="$refs.avatarInput.click()" class="btn-change-avatar">
            更换头像
          </button>
        </div>
        <div class="user-info">
          <span class="username">{{ userInfo.name}}</span>
          <span class="credit-score">信用积分: {{ userInfo.creditScore }}</span>
        </div>
      </div>
  
      <div class="profile-form">
        <div class="form-group">
          <label>姓名</label>
          <input 
            type="text" 
            v-model="userInfo.name" 
            :readonly="!editing"
          >
        </div>
        
        <div class="form-group">
          <label>邮箱</label>
          <input 
            type="email" 
            v-model="userInfo.email" 
            :readonly="!editing"
          >
        </div>
        
        <div class="form-group">
          <label>学号</label>
          <input 
            type="text" 
            v-model="userInfo.id" 
            readonly
          >
        </div>
        
        <div class="form-group">
          <label>电话</label>
          <input 
            type="tel" 
            v-model="userInfo.phonenumber" 
            :readonly="!editing"
          >
        </div>
  
        <div class="form-actions">
          <button 
            v-if="!editing" 
            @click="startEditing" 
            class="btn-edit"
          >
            编辑信息
          </button>
          <template v-else>
            <button @click="saveProfile" class="btn-save">保存</button>
            <button @click="cancelEditing" class="btn-cancel">取消</button>
          </template>
          <button @click="showPasswordDialog = true" class="btn-change-password">修改密码</button>
        </div>

            <!-- 密码修改对话框 -->
        <div v-if="showPasswordDialog" class="password-dialog-overlay">
        <div class="password-dialog">
            <h3>修改密码</h3>
        
            <div class="form-group">
                <label>原密码</label>
                <input 
                    type="password" 
                    v-model="passwordForm.oldPassword" 
                    placeholder="请输入原密码"
                >
            </div>
        
            <div class="form-group">
                <label>新密码</label>
                <input 
                    type="password" 
                    v-model="passwordForm.newPassword" 
                    placeholder="请输入新密码"
                >
            </div>
        
            <div class="form-group">
                <label>确认新密码</label>
                <input 
                    type="password" 
                    v-model="passwordForm.confirmPassword" 
                    placeholder="请再次输入新密码"
                >
            </div>
        
            <div v-if="passwordError" class="error-message">
                    {{ passwordError }}
            </div>
        
            <div class="dialog-actions">
                <button @click="handleChangePassword" class="btn-confirm">确认修改</button>
                <button @click="closePasswordDialog" class="btn-cancel">取消</button>
            </div>
        </div>
       </div>
      </div>
    </div>
  </template>
  
  <script>
  import AvatarDisplay from '../components/AvatarDisplay.vue'
  import { jwtDecode } from 'jwt-decode';

  export default {
    name: 'PersonProfile',
    components:{
        AvatarDisplay
    },
    data() {
      return {
        showPasswordDialog: false,
        passwordForm: {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
        },
        passwordError: '',
        userInfo: {
          name: '',
          email: '',
          id: '',
          phonenumber: '',
          avatar: require('@/assets/default-avatar.png'), // 默认头像
          creditScore: 100
        },
        editing: false,
        originalUser: null
      };
    },
    mounted() {
        this.fetchUserInfo();
    },
    methods: {
        goToHome() {
            this.$router.push('/TheHome'); 
        },
        fetchUserInfo() {
            const token = localStorage.getItem('authjwt')// 从 localStorage 获取 token
            //console.log('token已提取');

            if (token) {
                try {
                    const decoded = jwtDecode(token);
                    //console.log('Decoded Token:', decoded);

                    this.userInfo = {
                    name: decoded.name||'用户',
                    email: decoded.email,
                    id:decoded.id,
                    phonenumber:decoded.phonenumber,
                    creditScore:decoded.Creditscore
                    //avatar :'/uploads/${decoded.avatar}'||'https://via.placeholder.com/40' // 默认头像

                };
                } catch (e) {
                console.error('Token 解析失败:', e)
                }
            }
      },
      startEditing() {
        this.originalUser = {...this.userInfo};
        this.editing = true;
      },
      
      cancelEditing() {
        this.userInfo = {...this.originalUser};
        this.editing = false;
      },
      showChangePasswordDialog() {
        this.showPasswordDialog = true;
      },
  
      closePasswordDialog() {
        this.showPasswordDialog = false;
        // 可选：重置表单和错误信息
        this.passwordError = '';
        this.passwordForm = {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
        };
    },

      //修改密码
    async handleChangePassword() {
        // 验证
        if (!this.passwordForm.oldPassword) {
        this.passwordError = '请输入原密码';
        return;
        }
        if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
            this.passwordError = '两次输入密码不一致';
            return;
        }
    
        try {
            const resp = await this.$http.post('/TradingSystem/ChangePassword', {
            id: this.userInfo.id,
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
        });
      
        if (resp.data.status === "success") {
            alert(resp.data.message);
            this.showPasswordDialog = false;
            this.passwordForm = {
                    oldPassword: '',
                    newPassword: '',
                    confirmPassword: ''
                };
            this.passwordError = '';
        } else {
            this.passwordError = resp.data.message || '密码修改失败';
        }
        } catch (error) {
            this.passwordError = '请求失败，请稍后重试';
        }
    },

      //修改常规数据
      async saveProfile() {
        //传回数据到后端进行处理
        const resp = await this.$http.post('/TradingSystem/ChangePerson',{
                        id:this.userInfo.id,
                        email:this.userInfo.email,
                        phonenumber:this.userInfo.phonenumber,
                        name:this.userInfo.name,
                     }   ,{
                     headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        },
                    
                     });
        if(resp.data.status === "success"){
            alert(resp.data.message);
            //存储token到localStorage
            localStorage.setItem('authjwt', resp.data.jwt);
        }
        else{
            alert('服务器出现错误，请稍后重试！')
        }
        this.editing = false;
        
      },
      
      async handleAvatarChange(event) {
        const file = event.target.files[0];
        if (!file) return;
        
        // 验证文件类型
        if (!file.type.startsWith('image/')) {
          this.$message.error('请选择图片文件');
          return;
        }
        
        // 验证文件大小 (限制2MB)
        if (file.size > 2 * 1024 * 1024) {
          this.$message.error('图片大小不能超过2MB');
          return;
        }
        // 创建FormData对象
        const formData = new FormData();
        formData.append('avatar', file);
        formData.append('userId', this.userInfo.id);
  
        try {
        // 发送到后端
            const response = await this.$http.post('/TradingSystem/UploadAvatar', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
            });
    
            if (response.data.status === "success") {
            // 更新本地头像显示
                const reader = new FileReader();
                reader.onload = (e) => {
                    this.userInfo.avatar = e.target.result;
                    this.$message.success('头像更新成功');
        
                    // 更新token中的头像信息
                    localStorage.setItem('authjwt', response.data.jwt);
                };
                reader.readAsDataURL(file);
                } else {
                    this.$message.error(response.data.message || '头像上传失败');
                }
            } catch (error) {
                console.error('上传头像失败:', error);
                this.$message.error('上传头像失败，请稍后重试');
            }
        }
    }
    
  };
  </script>
  
  <style scoped>
  .profile-container {
    position: relative;
    max-width: 800px;
    margin: 20px auto;
    padding: 30px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  }
  
  .profile-header {
    display: flex;
    align-items: center;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 40px;
  }
  
  .avatar {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 15px;
    border: 4px solid #f8f8f8;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  }
  
  .btn-change-avatar {
    background-color: #f0f0f0;
    border: none;
    padding: 8px 15px;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s;
    font-size: 14px;
    color: #555;
  }
  
  .btn-change-avatar:hover {
    background-color: #e0e0e0;
    transform: translateY(-2px);
  }
  
  .user-info {
    margin: 0;
    color: #333;
    font-size: 28px;
  }
  
  .credit-score {
    color: #666;
    font-size: 16px;
    margin-top: 10px;
    font-weight: 500;
  }
  
  .profile-form {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 25px;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #555;
    font-size: 15px;
  }
  
  .form-group input {
    width: 100%;
    padding: 10px 15px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 15px;
    transition: border-color 0.3s;
  }
  
  .form-group input:focus {
    border-color: #409eff;
    outline: none;
  }
  
  .form-group input[readonly] {
    background-color: #f9f9f9;
    color: #666;
    cursor: not-allowed;
  }
  
  .form-actions {
    grid-column: span 2;
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    margin-top: 20px;
  }
  
  .btn-edit, .btn-save, .btn-cancel {
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s;
  }
  
  .btn-edit {
    background-color: #409eff;
    color: white;
  }
  
  .btn-edit:hover {
    background-color: #66b1ff;
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  }
  
  .btn-save {
    background-color: #67c23a;
    color: white;
  }
  
  .btn-save:hover {
    background-color: #85ce61;
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
  }
  
  .btn-cancel {
    background-color: #f5f5f5;
    color: #666;
  }
  
  .btn-cancel:hover {
    background-color: #e0e0e0;
    transform: translateY(-2px);
  }

  /* 密码对话框样式 */
.password-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.password-dialog {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
}

.btn-change-password {
  background: #f56c6c;
  color: white;
  margin-left: 10px;
}

.error-message {
  color: red;
  margin: 10px 0;
}

/* 添加返回按钮样式 */
.btn-back {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 8px 15px;
  background-color: #f5f5f5;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.btn-back:hover {
  background-color: #e0e0e0;
  transform: translateY(-2px);
}
  </style>