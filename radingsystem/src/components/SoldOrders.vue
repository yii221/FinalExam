<template>
    <div class="sold-orders">
      <OrderList 
        :orders="orders" 
        :loading="loading" 
        :error="error"
        type="sold"
        @refresh="fetchOrders"
      />
    </div>
  </template>
  
  <script>
  import OrderList from '../components/OrderList.vue'
 
  
  export default {
    name: 'SoldOrders',
    components: {
      OrderList
    },
    data() {
      return {
        orders: [],
        loading: false,
        error: null
      }
    },
    created() {
      this.fetchOrders()
    },
    methods: {
      async fetchOrders() {
        const token = localStorage.getItem('authjwt');
        if (!token) {
            this.$message.error('请先登录');
            this.$router.push('/login');
            return;
        }
        try {
          const response = await this.$http.post('/TradingSystem/FetchSoldServlet',{
          },{
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
                'Authorization': `Bearer ${token}`
        },
          });
    
          this.orders = response.data;
          this.orders = response.data.map(order => {
            return {
                    ...order,
                    proImage: `http://localhost:8080/TradingSystem/${order.proImage}`,
                   
            };
        });
        } catch (error) {
          this.error = '获取购买订单失败: ' + (error.response?.data?.message || error.message)
          console.error(error)
        } finally {
          this.loading = false
        }
      }
    }
  }
  </script>