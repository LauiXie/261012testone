<script setup>
import { computed, onMounted, ref } from 'vue'
import { fetchStats } from '../api/idcard'
import { toPercentText } from '../utils/idcard'

const loading = ref(false)
const failed = ref(false)
const stats = ref(null)

const total = computed(() => (stats.value && stats.value.total) || 0)
const maleCount = computed(() => (stats.value && stats.value.maleCount) || 0)
const femaleCount = computed(() => (stats.value && stats.value.femaleCount) || 0)
const otherCount = computed(() => (stats.value && stats.value.otherCount) || 0)

const maleRatioText = computed(() => toPercentText(stats.value && stats.value.maleRatio))
const femaleRatioText = computed(() => toPercentText(stats.value && stats.value.femaleRatio))

const maleBarPercent = computed(() => ratioToPercent(stats.value && stats.value.maleRatio))
const femaleBarPercent = computed(() => ratioToPercent(stats.value && stats.value.femaleRatio))

const ageGroups = computed(() => (stats.value && stats.value.ageGroups) || [])

const ageGroupMax = computed(() => {
  return ageGroups.value.reduce((max, group) => Math.max(max, Number(group.count) || 0), 0)
})

function ratioToPercent(ratio) {
  const value = Number(ratio)
  if (!Number.isFinite(value)) {
    return 0
  }
  return Math.min(100, Math.max(0, Math.round(value * 1000) / 10))
}

function ageGroupPercent(count) {
  const max = ageGroupMax.value
  const value = Number(count) || 0
  if (!max) {
    return 0
  }
  return Math.round((value / max) * 1000) / 10
}

async function load() {
  loading.value = true
  failed.value = false
  try {
    stats.value = await fetchStats()
  } catch (error) {
    stats.value = null
    failed.value = true
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="page-head">
      <h2 class="page-title">数据统计</h2>
      <p class="page-desc">总数、男女数量与占比、年龄段分布，数据来自 /api/idcards/stats。</p>
    </div>

    <el-alert
      v-if="failed"
      type="error"
      show-icon
      :closable="false"
      title="统计数据加载失败，请确认后端服务已启动（http://localhost:8080）。"
      class="stats-alert"
    >
      <template #default>
        <el-button type="primary" link @click="load">重新加载</el-button>
      </template>
    </el-alert>

    <div v-loading="loading" class="stats-body">
      <el-row :gutter="16" class="stats-cards">
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-card__label">总人数</div>
            <div class="stat-card__value">{{ total }}</div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-card__label">男性</div>
            <div class="stat-card__value stat-card__value--male">{{ maleCount }}</div>
            <div class="stat-card__ratio">占比 {{ maleRatioText }}</div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-card__label">女性</div>
            <div class="stat-card__value stat-card__value--female">{{ femaleCount }}</div>
            <div class="stat-card__ratio">占比 {{ femaleRatioText }}</div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="stat-card">
            <div class="stat-card__label">其他 / 未标注</div>
            <div class="stat-card__value">{{ otherCount }}</div>
          </div>
        </el-col>
      </el-row>

      <div class="panel stats-panel">
        <h3 class="panel-title">性别占比</h3>
        <div class="ratio-row">
          <span class="ratio-row__label">男</span>
          <el-progress
            class="ratio-row__bar"
            :percentage="maleBarPercent"
            :stroke-width="16"
            color="#409eff"
          />
          <span class="ratio-row__value">{{ maleCount }} 人 · {{ maleRatioText }}</span>
        </div>
        <div class="ratio-row">
          <span class="ratio-row__label">女</span>
          <el-progress
            class="ratio-row__bar"
            :percentage="femaleBarPercent"
            :stroke-width="16"
            color="#f56c6c"
          />
          <span class="ratio-row__value">{{ femaleCount }} 人 · {{ femaleRatioText }}</span>
        </div>
      </div>

      <div class="panel stats-panel">
        <h3 class="panel-title">年龄段分布</h3>
        <el-empty v-if="!loading && ageGroups.length === 0" description="暂无统计数据" />
        <div v-else>
          <div v-for="group in ageGroups" :key="group.label" class="ratio-row">
            <span class="ratio-row__label ratio-row__label--wide">{{ group.label }}</span>
            <el-progress
              class="ratio-row__bar"
              :percentage="ageGroupPercent(group.count)"
              :stroke-width="16"
              color="#67c23a"
            />
            <span class="ratio-row__value">{{ group.count }} 人</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-head {
  margin-bottom: 16px;
}

.stats-alert {
  margin-bottom: 16px;
}

.stats-cards {
  margin-bottom: 16px;
}

.stats-cards .el-col {
  margin-bottom: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  height: 100%;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.stat-card__label {
  color: #909399;
  font-size: 13px;
}

.stat-card__value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 600;
  line-height: 1.2;
}

.stat-card__value--male {
  color: #409eff;
}

.stat-card__value--female {
  color: #f56c6c;
}

.stat-card__ratio {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}

.stats-panel + .stats-panel {
  margin-top: 16px;
}

.panel-title {
  margin: 0 0 16px;
  font-size: 15px;
  font-weight: 600;
}

.ratio-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.ratio-row:last-child {
  margin-bottom: 0;
}

.ratio-row__label {
  width: 32px;
  flex: none;
  color: #606266;
  font-size: 13px;
}

.ratio-row__label--wide {
  width: 80px;
}

.ratio-row__bar {
  flex: 1 1 auto;
  min-width: 0;
}

.ratio-row__value {
  flex: none;
  min-width: 96px;
  text-align: right;
  color: #606266;
  font-size: 13px;
}

@media (max-width: 768px) {
  .ratio-row {
    flex-wrap: wrap;
  }

  .ratio-row__bar {
    order: 3;
    flex-basis: 100%;
  }

  .ratio-row__value {
    text-align: left;
  }
}
</style>
