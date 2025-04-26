import Vue from 'vue'
import VueRouter from 'vue-router'
import login from '../views/login.vue'
import TheHome from '../views/TheHome.vue'
//import axios from 'axios'
import axios from '@/utils/http'
import PersonProfile from '@/views/PersonProfile.vue'
import ProductPage from '../views/ProductPage.vue'
import ProductInfo from '../views/ProductInfo.vue'
import OrderPage from '../views/OrderPage.vue'
import AdminLogin from '@/views/AdminLogin.vue'
import UserList from '@/components/UserList.vue'
import AdminOrderList from '@/components/OrderList.vue'
import ProductList from '@/components/ProductList.vue'
import AdminHome from '@/views/AdminHome.vue'
Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'TheLogin',     
    component: login
  },
  {
    path: '/TheHome',
    name: 'TheHome',
    component: TheHome,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: '/PersonProfile',
    name: 'PersonProfile',
    component: PersonProfile
  },
  {
    path:'/ProductPage',
    name:'MyGoods',
    component: ProductPage
  },
  {
    path:'/ProductInfo',
    name:'ProductInfo',
    component: ProductInfo
  },
  {
    path:'/OrderPage',
    name:'OrderPage',
    component: OrderPage
  },
  {
    path:'/AdminLogin',
    name:'AdminLogin',
    component:AdminLogin
  },
  {
    path:'/UserList',
    name:'UserList',
    component:UserList
  },
  {
    path:'/ProductList',
    name:'ProductList',
    component:ProductList
  },
  {
    path:'/AdminOrderList',
    name:'AdminOrderList',
    component:AdminOrderList
  },
  {
    path:'/AdminHome',
    name:'AdminHome',
    component:AdminHome
  },
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

// 修改全局前置守卫
router.beforeEach((to, from, next) => {
  // 1. 白名单路径直接放行
  const whiteList = ['/login', '/register','/AdminLogin']
  if (whiteList.includes(to.path)) {
    return next()
  }
  

    // 2. 检查是否存在有效 Token
    const token1 = localStorage.getItem('authjwt');
    const token2 = localStorage.getItem('adminAuthjwt');
    if (!token1 || !token2) {
      return next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  

    axios.post('http://localhost:8080/TradingSystem/intercept', {
      targetUrl: `http://localhost:8081/#${to.path}`
    },{
      headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('authjwt')
    }}).then(response => {
      if (response.data.allow) {
        next(); // 允许跳转
      } else {
        alert('未登录，请重新登录');
        return next({
          path: '/login',
          query: { redirect: to.fullPath }
        }) // 阻止跳转
      }
    }).catch(error => {
      console.error('Error:', error);
      alert('An error occurred');
      next(false); // 阻止跳转
    });
    
  


  next()
});



export default router
