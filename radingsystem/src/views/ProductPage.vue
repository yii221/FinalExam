<template>
  <div class="my-goods-container">
     <!-- 添加返回按钮 -->
     <button @click="goToHome" class="btn-back">
        <i class="el-icon-back"></i> 返回主页
      </button>
    <!-- 顶部操作栏 -->
    <div class="action-bar">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索我的商品..."
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">
          <i class="el-icon-search"></i>
        </button>
      </div>
      <button class="add-btn" @click="showAddDialog">
        <i class="el-icon-plus"></i> 发布新商品
      </button>
    </div>

    <!-- 商品列表 -->
    <div class="goods-list">
      <div v-if="filteredGoods.length > 0" class="goods-grid">
        <div 
          v-for="item in filteredGoods" 
          :key="item.proid" 
          class="goods-card"
        >
          <div class="goods-img">
            <img :src="item.primaryImage" :alt="item.proname" />
            <div class="goods-status" :class="'status-' + item.status">
                {{ statusText(item.status) }}
            </div>
          </div>
          <div class="goods-info">
            <div class="goods-category">
              {{ getCategoryLabel(item.category) }}
            </div>
            <h3 class="goods-title">{{ item.proname }}</h3>  
            <p class="goods-desc">{{ item.description }}</p>
            <div class="goods-footer">
              <span class="goods-price">¥{{ item.price }}</span>
              <div class="goods-actions">
                <button class="edit-btn" @click="editGoods(item)">
                  <i class="el-icon-edit"></i>
                </button>
                <button class="delete-btn" @click="deleteGoods(item.proid)">
                  <i class="el-icon-delete"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <img src="https://img.alicdn.com/tfs/TB1YHEpwUT1gK0jSZFhXXaAtVXa-28-27.png" alt="空空如也" />
        <p>这里空空如也~</p>
        <button class="add-btn" @click="showAddDialog">
          <i class="el-icon-plus"></i> 发布我的第一个商品
        </button>
      </div>
    </div>
  <!--商品修改对话框-->
  <el-dialog 
    title="编辑商品" 
    :visible.sync="visible" 
    width="50%"
    @close="handleClose"
  >
    <el-form :model="newGoods" label-width="100px">
      <!-- 商品分类 -->
      <el-form-item label="商品分类">
        <el-select v-model="newGoods.category" placeholder="请选择分类">
          <el-option
            v-for="item in categories"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <!-- 商品状态 -->
      <el-form-item label="商品状态">
        <el-select v-model="newGoods.status">
          <el-option
            v-for="item in goodsstatus"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <!-- 商品名称 -->
      <el-form-item label="商品名称" required>
        <el-input v-model="newGoods.proname" />
      </el-form-item>

      <!-- 商品描述 -->
      <el-form-item label="商品描述">
        <el-input 
          type="textarea" 
          v-model="newGoods.description" 
          :rows="3" 
        />
      </el-form-item>

      <!-- 商品价格 -->
      <el-form-item label="商品价格" required>
        <el-input-number 
          v-model="newGoods.price" 
          :min="0" 
          :precision="2" 
        />
      </el-form-item>

      <!-- 商品库存 -->
      <el-form-item label="商品库存">
        <el-input-number v-model="newGoods.stock" :min="0" />
      </el-form-item>

      <!-- 商品图片 -->
      <el-form-item label="商品图片">
        <photo-gallery
          v-model="GoodsImage.Images"
          :max-count="9"
          :max-size="2"
          :proid=newGoods.proid
          @input="handleImageUpdate"
        ></photo-gallery>
      </el-form-item>
    </el-form>

    <span slot="footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">保存修改</el-button>
    </span>
  </el-dialog>

    <!-- 发布商品对话框 -->
    <el-dialog title="发布新商品" :visible.sync="dialogVisible" width="50%">
      <div class="add-goods-form">
        <el-form :model="newGoods" label-width="80px">
          <el-form-item label="商品分类">
            <el-select v-model="newGoods.category" placeholder="请选择商品分类">
              <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="商品状态">
            <el-select v-model="newGoods.status" placeholder="请选择商品状态">
              <el-option
                v-for="item in goodsstatus"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="商品名称">
            <el-input v-model="newGoods.proname"></el-input>
          </el-form-item>
          <el-form-item label="商品描述">
            <el-input 
              type="textarea" 
              v-model="newGoods.description"
              :rows="3"
            ></el-input>
          </el-form-item>
          <el-form-item label="商品价格">
            <el-input v-model="newGoods.price" type="number">
              <template slot="append">元</template>
            </el-input>
          </el-form-item>
          <el-form-item label="商品库存">
            <el-input v-model="newGoods.stock" type="number"></el-input>
          </el-form-item>
          <el-form-item label="商品图片">
            <multi-image-upload 
              v-model="GoodsImage.Images"
              :max-count="9"
              :max-size="2"
              @input="handleImageUpdate"
            ></multi-image-upload>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addGoods">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import MultiImageUpload from '../components/MultiImageUpload.vue';
import PhotoGallery from '../components/PhotoGallery.vue';

export default {
  components: { MultiImageUpload,PhotoGallery },
  name: 'MyGoods',
  data() {
    return {
      searchQuery: '',
      GoodsImage: {
        Images: [],
        proid: ''
      },
      categories: [
        { value: 'daily', label: '日用品' },
        { value: 'food', label: '食品' },
        { value: 'clothing', label: '服装' },
        { value: 'home', label: '家居用品' },
        { value: 'digital', label: '数码产品' },
        { value: 'beauty', label: '美妆护肤' },
        { value: 'other', label: '其他' }
      ],
      goodsstatus:[
        { value:0, label:'在售'},
        { value:1, label:'无库存'},
        { value:-1, label:'下架'}
        
      ],
      goodsList: [],
      dialogVisible: false,
      visible:false,
      newGoods: {
        proname: '',
        category: '',
        description: '',
        price: 0,
        primaryImages: '',
        status:'',
        stock:'',
        proid:''
      },
      fileList: []
    };
  },
  computed: {
    statusText() {
    return (status) => {
      const statusMap = {
        1: '无库存',
        0: '在售',
        '-1': '已下架'
      };
      return statusMap[status];
    };
  },
    filteredGoods() {
      if (!this.searchQuery) {
        return this.goodsList;
      }
      const query = this.searchQuery.toLowerCase();
      return this.goodsList.filter(item => 
        item.name.toLowerCase().includes(query) || 
        item.description.toLowerCase().includes(query)
      );
    }
  },
  methods: {
    goToHome() {
            this.$router.push('/TheHome'); 
        },
    handleImageUpdate(files) {
      this.GoodsImage.Images = files.map(file => ({
        file: file.file,  // 确保获取的是File对象
        url: file.url || URL.createObjectURL(file.file) // 保留已有URL或创建新URL
      }));
    },
    getCategoryLabel(value) {
      const category = this.categories.find(item => item.value === value);
      return category ? category.label : '未知分类';
    },
    handleSearch() {
      console.log('搜索商品:', this.searchQuery);
    },
    showAddDialog() {
      this.dialogVisible = true;
      this.newGoods = {
        proname: '',
        category: '',
        description: '',
        price: 0,
        images: []
      };
      this.GoodsImage.Images = [];
    },
    async addGoods() {
      // 表单验证
      if (!this.newGoods.proname || !this.newGoods.price) {
        this.$message.error('请填写商品名称和价格');
        return;
      }

      const token = localStorage.getItem('authjwt');
      if (!token) {
        this.$message.error('请先登录');
        this.$router.push('/login');
        return;
      }

      // 添加商品基本信息
      const newItem = await this.$http.post('/TradingSystem/AddProductServlet', {
        category: this.newGoods.category,
        proname: this.newGoods.proname,
        description: this.newGoods.description,
        price: parseFloat(this.newGoods.price),
        status: this.newGoods.status,
        stock:this.newGoods.stock
      }, {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8',
          'Authorization': `Bearer ${token}`
        },
        withCredentials: true
      });

      if (newItem.data.ProcessStatus === "success") {
        this.GoodsImage.proid = newItem.data.proid;
        this.newGoods.proid =  newItem.data.proid
        try {
          const formData = new FormData();
          
          // 添加商品ID
          formData.append('proid', this.GoodsImage.proid);

          // 添加所有图片文件
          this.GoodsImage.Images.forEach(image => {
            formData.append('images', image.file);
          });

          // 发送请求上传图片
          const response = await this.$http.post('/TradingSystem/ProImageUploadServlet', formData, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });

          if (response.data.status === 'success') {
            this.GoodsImage.Images = [];
            alert(newItem.data.message);
          } else {
            alert('图片上传失败');
          }
        } catch (error) {
          console.error('上传失败:', error);
          this.uploadStatus = {
            type: 'error',
            message: error.response?.data?.message || '图片上传失败'
          };
        }
      } else {
        alert(newItem.data.message);
      }

      // 添加到商品列表并关闭对话框
      this.goodsList.unshift(newItem.data);
      this.dialogVisible = false;
    },
    handleClose() {
      this.form = this.$options.data().form
      this.fileList = []
    },

    //修改商品信息和图片
    async submitForm() {
      try{
        const resp = await this.$http.post('/TradingSystem/EditProServlet', 
          {
            category: this.newGoods.category,
            proname: this.newGoods.proname,
            description: this.newGoods.description,
            price: this.newGoods.price,
            status: this.newGoods.status,
            stock: this.newGoods.stock,
            proid:  this.newGoods.proid
          },
          {
            headers: {
              'Content-Type': 'application/json'
            }
          }
        );
        
        console.log(resp.data.status);
        if(resp.data.status === "success")
        {
          //this.$message.success(resp.data.message);
          try {
          const formData = new FormData();
          
          // 添加商品ID
          formData.append('proid', this.newGoods.proid);

          // 添加所有图片文件
          this.GoodsImage.Images.forEach(image => {
            formData.append('images', image.file);
          });

          // 发送请求上传图片
          const response = await this.$http.post('/TradingSystem/EditProImageServlet', formData);

          if (response.data.status === 'success') {
            this.GoodsImage.Images = [];
            this.$message.success(resp.data.message);
          } else {
            alert('图片上传失败');
          }
        } catch (error) {
          console.error('上传失败:', error);
          this.uploadStatus = {
            type: 'error',
            message: error.response?.data?.message || '商品修改失败'
          };
        }
        }


      }catch (error) {
        this.$message.error(`更新失败: ${error.message}`)
      }
    },

    //加载商品信息
    async loadGoods() {
      this.loading = true;
      this.error = null;
      const token = localStorage.getItem('authjwt');
      if (!token) {
        this.$message.error('请先登录');
        this.$router.push('/login');
        return;
      }
      
      try {
        // 发送GET请求获取商品数据
        const response = await this.$http.get('/TradingSystem/LoadProducts',{
          headers: {
              'Authorization': `Bearer ${token}`
            }
        });
        
        // 检查响应状态
        if (response.status !== 200) {
          throw new Error(`请求失败: ${response.status}`);
        }
        
        // 解析JSON数据并存储到goodsList
        this.goodsList = response.data;
        
        //修改primaryImage地址以便于找到
        this.goodsList.forEach(item => {
          // 确保图片路径完整
          if (item.primaryImage && !item.primaryImage.startsWith('http')) {
            item.primaryImage = `http://localhost:8080/TradingSystem/${item.primaryImage}`;
          }
        });
        
      } catch (error) {
        console.error('加载商品失败:', error);
        this.error = '加载商品失败: ' + error.message;
      } finally {
        this.loading = false;
      }
    },
    editGoods(item) {
      this.newGoods = { ...item };
      /*if (item.images) {
        this.GoodsImage.Images = item.images.map(img => ({
          url: img.url,
          file: null
        }));
      }*/
      this.visible = true;
    },
    deleteGoods(proid) {
      //console.log(proid);
      this.$confirm('确定要删除这个商品吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
          const resp = await this.$http.post('/TradingSystem/DeleteProServlet',  {proid}   ,{
                     headers: {
                            'Content-Type': 'application/json'
                        },
                    
                     });
          if(resp.data.status==="success"){
            this.$message.success('删除成功!');
          } else {
          this.$message.error(resp.data.message);
        }
        this.goodsList = this.goodsList.filter(item => item.proid !== proid);
      }
        
    ).catch(() => {});
  }
  },
  mounted() {
    // 初始化商品列表
    this.loadGoods();
  }
};
</script>
  
  <style scoped>
  .my-goods-container {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .search-box {
    display: flex;
    width: 300px;
  }
  
  .search-box input {
    flex: 1;
    padding: 10px 15px;
    border: 1px solid #dcdfe6;
    border-radius: 4px 0 0 4px;
    outline: none;
  }
  
  .search-box button {
    padding: 10px 15px;
    background-color: #f5f7fa;
    border: 1px solid #dcdfe6;
    border-left: none;
    border-radius: 0 4px 4px 0;
    cursor: pointer;
  }
  
  .add-btn {
    padding: 10px 15px;
    background-color: #409eff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .add-btn:hover {
    background-color: #66b1ff;
  }
  
  .goods-list {
    min-height: 500px;
  }
  
  .goods-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }
  
  .goods-card {
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
  }
  
  .goods-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
  
  .goods-img {
    position: relative;
    height: 200px;
    overflow: hidden;
  }
  
  .goods-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s;
  }
  
  .goods-card:hover .goods-img img {
    transform: scale(1.05);
  }
  
  .goods-status {
    position: absolute;
    top: 10px;
    right: 10px;
    padding: 3px 8px;
    border-radius: 4px;
    font-size: 12px;
    color: white;
  }
  
  .goods-status.onSale {
    background-color: #67c23a;
  }
  
  .goods-status.offSale {
    background-color: #909399;
  }
  
  .goods-info {
    padding: 15px;
  }
  
  .goods-title {
    margin: 0 0 10px;
    font-size: 16px;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .goods-desc {
    margin: 0 0 15px;
    font-size: 14px;
    color: #999;
    height: 40px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  
  .goods-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .goods-price {
    font-size: 18px;
    color: #f56c6c;
    font-weight: bold;
  }
  
  .goods-actions button {
    padding: 5px;
    margin-left: 5px;
    background: none;
    border: none;
    cursor: pointer;
    color: #606266;
  }
  
  .goods-actions button:hover {
    color: #409eff;
  }
  
  .empty-state {
    text-align: center;
    padding: 80px 0;
    background: white;
    border-radius: 8px;
  }
  
  .empty-state img {
    width: 80px;
    height: 80px;
    margin-bottom: 20px;
  }
  
  .empty-state p {
    color: #999;
    margin-bottom: 20px;
  }

  .goods-category {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
  }

  
  </style>
