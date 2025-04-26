<template>
    <div class="multi-images-upload">
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
          <div class="upload-hint">支持多选，最多{{ maxCount }}张, 默认第一张图片为商品主图</div>
        </div>
  
        <!-- 图片预览列表 -->
        <div class="preview-list">
          <!-- 已上传的图片 -->
          <div v-for="(image, index) in allImages" :key="'image-'+index" class="preview-item">
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
                v-if="index < allImages.length - 1"
                @click="handleMoveDown(index)"
              ></i>
            </div>
            <div class="image-status">{{ image.isExisting ? '已上传' : '待上传' }}</div>
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
      },
      proid: {
        type:[String,Number] ,
        default: ''
      }
    },
    data() {
      return {
        previewUrls: [],    // 新上传的图片预览
        existingImages: [], // 已存在的图片
        error: '',          // 错误信息
        dialogVisible: false, // 预览对话框显示状态
        dialogImageUrl: '',   // 预览对话框图片URL
        loading: false       // 加载状态
      };
    },
    watch: {
      // 监听value变化，初始化预览
      value: {
        immediate: false,
        handler(newVal) {
            if (newVal) {
        this.fetchExistingImages();
      } else {
        this.existingImages = []; // 清空已有图片
      }
        }
      },
      // 监听proid变化，加载已有图片
      proid: {
        immediate: true,
        handler(newVal) {
          if (newVal) {
            this.fetchExistingImages();
          }
        }
      }
    },
    computed: {
      // 合并所有的图片（已存在和新上传的）
      allImages() {
        return [
          ...this.existingImages.map(img => ({ ...img, isExisting: true })),
          ...this.previewUrls.map(img => ({ ...img, isExisting: false }))
        ];
      }
    },
    created() {
    // 组件创建时立即获取已有图片
        if (this.proid) {
            this.fetchExistingImages();
        }
    },
    methods: {
    // 下载图片并生成文件
    async fetchImageAsFile(url) {
        try {
            console.log(url);
            // 使用 axios 获取图片
            const response = await this.$http.get(url, {
                responseType: 'blob',  // 指定响应类型为 'blob'
                withCredentials: true,  // 如果需要跨域请求时带上 cookie
                headers: {
                    'Accept': 'image/*',  // 告诉服务器返回图片类型的数据
                }
            });

            if (!response) {
                throw new Error('Failed to fetch image');
            }

            // 获取图片的 Blob 数据
            const blob = response.data;

            // 创建一个新的 File 对象
            const file = new File([blob], 'image.jpg', { type: blob.type });

            return file;
        } catch (error) {
            console.error('Error fetching image as file:', error);
            return null;
        }
    },
      // 获取已上传的图片
    async fetchExistingImages() {
        console.log("start fetch!");
        this.loading = true;
        this.error = '';
        
        try {
          const response = await this.$http.post(`/TradingSystem/GetProImageServlet`, 
            { proid: this.proid },
            {headers: {
              'Content-Type': 'application/json;charset=UTF-8'
            }
          });
          
          console.log('获取到的响应数据:', response.data);
          if (response.data && Array.isArray(response.data)) {
             // 遍历已上传图片的 URL，并下载每个文件
            for (let i = 0; i < response.data.length; i++) {
                const imageUrl = response.data[i];
                const file = await this.fetchImageAsFile(`http://localhost:8080/TradingSystem/${imageUrl}`);

            if (file) {
            // 将下载的 file 赋值给图片对象
                this.existingImages.push({
                url: imageUrl.startsWith('http') ? imageUrl : `http://localhost:8080/TradingSystem/${imageUrl}`,
                file: file,
                isExisting: true
          });
        }
        console.log(this.existingImages.url);
      }
          }
        } catch (error) {
          console.error('获取已有图片失败:', error);
          this.error = '获取已有图片失败，请稍后重试';
        } finally {
          this.loading = false;
        }
      },
  
      // 触发文件选择
      triggerFileInput() {
        // 检查是否已达到最大数量
        if (this.existingImages.length + this.previewUrls.length >= this.maxCount) {
          this.error = `最多只能上传${this.maxCount}张图片`;
          return;
        }
  
        this.$refs.fileInput.value = ''; // 清除上次选择，允许重复选择相同文件
        this.$refs.fileInput.click();
      },
  
      // 处理文件选择变化
      handleFileChange(event) {
        const files = Array.from(event.target.files);
        if (!files.length) return;
  
        // 检查总数量是否超过限制
        if (this.existingImages.length + this.previewUrls.length + files.length > this.maxCount) {
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
            isExisting: false
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
              file: file.file || null,
              isExisting: false
            });
          }
        });
      },
  
      // 删除图片
      handleRemove(index) {
        this.previewUrls.splice(index, 1);
        this.emitInput();
      },
  
      // 删除已存在的图片
      async handleRemoveExisting(index) {
        this.$confirm('确定要删除这张图片吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(this.existingImages.splice(index, 1)
            /*async () => {
          try {
            const imageUrl = this.existingImages[index].url;
            const response = await this.$http.post('/TradingSystem/DeleteProImageServlet', {
              proid: this.proid,
              imageUrl: imageUrl
            },{
              headers: {
                'Content-Type': 'application/json;charset=UTF-8'
              }
            });
  
            if (response.data.status === 'success') {
              this.existingImages.splice(index, 1);
              this.$message.success('删除成功');
            } else {
              this.$message.error(response.data.message);
            }
          } catch (error) {
            console.error('删除图片失败:', error);
            this.$message.error('删除图片失败');
          }
        }*/).catch(() => {});
      },
  
      // 预览图片
      handlePreview(image) {
        this.dialogImageUrl = image.url;
        this.dialogVisible = true;
      },
  
      // 上移图片
      handleMoveUp(index) {
        if (index > 0) {
          const allImages = this.allImages;
          [allImages[index], allImages[index - 1]] = [allImages[index - 1], allImages[index]];
          this.emitInput();
        }
      },
  
      // 下移图片
      handleMoveDown(index) {
        if (index < this.allImages.length - 1) {
          const allImages = this.allImages;
          [allImages[index], allImages[index + 1]] = [allImages[index + 1], allImages[index]];
          this.emitInput();
        }
      },
  
      // 向父组件发送更新
      emitInput() {
        this.$emit('input', this.allImages);
      },
  
      // 获取所有图片（包括已存在和新上传的）
      getAllImages() {
        return {
          existing: this.existingImages,
          newUploads: this.previewUrls
        };
      }
    }
  };
  </script>
  
  <style scoped>
  .multi-images-upload {
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
    background: rgba(0, 0, 0, 0.7);
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
  
  .image-status {
    position: absolute;
    top: 5px;
    right: 5px;
    background: rgba(0, 0, 0, 0.6);
    color: white;
    padding: 2px 5px;
    border-radius: 3px;
    font-size: 12px;
  }
  </style>
  