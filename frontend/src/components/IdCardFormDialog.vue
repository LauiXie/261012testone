<script setup>
import { computed, nextTick, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createIdCard, updateIdCard } from '../api/idcard'
import { isValidIdNumber, parseIdNumber } from '../utils/idcard'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  record: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'saved'])

const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  name: '',
  idNumber: '',
  gender: '',
  birthDate: '',
  address: ''
})

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isEdit = computed(() => Boolean(props.record && props.record.id))
const title = computed(() => (isEdit.value ? '编辑身份证信息' : '新增身份证信息'))

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 64, message: '姓名长度不能超过 64 个字符', trigger: 'blur' }
  ],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (isValidIdNumber(value)) {
          callback()
        } else {
          callback(new Error('身份证号格式不正确，需为 18 位且校验位合法'))
        }
      },
      trigger: 'blur'
    }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  address: [{ max: 255, message: '地址长度不能超过 255 个字符', trigger: 'blur' }]
}

// 身份证号合法时自动带出性别与出生日期
watch(
  () => form.idNumber,
  (value) => {
    const parsed = parseIdNumber(value)
    if (parsed.gender) {
      form.gender = parsed.gender
    }
    if (parsed.birthDate) {
      form.birthDate = parsed.birthDate
    }
  }
)

watch(
  () => props.modelValue,
  (open) => {
    if (!open) {
      return
    }
    const record = props.record
    form.name = record ? record.name || '' : ''
    form.idNumber = record ? record.idNumber || '' : ''
    form.gender = record ? record.gender || '' : ''
    form.birthDate = record ? record.birthDate || '' : ''
    form.address = record ? record.address || '' : ''
    nextTick(() => {
      if (formRef.value) {
        formRef.value.clearValidate()
      }
    })
  }
)

async function submit() {
  if (!formRef.value) {
    return
  }
  let valid = false
  try {
    valid = await formRef.value.validate()
  } catch (error) {
    valid = false
  }
  if (!valid) {
    return
  }

  const payload = {
    name: form.name.trim(),
    idNumber: form.idNumber.trim().toUpperCase(),
    gender: form.gender,
    birthDate: form.birthDate || null,
    address: form.address ? form.address.trim() : ''
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateIdCard(props.record.id, payload)
      ElMessage.success('修改成功')
    } else {
      await createIdCard(payload)
      ElMessage.success('新增成功')
    }
    visible.value = false
    emit('saved')
  } catch (error) {
    // 错误信息已由 axios 拦截器统一提示
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="520px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" maxlength="64" clearable placeholder="请输入姓名" />
      </el-form-item>

      <el-form-item label="身份证号" prop="idNumber">
        <el-input
          v-model="form.idNumber"
          maxlength="32"
          clearable
          placeholder="请输入 18 位身份证号"
        />
        <div class="form-tip">性别与出生日期会根据身份证号自动带出，也可手动修改。</div>
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio value="男">男</el-radio>
          <el-radio value="女">女</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="出生日期" prop="birthDate">
        <el-date-picker
          v-model="form.birthDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择出生日期"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="地址" prop="address">
        <el-input
          v-model="form.address"
          type="textarea"
          :rows="2"
          maxlength="255"
          show-word-limit
          placeholder="请输入地址"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.form-tip {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}
</style>
