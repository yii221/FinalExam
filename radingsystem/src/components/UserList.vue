<template>
    <div>
      <h2>用户列表</h2>
      <table>
        <thead>
          <tr>
            <th>姓名</th>
            <th>邮箱</th>
            <th>手机号</th>
            <th>信用分</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.name }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.phonenumber }}</td>
            <td>{{ user.creditscore }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    name:"UserList",
    data() {
      return {
        users: []
      };
    },
    mounted() {
      this.fetchUsers();
    },
    methods: {
      fetchUsers() {
        axios.get("http://localhost:8080/TradingSystem/AdminUser")
          .then(response => {
            this.users = response.data;
          })
          .catch(error => {
            console.error(error);
          });
      }
    }
  };
  </script>
  