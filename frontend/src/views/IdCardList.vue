<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import IdCardFormDialog from '../components/IdCardFormDialog.vue'
import { deleteIdCard, fetchIdCards } from '../api/idcard'

const loading = ref(false)
const rows = ref([])
const total = ref(0)

const query = reactive({
  keyword: '',
  page: 1,
  size: 10
})

const dialogVisible = ref(false)
const editingRecord = ref(null)

async function load() {
  loading.value = true
  try {
    const data = await fetchIdCards({
      keyword: query.keyword ? query.keyword.trim() : undefined,
      page: query.page,
      size: query.size
    })
    rows.value = (data && data.list) || []
    total.value = (data && data.total) || 0
  } catch (error) {
    rows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  load()
}

function handleReset() {
  query.keyword = ''
  query.page = 1
  query.size = 10
  load()
}

function handleSizeChange(size) {
  query.size = size
  query.page = 1
  load()
}

function handlePageChange(page) {
  query.page = page
  load()
}

function openCreate() {
  editingRecord.value = null
  dialogVisible.value = true
}

function openEdit(row) {
  editingRecord.value = Object.assign({}, row)
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      '确定删除「' + row.name + '」（' + row.idNumber + '）的身份证记录吗？删除后不可恢复。',
      '删除确认',
      {
        type: 'warning',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    )
  } catch (error) {
    return
  }

  try {
    await deleteIdCard(row.id)
    ElMessage.success('删除成功')
    if (rows.value.length === 1 && query.page > 1) {
      query.page -= 1
    }
    load()
  } catch (error) {
    // 错误信息已由 axios 拦截器统一提示
  }
}

function genderTagType(gender) {
  if (gender === '男') {
    return 'primary'
  }
  if (gender === '女') {
    return 'danger'
  }
  return 'info'
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="page-head">
      <h2 class="page-title">身份证管理</h2>
      <p class="page-desc">支持按姓名 / 身份证号搜索，新增、编辑与删除身份证信息。</p>
    </div>

    <div class="panel">
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          class="toolbar__search"
          placeholder="按姓名或身份证号搜索"
          clearable
          style="width: 260px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div class="toolbar-spacer"></div>
        <el-button type="primary" plain @click="openCreate">新增</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="rows"
        border
        stripe
        empty-text="暂无身份证数据"
        style="width: 100%"
      >
        <el-table-column prop="name" label="姓名" min-width="100" show-overflow-tooltip />
        <el-table-column prop="idNumber" label="身份证号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="gender" label="性别" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.gender" :type="genderTagType(row.gender)" effect="light">
              {{ row.gender }}
            </el-tag>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="130">
          <template #default="{ row }">{{ row.birthDate || '-' }}</template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">{{ row.address || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          :current-page="query.page"
          :page-size="query.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <IdCardFormDialog v-model="dialogVisible" :record="editingRecord" @saved="load" />
  </div>
</template>

<style scoped>
.page-head {
  margin-bottom: 16px;
}

.muted {
  color: #c0c4cc;
}
</style>
