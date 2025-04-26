<template>
    <div class="avatar-container">
      <img 
        :src="avatarUrl" 
        alt="用户头像"
        class="avatar"
        @error="handleImageError"
      >
    </div>
  </template>
  
  <script>
  import { jwtDecode } from 'jwt-decode';
  export default {
    data() {
      return {
        avatarUrl: ''
      }
    },
    methods: {
      loadAvatar() {
        const token = localStorage.getItem('authjwt');
        if (!token) {
            console.warn("未找到JWT token");
            this.useFallbackAvatar();
            return;
        }

        try {
            const decoded = jwtDecode(token);
            console.log("解码后的JWT:", decoded); // 打印解码结果
    
            if (!decoded.avatar_path) {
            console.warn("JWT中缺少avatar_path字段");
            this.useFallbackAvatar();
            return;
            }

            // 构造URL并测试
            const avatarUrl = `http://localhost:8080/TradingSystem/uploads/${decoded.avatar_path}`;
            console.log("尝试加载头像URL:", avatarUrl);
    
            this.testImageLoad(avatarUrl);
        } catch (e) {
            console.error("JWT解析错误:", e);
            this.useFallbackAvatar();
        }
    },
      handleImageError() {
        // 头像加载失败时使用默认头像
        this.avatarUrl ='https://via.placeholder.com/40'
      },
      testImageLoad(url) {
        const img = new Image();
        img.onload = () => {
            console.log("头像加载成功，设置URL:", url);
            this.avatarUrl = url;
        };
        img.onerror = () => {
            console.error("头像加载失败，URL不可访问:", url);
            this.useFallbackAvatar();
        };
        img.src = url;
  },
  useFallbackAvatar() {
    // 使用本地默认头像
    this.avatarUrl = require('@/assets/default-avatar.png');
    console.log("已使用备用头像:", this.avatarUrl);
  }
  
    },
    mounted() {
      this.loadAvatar()
    }
  }
  </script>
  
  <style scoped>
  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    margin-right: 10px;
  }
  </style>