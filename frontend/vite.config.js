import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 开发环境把 /api 代理到后端 http://localhost:8080
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    open: false,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  preview: {
    port: 4173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    // 把体积较大的第三方库拆成独立 chunk，避免单个文件过大
    rollupOptions: {
      output: {
        manualChunks: {
          vue: ['vue', 'vue-router'],
          'element-plus': ['element-plus'],
          axios: ['axios']
        }
      }
    }
  }
})
