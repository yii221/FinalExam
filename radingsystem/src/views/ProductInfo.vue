<template>
    <div class="product-detail container my-5">
      <!-- 面包屑导航
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><a href="#">首页</a></li>
          <li class="breadcrumb-item"><a href="#">{{ product.category }}</a></li>
          <li class="breadcrumb-item active">{{ product.proname }}</li>
        </ol>
      </nav> -->
      <button @click="goToHome" class="btn-back">
        <i class="el-icon-back"></i> 返回主页
      </button>
      <div class="row">
        <!-- 商品图片展示区 -->
        <div class="col-md-6">
            <div class="main-image mb-3">
        <img 
            :src="mainImage" 
            class="img-fluid rounded" 
            alt="商品主图"
            v-if="mainImage" >
        <div v-else class="img-placeholder">暂无图片</div>
      </div>

        <!-- 缩略图列表 -->
        <div class="thumbnail-container d-flex flex-wrap">
            <img 
            v-for="(image, index) in productImages" 
                :key="index"
                :src="image" 
                class="thumbnail"
            @click="changeMainImage(image)"
            >
        </div>
    </div>
  
        <!-- 商品信息区 -->
    <div class="col-md-6">
          <h1>{{ product.proname }}</h1>
          <div class="price-section mb-3">
            <span class="h4 text-danger">¥{{ product.price }}</span>
          </div>
          
          <div class="stock-status mb-3">
            <span class="badge" >{{ statusText }}</span>
            <span class="text-muted ms-2">库存: {{ product.stock }}</span>
          </div>
          
          <div class="description mb-4">
            <h5>商品描述</h5>
            <p>{{ product.description }}</p>
            <h5>商品评分(0则代表暂无商品评分)</h5>
            <p>{{ product.proscore }}</p>
          </div>
          
          <div class="quantity-selector mb-4">
            <label for="quantity" class="form-label">数量</label>
            <div class="input-group" style="width: 150px;">
              <button class="btn btn-outline-secondary" @click="decreaseQuantity">-</button>
              <input type="number" class="form-control text-center" v-model.number="quantity" min="1">
              <button class="btn btn-outline-secondary" @click="increaseQuantity">+</button>
            </div>
          </div>
          
          <div class="action-buttons">
            <button class="btn btn-primary btn-lg me-2" @click="addToCart">加入购物车</button>
            <button class="btn btn-outline-danger btn-lg" @click="buyNow">立即购买</button>
          </div>
        </div>
      </div>
   



       <!-- 简化版购买弹窗 -->
    <div class="checkout-modal" v-if="showCheckout">
      <div class="modal-overlay" @click="showCheckout = false"></div>
      <div class="modal-content">
        <div class="modal-header">
          <h5>确认订单</h5>
          <button @click="showCheckout = false" class="close-btn">&times;</button>
        </div>
        
        <div class="modal-body">
          <div class="product-info">
            <img :src="mainImage" class="product-image">
            <div>
              <h6>{{ product.proname }}</h6>
              <p>¥{{ product.price }} × {{ quantity }} = ¥{{ (product.price * quantity).toFixed(2) }}</p>
            </div>
          </div>
          
          <div class="form-group">
            <label>收货人</label>
            <input type="text" v-model="shippingInfo.name" placeholder="请输入收货人姓名">
          </div>
          
          <div class="form-group">
            <label>手机号</label>
            <input type="tel" v-model="shippingInfo.phone" placeholder="请输入手机号">
          </div>
          
          <div class="form-group">
            <label>收货地址</label>
            <textarea v-model="shippingInfo.address" placeholder="请输入详细地址"></textarea>
          </div>
          
          <div class="payment-methods">
            <label>支付方式：</label>
            <div>
              <label>
                <input type="radio" v-model="paymentMethod" value="online"> 在线支付
              </label>
              <label>
                <input type="radio" v-model="paymentMethod" value="cod"> 货到付款
              </label>
            </div>
          </div>
          
          <div class="total-price">
            总计：<span class="price">¥{{ (product.price * quantity).toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="showCheckout = false" class="btn-cancel">取消</button>
          <button @click="confirmOrder" class="btn-confirm">确认订单</button>
        </div>
      </div>
    </div>

    <div class="product-reviews mt-5">
      <h3>商品评价</h3>
      <div class="review-list" v-if="reviews.length > 0">
        <div class="review-item card mb-3" v-for="(review, index) in reviews" :key="index">
          <div class="card-body">
            <div class="review-content mt-2">
              <p>{{ review }}</p>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-reviews">
        <p class="text-muted">暂无评价，快来成为第一个评价的人吧~</p>
      </div>
    </div>

</div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        reviews: [], // 存储评论数据
        name:'ProductInfo',
        product: '',
        proid:'',
        Goods:{
          proname: '',
          description: '',
          price: 0,
          category: '',
          status: '',
          stock: 0,
          userid:'',
        },    
        productImages: [],
        mainImage: '',
        quantity: 1,
        // 新增购买弹窗相关数据
        showCheckout: false,
        paymentMethod: 'online',
        shippingInfo: {
        name: '',
        phone: '',
        address: ''
        },
      }
    },
    watch: {
      quantity(newVal) {
        if (newVal > this.product.stock) {
          this.$message.warning(`库存仅剩 ${this.product.stock} 件`);
          this.quantity = this.product.stock;
        }
      }
    },
    computed: {
      statusText() {
         const statusMap = {
            0: '在售',
            1: '无库存',
            '-1': '已下架'
        };
    return statusMap[this.product.status] || '未知状态';
  }
    },
    mounted() {
      this.proid = this.$route.query.proid;  
      this.fetchProductData();
      this.fetchProductImages();
      this.fetchProductReviews();
    },
    methods: {
      async fetchProductReviews() {
            try {
                const response = await this.$http.post('/TradingSystem/FetchCommentServlet', {
                    proid: this.proid
                }, {
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8',
                    },
                });
                
                if (response.data && response.data.length > 0) {
                     this.reviews=response.data;
                    
                }
                
            } catch (error) {
                console.error('获取商品评论失败:', error);
                this.$message.error("加载评论失败: " + (error.response?.data?.message || '网络错误'));
            }
        },
     goToHome() {
            this.$router.push('/TheHome'); 
        },
    // 新增方法
    async confirmOrder() {
      // 简单验证
      if (!this.shippingInfo.name || !this.shippingInfo.phone || !this.shippingInfo.address) {
        alert('请填写完整的收货信息');
        return;
      }
      
      // 验证手机号
      if (!/^1[3-9]\d{9}$/.test(this.shippingInfo.phone)) {
        alert('请输入正确的手机号码');
        return;
      }
      const token = localStorage.getItem('authjwt');
      if (!token) {
        this.$message.error('请先登录');
        this.$router.push('/login');
        return;
      }
      try{ 
        this.$message.info("订单提交中...");
        const response = await this.$http.post('/TradingSystem/AddOrderServlet', {
            proid:this.proid,
            proname: this.product.proname,
            sellerid:this.product.userid,
            quantity: this.quantity,
            total: this.product.price * this.quantity,
            payment: this.paymentMethod,
            buyername:this.shippingInfo.name,
            phone:this.shippingInfo.phone,
            address:this.shippingInfo.address,
            proImage:this.product.primaryImage
        }, {
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
                'Authorization': `Bearer ${token}`
        },
        });
        
        if (!response.data){
            this.$message.error("库存不足，请重新选择数量！");
            return;
        }
        //console.log('订单信息:', order);
        alert('订单提交成功！');
        this.showCheckout = false;
        // 重置表单
        this.shippingInfo = {
            name: '',
            phone: '',
            address: ''
        };
        this.quantity = 1;
    }catch (error) {
        console.error('订单建立失败:', error);
        this.$message.error("加载失败: " + (error.response?.data?.message || '网络错误'));
    }
    },
  
    async fetchProductData() {
        //console.log(this.proid);
       try{ 
        const response = await this.$http.post('/TradingSystem/ProInfoServlet', {
            proid:this.proid
        }, {
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
        },
        });
        
        if (!response.data || Object.keys(response.data).length === 0) {
            this.$message.error("商品数据为空，请稍后重试！");
            return;
        }
        this.product = response.data;
    }catch (error) {
        console.error('获取商品图片失败:', error);
        this.$message.error("加载失败: " + (error.response?.data?.message || '网络错误'));
    }
    },
    async fetchProductImages() {
        try{ 
        const response = await this.$http.post('/TradingSystem/ProInfoImageServlet', {
            proid:this.proid
        }, {
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
        },
        });
        
        if (!response.data || Object.keys(response.data).length === 0) {
            this.$message.error("商品图片为空，请稍后重试！");
            return;
        }
        this.productImages = response.data;
        this.productImages = response.data.map(item => {
        if (item && !item.startsWith('http')) {
            return `http://localhost:8080/TradingSystem/${item}`;
        }
            return item;
        });
        // 设置默认主图
        if (this.productImages.length > 0) {
            this.mainImage = this.productImages[0];
        }
        
        }catch (error) {
        console.error('获取商品失败:', error);
        this.$message.error("加载失败: " + (error.response?.data?.message || '网络错误'));
        }
    
      },
      changeMainImage(image) {
        this.mainImage = image;
      },
      increaseQuantity() {
        this.quantity++;
      },
      decreaseQuantity() {
        if (this.quantity > 1) {
          this.quantity--;
        }
      },
      addToCart() {
        // 加入购物车逻辑
        alert(`已添加 ${this.quantity} 件商品到购物车`);
      },
      buyNow() {
        // 立即购买逻辑
        //alert(`立即购买 ${this.quantity} 件商品`);
        this.showCheckout = true;
      }
    }}
  
  </script>
  
  <style scoped>
  .product-detail {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  }
  
  .main-image {
    border: 1px solid #dee2e6;
    border-radius: 0.25rem;
    padding: 10px;
    background-color: white;
  }
  
  .thumbnail {
    width: 80px;
    height: 80px;
    object-fit: cover;
    margin-right: 10px;
    margin-bottom: 10px;
    border: 1px solid #dee2e6;
    border-radius: 0.25rem;
    cursor: pointer;
    transition: all 0.3s ease;
  }
  
  .thumbnail:hover {
    border-color: #0d6efd;
    transform: scale(1.05);
  }
  
  .price-section .text-danger {
    font-weight: bold;
  }
  
  .description {
    border-top: 1px solid #eee;
    padding-top: 15px;
  }
  
  .badge {
    font-size: 1rem;
    padding: 0.35em 0.65em;
  }

  .main-image img {
  max-width: 100%;
  height: auto;
  max-height: 350px; /* 设置主图最大高度 */
  object-fit: contain; /* 保持比例 */
}

/* 新增弹窗样式 */
.checkout-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1001;
}

.modal-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h5 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.modal-body {
  padding: 15px;
}

.product-info {
  display: flex;
  margin-bottom: 20px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  margin-right: 15px;
  border-radius: 4px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-group textarea {
  height: 80px;
}

.payment-methods {
  margin: 20px 0;
}

.payment-methods label {
  margin-right: 15px;
  cursor: pointer;
}

.total-price {
  text-align: right;
  font-size: 18px;
  margin: 20px 0;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.modal-footer {
  padding: 15px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
}

.btn-cancel, .btn-confirm {
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-cancel {
  background: #f0f0f0;
  border: 1px solid #ddd;
  margin-right: 10px;
}

.btn-confirm {
  background: #f56c6c;
  color: white;
  border: none;
}

.product-reviews {
    margin-top: 50px;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
}

.review-list {
    margin-top: 20px;
}

.review-item {
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.review-header {
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
}

.review-content {
    line-height: 1.6;
}

  </style>