const { defineConfig } = require('@vue/cli-service')
module.exports = {
  // vue.config.js
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/TradingSystem'  // 把/api替换为/TradingSystem
        },
        // 防止302重定向问题
        onProxyRes: function(proxyRes) {
          if (proxyRes.headers.location && proxyRes.headers.location.includes('8080')) {
            proxyRes.headers.location = proxyRes.headers.location.replace(
              '8080', 
              '8081'
            );
          }
        }
    }
  }
}};


