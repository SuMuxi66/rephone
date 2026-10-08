import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 127.0.0.1 而非 localhost：Docker 重建容器后 Windows 的 IPv6 回环代理可能失效，
      // localhost 被解析到 ::1 时所有 API 会挂起；显式 v4 地址不受影响
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
      // 上传的质检图片由后端 /img/** 静态托管，不代理的话开发态缩略图全是 404
      '/img': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
    },
  },
});
