<template>
    <div id="app">
      <!-- 顶部导航栏 -->
      <header class="header">
        <div class="logo-container">
          <h1>校园二手交易平台</h1>
        </div>
        
        <div class="user-info">
            <AvatarDisplay/>
            <span class="username">{{ userInfo.name}}</span>
            <button class="logout-btn" @click="logout">退出登录</button>
        </div>
      </header>
  
      <div class="main-container">
        <!-- 侧边栏 -->
        <TheSidebar />
        
        <!-- 主内容区 -->
        <div class="content">
          <SearchBar @search="handleSearch" />
          
          <div class="filter-sort-area">
      <!-- 分类筛选 -->
      <div class="filter-item">
        <label>分类：</label>
        <select v-model="filter.category" @change="applyFilters">
          <option value="">全部</option>
          <option v-for="cat in categories" :value="cat.value" :key="cat.value">
            {{ cat.label }}
          </option>
        </select>
      </div>
      
      <!-- 状态筛选 -->
      <div class="filter-item">
        <label>状态：</label>
        <select v-model="filter.status" @change="applyFilters">
          <option value="">全部</option>
          <option v-for="stat in goodsstatus" :value="stat.value" :key="stat.value">
            {{ stat.label }}
          </option>
        </select>
      </div>
      
      <!-- 价格范围 -->
      <div class="filter-item">
        <label>价格：</label>
        <input type="number" v-model="filter.minPrice" placeholder="最低价" @change="applyFilters">
        <span>-</span>
        <input type="number" v-model="filter.maxPrice" placeholder="最高价" @change="applyFilters">
      </div>
      
      <!-- 排序方式 -->
      <div class="sort-item">
        <label>排序：</label>
        <select v-model="sort.field" @change="applySorting">
          <option value="">默认</option>
          <option value="price">价格</option>
          <option value="proscore">评分</option>
        </select>
        <select v-model="sort.order" @change="applySorting">
          <option value="asc">升序</option>
          <option value="desc">降序</option>
        </select>
      </div>
    </div>

          <div class="product-list">
            <h2 v-if="searchQuery">搜索结果: {{ searchQuery }}</h2>
            <h2 v-else>热门商品</h2>
            
            <div class="products">
              <ProductCard 
                v-for="product in filteredProducts" 
                :key="product.proid"
                :product="product"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import TheSidebar from '../components/TheSidebar.vue'
  import SearchBar from '../components/SearchBar.vue'
  import ProductCard from '../components/ProductCard.vue'
  import AvatarDisplay from '../components/AvatarDisplay.vue'
  import { jwtDecode } from 'jwt-decode';

  export default {
    name: 'TheHome',
    components: {
      TheSidebar,
      SearchBar,
      ProductCard,
      AvatarDisplay
    },
    data() {
      return {
        filter: {
        category: '',
        status: '',
        minPrice: null,
        maxPrice: null
      },
      sort: {
        field: '',
        order: 'asc'
      },
        searchQuery: '',
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
        userInfo: { // 新增用户信息字段
            name: '未登录',
            avatar: 'https://via.placeholder.com/40'
        },
        goodsList: [],
        product: {
        proname: '',
        category: '',
        description: '',
        price: 0,
        primaryImages: '',
        status:'',
        stock:'',
        proid:''
      },
      }
    },
    computed: {
        filteredProducts() {
          let goods = this.goodsList || [];
  
          // 搜索过滤
          if (this.searchQuery) {
            const query = this.searchQuery.toLowerCase();
            goods = goods.filter(product => 
            product.proname.toLowerCase().includes(query) ||
            (product.description && product.description.toLowerCase().includes(query))
          );
          }
  
          // 排序
          if (this.sort.field) {
          goods = [...goods].sort((a, b) => {
              const field = this.sort.field;
              const order = this.sort.order === 'asc' ? 1 : -1;
      
              if (a[field] < b[field]) return -1 * order;
              if (a[field] > b[field]) return 1 * order;
              return 0;
          });
      }
      return goods;
    }
  },
    mounted() {
        this.fetchUserInfo();
       this.loadGoods();
    },
    methods: {
      applyFilters() {
      const params = {
        ...this.filter,
        ...this.sort
      };
      
      // 移除空值参数
      Object.keys(params).forEach(key => {
        if (params[key] === '' || params[key] === null) {
          delete params[key];
        }
        });
        this.loadGoodsWithFilters(params);
      },
    
      // 新增排序方法
      applySorting() {
        this.applyFilters(); // 直接调用筛选方法，因为参数结构相同
      },
    
      // 修改后的加载商品方法
      async loadGoodsWithFilters(params = {}) {
        this.loading = true;
        this.error = null;
        const token = localStorage.getItem('authjwt');
      
        if (!token) {
          this.$message.error('请先登录');
          this.$router.push('/login');
          return;
        }
      
      try {
        const response = await this.$http.get('/TradingSystem/ProductCheckServlet', {
          params, // 传递筛选和排序参数
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        if (response.status !== 200) {
          throw new Error(`请求失败: ${response.status}`);
        }
        
        this.goodsList = response.data;
        
        this.goodsList.forEach(item => {
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
  
      handleSearch(query) {
        this.searchQuery = query
        if (query) {
            this.searchProducts(query)  // 有搜索词时调用搜索方法
        } else {
        this.loadGoods()  // 搜索词为空时加载全部商品
      }
    },
      logout() {
        // 清除本地存储的token
        localStorage.removeItem('authjwt');
        this.$router.push('/login');
        this.$message.success('已成功退出登录');
      },
     
      async searchProducts(keyword) {
        this.loading = true
        this.error = null
        const token = localStorage.getItem('authjwt')
        if (!token) {
          this.$message.error('请先登录')
          this.$router.push('/login')
          return
      }
  
        try {
          const response = await this.$http.post('/TradingSystem/ProductSearchServlet', {
              keyword: keyword  // 将搜索关键词作为参数传递
            },{
            headers: {
              'Authorization': `Bearer ${token}`  // 携带认证token
          }
        })
    
        if (response.status !== 200) {
          throw new Error(`搜索失败: ${response.status}`)
        }
    
        this.goodsList = response.data  // 更新商品列表
    
       // 处理图片路径
        this.goodsList.forEach(item => {
        if (item.primaryImage && !item.primaryImage.startsWith('http')) {
          item.primaryImage = `http://localhost:8080/TradingSystem/${item.primaryImage}`
        }
      })
    
      } catch (error) {
        console.error('搜索商品失败:', error)
        this.error = '搜索商品失败: ' + error.message
      } finally {
        this.loading = false
      }
    },
      fetchUserInfo() {
      const token = localStorage.getItem('authjwt') // 从 localStorage 获取 token
      if (token) {
        // 解析 JWT 中的 payload（假设 payload 包含用户信息）
        try {
            const decoded = jwtDecode(token);
            this.userInfo = {
            name: decoded.name||'用户',
            //avatar :'/uploads/${decoded.avatar}'||'https://via.placeholder.com/40' // 默认头像

        };
        } catch (e) {
          console.error('Token 解析失败:', e)
        }
      }
    },
    async loadGoods() {
      /*this.loading = true;
      this.error = null;
      const token = localStorage.getItem('authjwt');
      if (!token) {
        this.$message.error('请先登录');
        this.$router.push('/login');
        return;
      }
      
      try {
        // 发送GET请求获取商品数据
        const response = await this.$http.get('/TradingSystem/ProductDisplayServlet',{
          headers: {
            'Content-Type': 'application/json',
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
      }*/
      this.loadGoodsWithFilters(); // 调用带参数的方法但不传参
    },
  }}
  
  </script>
  
  <style>
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  
  body {
    font-family: 'Arial', sans-serif;
    background-color: #f5f5f5;
  }
  
  #app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 30px;
    background-color: #ff0036;
    color: white;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .logo-container {
    display: flex;
    align-items: center;
  }
  
  .logo {
    width: 40px;
    height: 40px;
    margin-right: 15px;
  }
  
  .user-info {
    display: flex;
    align-items: center;
  }
  
  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 10px;
  }
  
  .username {
    font-size: 16px;
  }
  
  .main-container {
    display: flex;
    flex: 1;
  }
  
  .content {
    flex: 1;
    padding: 20px;
  }
  
  .product-list h2 {
    margin: 20px 0;
    color: #333;
  }
  
  .products {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
  }

  .logout-btn {
  margin-left: 15px;
  padding: 5px 10px;
  background-color: #fff;
  color: #ff0036;
  border: 1px solid #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.logout-btn:hover {
  background-color: #ff0036;
  color: #fff;
}

.filter-sort-area {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin: 20px 0;
  padding: 15px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-item, .sort-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label, .sort-item label {
  font-weight: bold;
  color: #555;
}

.filter-item select, .sort-item select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.filter-item input[type="number"] {
  width: 80px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
  </style>