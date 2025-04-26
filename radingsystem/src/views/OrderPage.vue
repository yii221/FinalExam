<template>
    <div class="orders-container">
      <h1>我的订单</h1>
      <!-- 添加返回按钮 -->
      <button @click="goToHome" class="btn-back">
        <i class="el-icon-back"></i> 返回主页
      </button>
      <div class="tabs">
        <button 
          :class="{ active: activeTab === 'bought' }" 
          @click="activeTab = 'bought'"
        >
          我购买的
        </button>
        <button 
          :class="{ active: activeTab === 'sold' }" 
          @click="activeTab = 'sold'"
        >
          我卖出的
        </button>
      </div>
      
      <div class="tab-content">
        <BoughtOrders v-if="activeTab === 'bought'" />
        <SoldOrders v-else />
      </div>
    </div>
  </template>
  
  <script>
  import BoughtOrders from '../components/BoughtOrders.vue'
  import SoldOrders from '../components/SoldOrders.vue'
  
  export default {
    name: 'OrderPage',
    components: {
      BoughtOrders,
      SoldOrders
    },
    data() {
      return {
        activeTab: 'bought'
      }
    },
    methods:{
        goToHome() {
            this.$router.push('/TheHome'); 
        },
    }
  }
  </script>
  
  <style scoped>
  .orders-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .tabs {
    margin: 20px 0;
    border-bottom: 1px solid #ddd;
  }
  
  .tabs button {
    padding: 10px 20px;
    background: none;
    border: none;
    cursor: pointer;
    font-size: 16px;
    margin-right: 10px;
  }
  
  .tabs button.active {
    border-bottom: 2px solid #42b983;
    color: #42b983;
    font-weight: bold;
  }
  
  .tab-content {
    margin-top: 20px;
  }
  </style>