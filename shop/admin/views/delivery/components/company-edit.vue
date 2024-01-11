<!-- eslint-disable n/handle-callback-err -->
<template>
	<el-dialog v-model="showDialog" :title="formData.company_id ? t('updateCompany') : t('addCompany')" width="500px"   class="diy-dialog-wrap" :destroy-on-close="true">
		<el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
			<el-form-item :label="t('companyName')" prop="company_name">
				<el-input v-model="formData.company_name" clearable :placeholder="t('companyNamePlaceholder')"  class="input-width"/>
			</el-form-item>
			<el-form-item :label="t('logo')">
				<upload-image v-model="formData.logo"/>
			</el-form-item>
			<el-form-item :label="t('url')">
				<el-input v-model="formData.url" clearable :placeholder="t('urlPlaceholder')" class="input-width"/>
			</el-form-item>
			<el-form-item :label="t('expressNo')">
				<div>
					<el-input v-model="formData.express_no" clearable :placeholder="t('expressNoPlaceholder')"  class="input-width"/>
					<p class="input-width text-[12px] text-[#999] mt-[5px] leading-[20px]">{{ t('expressNoTips') }}</p>
				</div>

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
import { addCompany, editCompany, getCompanyInfo } from '@/addon/shop/api/delivery'

const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    company_id: '',
    company_name: '',
    logo: '',
    url: '',
    express_no: ''
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        company_name: [
            { required: true, message: t('companyNamePlaceholder'), trigger: 'blur' }
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
    const save = formData.company_id ? editCompany : addCompany

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
        const data = await (await getCompanyInfo(row.company_id)).data
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
