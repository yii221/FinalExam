<template>
    <aside class="sidebar">
      <nav>
        <ul>
          <li>
            <router-link to="/PersonProfile" class="sidebar-item">
              <i class="icon">👤</i>
              <span>个人主页</span>
            </router-link>
          </li>
          <li>
            <router-link to="/OrderPage" class="sidebar-item">
              <i class="icon">📦</i>
              <span>我的订单</span>
            </router-link>
          </li>
          <li>
            <router-link to="/ProductPage" class="sidebar-item">
              <i class="icon">🛍️</i>
              <span>我的商品</span>
            </router-link>
          </li>
          <li>
            <router-link to="/favorites" class="sidebar-item">
              <i class="icon">❤️</i>
              <span>我的收藏</span>
            </router-link>
          </li>
        </ul>
      </nav>
    </aside>
  </template>
  
  <script>
  export default {
    name: 'TheSidebar'
  }
  </script>
  
  <style scoped>
  .sidebar {
    width: 200px;
    background-color: white;
    padding: 20px 0;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
    height: calc(100vh - 70px);
    position: sticky;
    top: 70px;
  }
  
  .sidebar ul {
    list-style: none;
  }
  
  .sidebar-item {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    color: #333;
    text-decoration: none;
    transition: background-color 0.3s;
  }
  
  .sidebar-item:hover {
    background-color: #f5f5f5;
  }
  
  .sidebar-item .icon {
    margin-right: 10px;
    font-size: 18px;
  }
  
  .sidebar-item span {
    font-size: 16px;
  }
  
  .router-link-active {
    background-color: #fff0f0;
    color: #ff0036;
    font-weight: bold;
  }
  </style>