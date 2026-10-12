import { createRouter, createWebHistory } from 'vue-router'

// 路由级懒加载：列表页与统计页拆成独立 chunk，首屏只加载当前页面
const routes = [
  { path: '/', redirect: '/idcards' },
  {
    path: '/idcards',
    name: 'idcards',
    component: () => import('../views/IdCardList.vue'),
    meta: { title: '身份证管理' }
  },
  {
    path: '/stats',
    name: 'stats',
    component: () => import('../views/IdCardStats.vue'),
    meta: { title: '数据统计' }
  },
  { path: '/:pathMatch(.*)*', redirect: '/idcards' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  const title = to.meta && to.meta.title
  document.title = title ? title + ' - 身份证管理系统' : '身份证管理系统'
})

export default router
