import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      // 上传的质检图片由后端 /img/** 静态托管，不代理的话开发态缩略图全是 404
      '/img': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
