<template>
    <el-form ref="formRef" :model="formData" :rules="formRules">
        <el-form-item label="" prop="num">
            <el-checkbox v-model="formData.is_use" :true-label="1" :false-label="0" label="" size="large" />
            <span class="ml-[10px] el-form-item__label">送</span>
            <div class="w-[70px]">
                <el-input v-model.trim="formData.num" clearable />
            </div>
            <span class="ml-[15px] el-form-item__label">积分</span>
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue'
import { FormRules } from 'element-plus'
import Test from '@/utils/test'

const props = defineProps({
    modelValue: {
        type: Object,
        default: () => {
            return {}
        }
    }
})
const emits = defineEmits(['update:modelValue'])

const formData = ref({
    is_use: 0,
    num: ''
})

const formRef = ref(null)

const formRules = reactive<FormRules>({
    num: [
        {
            validator: (rule: any, value: any, callback: Function) => {
                if (formData.value.is_use) {
                    if (Test.empty(formData.value.num)) {
                        callback('请输入发放积分数量')
                    }
                    if (!Test.digits(formData.value.num)) {
                        callback('积分数量格式错误')
                    }
                    if (formData.value.num <= 0) {
                        callback('积分数量不能小于等于0')
                    }
                    callback()
                } else {
                    callback()
                }
            }
        }
    ]
})

const value = computed({
    get () {
        return props.modelValue
    },
    set (value) {
        emits('update:modelValue', value)
    }
})

watch(() => value.value, (nval, oval) => {
    if ((!oval || !Object.keys(oval).length) && Object.keys(nval).length) {
        formData.value = value.value
    }
}, { immediate: true })

watch(() => formData.value, () => {
    value.value = formData.value
}, { deep: true })

const verify = async () => {
    let verify = true
    await formRef.value?.validate((valid) => {
        verify = valid
    })
    return verify
}

defineExpose({
    verify
})
</script>

<style lang="scss" scoped>
</style>
