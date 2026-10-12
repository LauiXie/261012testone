<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeIndex = computed(() => route.path)
</script>

<template>
  <el-container class="app-shell">
    <el-header class="app-header">
      <div class="app-header__inner">
        <div class="app-brand">
          <span class="app-brand__badge">ID</span>
          <span class="app-brand__text">身份证管理系统</span>
        </div>
        <el-menu
          class="app-nav"
          mode="horizontal"
          :default-active="activeIndex"
          :ellipsis="false"
          router
        >
          <el-menu-item index="/idcards">身份证管理</el-menu-item>
          <el-menu-item index="/stats">数据统计</el-menu-item>
        </el-menu>
      </div>
    </el-header>

    <el-main class="app-main">
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </el-main>

    <el-footer class="app-footer">
      身份证管理系统 Demo · Vue3 + Vite + Element Plus
    </el-footer>
  </el-container>
</template>

<style scoped>
.app-shell {
  min-height: 100%;
}

.app-header {
  height: auto;
  padding: 0;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.app-header__inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
}

.app-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 60px;
  font-size: 16px;
  font-weight: 600;
}

.app-brand__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: #409eff;
  color: #fff;
  font-size: 13px;
  letter-spacing: 0.5px;
}

.app-nav {
  border-bottom: none;
  flex: 1 1 auto;
  min-width: 200px;
}

.app-main {
  padding: 0;
}

.app-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
  background: transparent;
}

@media (max-width: 768px) {
  .app-header__inner {
    padding: 0 12px;
    gap: 4px;
  }

  .app-brand {
    height: 48px;
    font-size: 15px;
  }

  .app-nav {
    width: 100%;
  }
}
</style>
