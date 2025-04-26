<template>
    <div class="order-list">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="error" class="error">
        {{ error }}
        <button @click="$emit('refresh')">重试</button>
      </div>
      <div v-else-if="orders.length === 0" class="empty">
        暂无{{ type === 'bought' ? '购买' : '卖出' }}订单
      </div>
      <div v-else class="orders">
        <div v-for="order in orders" :key="order.proid" class="order-item">
          <div class="order-header">
            <span class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </span>
            <span class="payment-method">
              {{ getPaymentMethodText(order.payment) }}
            </span>
          </div>
          <div class="order-details">
            <div class="product-info">
              <img :src="order.proImage" class="product-image" />
              <div class="product-desc">
                <h3>{{ order.proname }}</h3>
              </div>
            </div>
            <div class="order-meta">
              <div class="quantity">x{{ order.quantity }}</div>
              <div class="total">总价: ¥{{ order.total }}</div>
            </div>
          </div>
          <div class="order-footer">
            <span class="order-time">{{ formatDate(order.createdtime) }}</span>
            <div class="actions">
              <!-- 卖家操作 -->
              <button v-if="type === 'sold' && parseInt(order.status) === -1" 
                      @click="updateOrderStatus(order.orderid, 0)">
                发货
              </button>
              
              <!-- 买家操作 -->
              <button v-if="type === 'bought' && parseInt(order.status) === 0" 
                      @click="updateOrderStatus(order.orderid, 1)">
                确认收货
              </button>
              
              <button v-if="parseInt(order.status) === -1" 
                      @click="updateOrderStatus(order.orderid, 2)">
                取消订单
              </button>
  
              <!-- 评价按钮 -->
              <button v-if="type === 'bought' && parseInt(order.status) === 1 && !order.hasReviewed"
                      @click="openReviewDialog(order)"
                      class="review-button">
                评价订单
              </button>
            </div>
          </div>
        </div>
      </div>
  
<!-- 评价弹窗 -->
<div v-if="showReviewDialog" class="review-dialog-overlay">
  <div class="review-dialog">
    <h3>评价订单 #{{ reviewData.orderId }}</h3>

    <div class="review-section">
      <h4>商品评分</h4>
      <select v-model="reviewData.productRating">
        <option v-for="score in 5" :key="'product-score-'+score" :value="score">
          {{ score }} 分
        </option>
      </select>
    </div>

    <div class="review-section">
      <h4>卖家服务评分</h4>
      <select v-model="reviewData.sellerRating">
        <option v-for="score in 5" :key="'seller-score-'+score" :value="score">
          {{ score }} 分
        </option>
      </select>
    </div>

    <div class="review-section">
      <h4>文字评价</h4>
      <textarea v-model="reviewData.comment" placeholder="请写下您的购物体验..."></textarea>
    </div>

    <div class="dialog-buttons">
      <button @click="submitReview" class="submit-btn">提交评价</button>
      <button @click="closeReviewDialog" class="cancel-btn">取消</button>
    </div>
    </div>
    </div>
    </div>
  </template>
  
  <script>
  //import axios from 'axios'
  
  export default {
    name: 'OrderList',
    props: {
      orders: {
        type: Array,
        default: () => []
      },
      loading: Boolean,
      error: String,
      type: {
        type: String,
        validator: value => ['bought', 'sold'].includes(value)
      }
    },
    data() {
      return {
        showReviewDialog: false,
        reviewData: {
          orderId: null,
          productRating: 5,
          sellerRating: 5,
          comment: ''
        }
      }
    },
    methods: {
      getStatusText(status) {
        const statusMap = {
          '-1': '待发货',
          '0': '待收货',
          '1': '订单完成',
          '2': '订单已取消'
        }
        return statusMap[status] || '未知状态'
      },
      getStatusClass(status) {
        const classMap = {
          '-1': 'pending',
          '0': 'shipped',
          '1': 'completed',
          '2': 'cancelled'
        }
        return `status-${classMap[status] || 'default'}`
      },
      getPaymentMethodText(payment) {
        return payment === 'online' ? '线上支付' : payment === 'cod' ? '货到付款' : '未知支付方式'
      },
      formatDate(timestamp) {
        return new Date(timestamp).toLocaleString()
      },
      async updateOrderStatus(orderId, newStatus) {
        try {
          const response = await this.$http.post('/TradingSystem/UpdateOrderStatusServlet', {
            orderid: orderId,
            movement: newStatus
          }, {
            headers: {
              'Content-Type': 'application/json;charset=UTF-8',
            },
          });
          alert(response.data.message)
          this.$emit('refresh')
        } catch (err) {
          alert('更新订单状态失败: ' + (err.response?.data?.message || err.message))
          console.error(err)
        }
      },
      openReviewDialog(order) {
        this.reviewData = {
          productRating: 5,
          sellerRating: 5,
          comment: '',
          orderid:order.orderid,
          proid:order.proid,
          sellerid:order.sellerid,
          buyerid:order.buyerid,
        }
        console.log("订单号为："+order.orderid);
        this.showReviewDialog = true
      },
      closeReviewDialog() {
        this.showReviewDialog = false
      },
      async submitReview() {
        if (!this.reviewData.comment.trim()) {
          alert('请填写评价内容')
          return
        }
  
        try {
        
          const response = await this.$http.post('/TradingSystem/SubmitReviewServlet', {
            productRating: this.reviewData.productRating,
            sellerRating: this.reviewData.sellerRating,
            comment: this.reviewData.comment,
            orderid:this.reviewData.orderid,
            proid:this.reviewData.proid,
            sellerid:this.reviewData.sellerid,
            buyerid:this.reviewData.buyerid,

          }, {
            headers: {
              'Content-Type': 'application/json;charset=UTF-8',
            },
          });
         if(response.data.status === "success")
         {
             alert('评价提交成功！')
          this.closeReviewDialog()
          this.$emit('refresh')
         }
         
        } catch (err) {
          alert('评价提交失败: ' + (err.response?.data?.message || err.message))
          console.error(err)
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .order-list {
    margin-top: 20px;
  }
  
  .loading, .error, .empty {
    text-align: center;
    padding: 20px;
    color: #666;
  }
  
  .error {
    color: #f56c6c;
  }
  
  .order-item {
    border: 1px solid #ddd;
    border-radius: 4px;
    padding: 15px;
    margin-bottom: 15px;
    background: #fff;
  }
  
  .order-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
  
  .order-status {
    padding: 3px 8px;
    border-radius: 4px;
    font-size: 12px;
  }
  
  .status-pending {
    background-color: #f0f0f0;
    color: #666;
  }
  
  .status-shipped {
    background-color: #e6f7ff;
    color: #1890ff;
  }
  
  .status-completed {
    background-color: #f6ffed;
    color: #52c41a;
  }
  
  .status-cancelled {
    background-color: #fff2f0;
    color: #f5222d;
  }
  
  .payment-method {
    padding: 3px 8px;
    border-radius: 4px;
    font-size: 12px;
    background-color: #f0f9eb;
    color: #67c23a;
  }
  
  .order-details {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
  }
  
  .product-info {
    display: flex;
    flex: 1;
  }
  
  .product-image {
    width: 80px;
    height: 80px;
    object-fit: cover;
    margin-right: 15px;
    border: 1px solid #eee;
    border-radius: 4px;
  }
  
  .product-desc {
    flex: 1;
  }
  
  .product-desc h3 {
    margin: 0 0 5px 0;
    font-size: 16px;
  }
  
  .order-meta {
    text-align: right;
    min-width: 150px;
  }
  
  .total {
    font-weight: bold;
    color: #f56c6c;
  }
  
  .quantity {
    color: #666;
  }
  
  .order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 10px;
    border-top: 1px solid #eee;
  }
  
  .order-time {
    color: #999;
    font-size: 12px;
  }
  
  .actions {
    display: flex;
  }
  
  .actions button {
    margin-left: 8px;
    padding: 5px 10px;
    font-size: 12px;
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
    color: #333;
    min-width: 80px;
  }
  
  .actions button:hover {
    opacity: 0.8;
  }
  
  .review-button {
    background-color: #67c23a;
    color: white !important;
    border-color: #67c23a !important;
  }
  
  /* 评价弹窗样式 */
  .review-dialog-overlay {
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
  
  .review-dialog {
    background: white;
    padding: 20px;
    border-radius: 8px;
    width: 500px;
    max-width: 90%;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  }
  
  .review-dialog h3 {
    margin-top: 0;
    text-align: center;
    color: #333;
  }
  
  .review-section {
    margin-bottom: 20px;
  }
  
  .review-section h4 {
    margin-bottom: 10px;
    color: #666;
  }
  
  .rating {
    font-size: 24px;
    margin: 10px 0;
  }
  
  .rating span {
    color: #ccc;
    cursor: pointer;
    margin-right: 8px;
  }
  
  .rating span.active {
    color: #ffcc00;
  }
  
  .review-dialog textarea {
    width: 100%;
    height: 100px;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    resize: vertical;
  }
  
  .dialog-buttons {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
  
  .dialog-buttons button {
    padding: 8px 15px;
    margin-left: 10px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
  }
  
  .submit-btn {
    background-color: #409eff;
    color: white;
    border: none;
  }
  
  .submit-btn:hover {
    background-color: #66b1ff;
  }
  
  .cancel-btn {
    background-color: white;
    border: 1px solid #dcdfe6;
    color: #606266;
  }
  
  .cancel-btn:hover {
    color: #409eff;
    border-color: #c6e2ff;
  }
  </style>