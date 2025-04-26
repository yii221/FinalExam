<template>
    <div>
      <h2>订单列表</h2>
      <table>
        <thead>
          <tr>
            <th>订单ID</th>
            <th>商品名</th>
            <th>数量</th>
            <th>总价</th>
            <th>购买人</th>
            <th>联系电话</th>
            <th>收货地址</th>
            <th>订单状态</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.orderid">
            <td>{{ order.orderid }}</td>
            <td>{{ order.proname }}</td>
            <td>{{ order.quantity }}</td>
            <td>{{ order.total }}</td>
            <td>{{ order.buyername }}</td>
            <td>{{ order.phone }}</td>
            <td>{{ order.address }}</td>
            <td>{{ order.status }}</td>
            <td>{{ order.createdtime }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    name:"AdminOrderList",
    data() {
      return {
        orders: []
      };
    },
    mounted() {
      this.fetchOrders();
    },
    methods: {
      fetchOrders() {
        axios.get("http://localhost:8080/TradingSystem/AdminOrders")
          .then(response => {
            this.orders = response.data;
          })
          .catch(error => {
            console.error(error);
          });
      }
    }
  };
  </script>
  