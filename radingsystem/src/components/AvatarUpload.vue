<template>
    <div class="avatar-upload">
      <input 
        type="file"
        ref="fileInput"
        accept="image/*"
        @change="handleFileChange"
        style="display: none"
      >
      <div class="avatar-preview" @click="triggerFileInput">
        <img v-if="previewUrl" :src="previewUrl" class="avatar-image">
        <div v-else class="avatar-placeholder">
          <i class="el-icon-plus"></i>
          <span>点击上传头像</span>
        </div>
      </div>
      <div v-if="error" class="error-message">{{ error }}</div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      value: File
    },
    data() {
      return {
        previewUrl: '',
        error: ''
      }
    },
    methods: {
      triggerFileInput() {
        this.$refs.fileInput.click()
      },
      handleFileChange(event) {
        const file = event.target.files[0]
        if (!file) return
        
        // 验证文件类型
        if (!file.type.match('image.*')) {
          this.error = '请选择图片文件'
          return
        }
        
        // 验证文件大小 (限制2MB)
        if (file.size > 2 * 1024 * 1024) {
          this.error = '图片大小不能超过2MB'
          return
        }
        
        // 生成预览图
        const reader = new FileReader()
        reader.onload = (e) => {
          this.previewUrl = e.target.result
          this.error = ''
          this.$emit('input', file) // 将文件对象传递给父组件
        }
        reader.readAsDataURL(file)
      }
    }
  }
  </script>
  
  <style scoped>
  .avatar-upload {
    margin: 20px 0;
  }
  .avatar-preview {
    width: 150px;
    height: 150px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  }
  .avatar-preview:hover {
    border-color: #409eff;
  }
  .avatar-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .avatar-placeholder {
    text-align: center;
    color: #8c939d;
  }
  .error-message {
    color: #f56c6c;
    font-size: 12px;
    margin-top: 5px;
  }
  </style>