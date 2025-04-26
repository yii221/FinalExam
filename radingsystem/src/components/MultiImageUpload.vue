<template>
    <div class="multi-image-upload">
      <div class="upload-area">
        <!-- 隐藏的文件输入 -->
        <input
          type="file"
          ref="fileInput"
          accept="image/*"
          multiple
          @change="handleFileChange"
          style="display: none"
        >
  
        <!-- 上传按钮 -->
        <div class="upload-btn" @click="triggerFileInput">
          <i class="el-icon-plus"></i>
          <div class="upload-text">点击上传商品图片</div>
          <div class="upload-hint">支持多选，最多{{maxCount}}张,默认第一张图片为商品主图</div>
        </div>
  
        <!-- 图片预览列表 -->
        <div class="preview-list">
          <div v-for="(image, index) in previewUrls" :key="index" class="preview-item">
            <img :src="image.url" class="preview-image">
            <div class="preview-actions">
              <i class="el-icon-view" @click="handlePreview(image)"></i>
              <i class="el-icon-delete" @click="handleRemove(index)"></i>
              <i
                class="el-icon-top"
                v-if="index > 0"
                @click="handleMoveUp(index)"
              ></i>
              <i
                class="el-icon-bottom"
                v-if="index < previewUrls.length - 1"
                @click="handleMoveDown(index)"
              ></i>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 错误提示 -->
      <div v-if="error" class="error-message">{{ error }}</div>
  
      <!-- 图片预览对话框 -->
      <el-dialog :visible.sync="dialogVisible" title="图片预览">
        <img :src="dialogImageUrl" style="width: 100%">
      </el-dialog>
    </div>
  </template>
  
  <script>
  export default {
    name: 'MultiImageUpload',
    props: {
      value: {
        type: Array,
        default: () => []
      },
      maxCount: {
        type: Number,
        default: 9
      },
      maxSize: {
        type: Number,
        default: 2
      }
    },
    data() {
      return {
        previewUrls: [], // 预览图URL数组
        error: '', // 错误信息
        dialogVisible: false, // 预览对话框显示状态
        dialogImageUrl: '', // 预览对话框图片URL
      }
    },
    watch: {
      // 监听value变化，初始化预览
      value: {
        immediate: true,
        handler(files) {
          if (!files || files.length === 0) {
            this.previewUrls = [];
            return;
          }
          this.initPreviews(files)
        }
      }
    },
    methods: {
      // 触发文件选择
      triggerFileInput() {
        this.$refs.fileInput.value = ''; // 清除上次选择，允许重复选择相同文件
        this.$refs.fileInput.click();
      },
  
      // 处理文件选择变化
      handleFileChange(event) {
        const files = Array.from(event.target.files);
        if (!files.length) return;
  
        // 检查总数量是否超过限制
        if (this.previewUrls.length + files.length > this.maxCount) {
          this.error = `最多只能上传${this.maxCount}张图片`;
          return;
        }
  
        // 处理每个文件
        files.forEach(file => {
          this.processFile(file);
        });
      },
  
      // 处理单个文件
      processFile(file) {
        // 验证文件类型
        if (!file.type.match('image.*')) {
          this.error = '请选择图片文件';
          return;
        }
  
        // 验证文件大小
        if (file.size > this.maxSize * 1024 * 1024) {
          this.error = `图片大小不能超过${this.maxSize}MB`;
          return;
        }
  
        // 生成预览图
        const reader = new FileReader();
        reader.onload = (e) => {
          this.previewUrls.push({
            url: e.target.result,
            file: file,
          });
          this.error = '';
          this.emitInput();
        };
        reader.readAsDataURL(file);
      },
  
      // 初始化预览
      initPreviews(files) {
            this.previewUrls = [];
            files.forEach(file => {
            if (file instanceof File) {
                this.processFile(file);
            } else if (file.url) {
            // 保留已有的 File 对象（如果有）
            this.previewUrls.push({
                url: file.url,
                file: file.file || null  // 保留原有 file 对象
        });
    }
  });
},
  
      // 删除图片
      
    handleRemove(index) {
        // 创建新数组避免直接修改原数组
        const newUrls = [...this.previewUrls];
        // 删除指定索引的图片
         newUrls.splice(index, 1);
        // 更新预览数组
        this.previewUrls = newUrls;
        // 通知父组件数据变化
        this.emitInput();
    },
  
      // 预览图片
      handlePreview(image) {
        this.dialogImageUrl = image.url;
        this.dialogVisible = true;
      },
      //上移图片
      handleMoveUp(index) {
       if (index > 0) {
        const newUrls = [...this.previewUrls];
        [newUrls[index], newUrls[index - 1]] = [newUrls[index - 1], newUrls[index]];
        this.previewUrls = newUrls;
        this.emitInput();
        }
    },

    // 下移图片
      handleMoveDown(index) {
        if (index < this.previewUrls.length - 1) {
            const newUrls = [...this.previewUrls];
            [newUrls[index], newUrls[index + 1]] = [newUrls[index + 1], newUrls[index]];
                this.previewUrls = newUrls;
                this.emitInput();
            }
        },
      // 向父组件发送更新
      emitInput() {
        // 只发送File对象数组
        this.previewUrls.forEach((item) => {
            console.log("URL:", item.url);
            console.log("File 对象:", item.file);
            console.log('-----------')
        });
        this.$emit('input', this.previewUrls.map(item => ({
          url: item.url,
          file: item.file,
        })))

      } 
    }
  };
  </script>
  
  
  <style scoped>
  .multi-image-upload {
    margin: 20px 0;
  }
  
  .upload-area {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .upload-btn {
    width: 150px;
    height: 150px;
    border: 1px dashed #dcdfe6;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #8c939d;
    transition: border-color 0.3s;
  }
  
  .upload-btn:hover {
    border-color: #409eff;
  }
  
  .upload-text {
    margin-top: 8px;
    font-size: 14px;
  }
  
  .upload-hint {
    margin-top: 4px;
    font-size: 12px;
    color: #999;
  }
  
  .preview-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .preview-item {
    position: relative;
    width: 150px;
    height: 150px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
  }
  
  .preview-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .preview-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 30px;
  background: rgba(0, 0, 0, 0.7); /* 加深背景色 */
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}
  
  .preview-item:hover .preview-actions {
    opacity: 1;
  }
  
  .preview-actions i {
  color: #fff;
  margin: 0 8px;
  cursor: pointer;
  font-size: 16px;
  padding: 5px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
}
.preview-actions i:hover {
  background: rgba(255, 255, 255, 0.4);
}
  
  .error-message {
    color: #f56c6c;
    font-size: 12px;
    margin-top: 5px;
  }
  </style>