<template>
    <div>
      <h2>商品列表</h2>
      <table>
        <thead>
          <tr>
            <th>商品名</th>
            <th>价格</th>
            <th>库存</th>
            <th>分类</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.proid">
            <td>{{ product.proname }}</td>
            <td>{{ product.price }}</td>
            <td>{{ product.stock }}</td>
            <td>{{ product.category }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    name:"ProductList",
    data() {
      return {
        products: []
      };
    },
    mounted() {
      this.fetchProducts();
    },
    methods: {
      fetchProducts() {
        axios.get("http://localhost:8080/TradingSystem/AdminProducts")
          .then(response => {
            this.products = response.data;
          })
          .catch(error => {
            console.error(error);
          });
      }
    }
  };
  </script>
  