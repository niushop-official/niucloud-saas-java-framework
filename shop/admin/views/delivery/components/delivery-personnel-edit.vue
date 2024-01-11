<template>
	<el-dialog v-model="showDialog" :title="formData.deliver_id ? t('updateDeliver') : t('addDeliveryPersonnel')"   width="480" class="diy-dialog-wrap" :destroy-on-close="true">
		<el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
			<el-form-item :label="t('deliverName')" prop="deliver_name">
				<el-input v-model="formData.deliver_name" clearable :placeholder="t('deliverNamePlaceholder')"  class="input-width"/>
			</el-form-item>
			<el-form-item :label="t('deliverMobile')" prop="deliver_mobile">
				<el-input v-model="formData.deliver_mobile" clearable :placeholder="t('deliverMobilePlaceholder')"  class="input-width"/>
			</el-form-item>
		</el-form>
		<template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{
                    t('confirm')
                }}</el-button>
            </span>
		</template>
	</el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { addShopDeliver, editShopDeliver, getShopDeliverInfo } from '@/addon/shop/api/delivery'

const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    deliver_id: '',
    deliver_name: '',
    deliver_mobile: ''

}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        deliver_name: [
            { required: true, message: t('deliverNamePlaceholder'), trigger: 'blur' }
        ],
        deliver_mobile: [
            { required: true, message: t('deliverMobilePlaceholder'), trigger: 'blur' },
            { min: 11, max: 11, message: '请输入11位手机号码', trigger: 'blur' },
            {
                pattern: /^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\d{8}$/,
                message: '请输入正确的手机号码',
                trigger: 'blur'
            }
        ]
    }
})

const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    const save = formData.deliver_id ? editShopDeliver : addShopDeliver

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            const data = formData

            save(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

const setFormData = async (row: any = null) => {
    Object.assign(formData, initialFormData)
    loading.value = true
    if (row) {
        const data = await (await getShopDeliverInfo(row.deliver_id)).data
        if (data) {
            Object.keys(formData).forEach((key: string) => {
                if (data[key] != undefined) formData[key] = data[key]
            })
        }
    }
    loading.value = false
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
<style lang="scss">
	.diy-dialog-wrap .el-form-item__label {
		height: auto !important;
	}
</style>
