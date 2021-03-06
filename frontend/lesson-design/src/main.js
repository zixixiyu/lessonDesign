import Vue from 'vue'
import './plugins/axios'
import App from './App.vue'
import './plugins/iview.js'
import router from './router'
import store from './store'

Vue.config.productionTip = false
Vue.prototype.$picHost = 'http://localhost:8080/pic/'

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
