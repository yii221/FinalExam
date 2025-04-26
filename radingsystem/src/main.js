import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import store from './store'
//import login from './views/login.vue'
//import TheHome from './views/TheHome.vue'
import http from '@/utils/http'
import 'element-ui/lib/theme-chalk/index.css';

Vue.use(ElementUI);

// 将http实例挂载到Vue原型上，方便组件内使用
Vue.prototype.$http = http
Vue.config.productionTip = false
Vue.use(ElementUI)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
