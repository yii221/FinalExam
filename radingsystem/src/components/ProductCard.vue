<template>
    <div class="goods-list">
      <div class="goods-img">
            <img :src="product.primaryImage" :alt="product.proname" />
            <div class="goods-status" :class="'status-' + product.status">
                {{ statusText(product.status) }}
            </div>
          </div>
          <div class="goods-info">
            <div class="goods-category">
              {{ getCategoryLabel(product.category) }}
            </div>
            <h3 class="goods-title">{{product.proname }}</h3>
            <p class="goods-desc">评分：（0代表暂无评分）{{ product.proscore }}</p>
            <p class="goods-desc">{{ product.description }}</p>
            <div class="goods-footer">
              <span class="goods-price">¥{{ product.price }}</span>
            </div>
          </div>
         <!-- 添加查看按钮 -->
         <div class="goods-actions">
         <button @click="checkProduct" class="btn-back">
              <i class="el-icon-back"></i> 查看更多
        </button>
        <button class="add-to-cart">加入购物车</button>
        </div>
      </div>
    
  </template>
  
  <script>
  export default {
    name: 'ProductCard',
    data(){
      return{
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
      }
    },
    props: {
      product: {
        type: Object,
        required: true
      }
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
   }
   },
   methods:{
    checkProduct() {
        console.log(this.product.proid);
        this.$router.push({
          path: '/ProductInfo',
          query: { proid: this.product.proid }
    });
    },
    getCategoryLabel(value) {
      const category = this.categories.find(item => item.value === value);
      return category ? category.label : '未知分类';
    },
   }
  }
  </script>
  
  <style scoped>
  .product-card {
    background-color: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s;
  }
  
  .product-card:hover {
    transform: translateY(-5px);
  }
  
  .product-image img {
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
  
  .product-info {
    padding: 15px;
  }
  
  .product-name {
    font-size: 16px;
    margin-bottom: 8px;
    color: #333;
  }
  
  .product-description {
    font-size: 14px;
    color: #666;
    margin-bottom: 10px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .price-container {
    margin: 10px 0;
  }
  
  .current-price {
    font-size: 18px;
    font-weight: bold;
    color: #ff0036;
    margin-right: 10px;
  }
  
  .original-price {
    font-size: 14px;
    color: #999;
    text-decoration: line-through;
  }
  
  .seller-info {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #888;
    margin: 10px 0;
  }
  
  .add-to-cart {
    width: 100%;
    padding: 8px 0;
    background-color: #ff0036;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.3s;
  }
  
  .add-to-cart:hover {
    background-color: #e60033;
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


  </style>