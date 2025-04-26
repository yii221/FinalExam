<template>
    <div class="admin-home">
      <el-container style="height: 100vh;">
        <el-aside width="200px" style="background-color: #2d3a4b; color: #fff;">
          <div class="logo" style="text-align:center; padding:20px;">管理员系统</div>
          <el-menu
            default-active="users"
            class="el-menu-vertical-demo"
            background-color="#2d3a4b"
            text-color="#fff"
            active-text-color="#ffd04b"
            @select="handleMenuSelect"
          >
            <el-menu-item index="users">查看用户</el-menu-item>
            <el-menu-item index="products">查看商品</el-menu-item>
            <el-menu-item index="orders">查看订单</el-menu-item>
          </el-menu>
        </el-aside>
  
        <el-container>
          <el-header style="background: #fff; text-align: right; padding: 0 20px;">
            欢迎，管理员
          </el-header>
  
          <el-main>
            <div v-if="currentView === 'users'">
              <user-list></user-list>
            </div>
            <div v-else-if="currentView === 'products'">
              <product-list></product-list>
            </div>
            <div v-else-if="currentView === 'orders'">
              <order-list></order-list>
            </div>
          </el-main>
        </el-container>
      </el-container>
    </div>
  </template>
  
  <script>
  import UserList from '../components/UserList.vue'
  import ProductList from '../components/ProductList.vue'
  import OrderList from '../components/AdminOrderList.vue'
  
  export default {
    name: 'AdminHome',
    components: {
      UserList,
      ProductList,
      OrderList
    },
    data() {
      return {
        currentView: 'users'
      }
    },
    methods: {
      handleMenuSelect(key) {
        this.currentView = key;
      }
    }
  }
  </script>
  
  <style scoped>
  .el-menu {
    border-right: none;
  }
  </style>
  