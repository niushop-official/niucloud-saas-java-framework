<template>
    <el-dialog v-model="showDialog" :title="formData.printer_id ? t('updatePrinter') : t('addPrinter')" width="50%" class="diy-dialog-wrap"
        :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
                <el-form-item :label="t('siteId')" prop="site_id">
                    <el-input v-model="formData.site_id" clearable :placeholder="t('siteIdPlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('brand')" prop="brand">
                    <el-input v-model="formData.brand" clearable :placeholder="t('brandPlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('printerName')" prop="printer_name">
                    <el-input v-model="formData.printer_name" clearable :placeholder="t('printerNamePlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('printerCode')" prop="printer_code">
                    <el-input v-model="formData.printer_code" clearable :placeholder="t('printerCodePlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('printerKey')" prop="printer_key">
                    <el-input v-model="formData.printer_key" clearable :placeholder="t('printerKeyPlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('openId')" prop="open_id">
                    <el-input v-model="formData.open_id" clearable :placeholder="t('openIdPlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('apikey')" prop="apikey">
                    <el-input v-model="formData.apikey" clearable :placeholder="t('apikeyPlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('printNum')" prop="print_num">
                    <el-input v-model="formData.print_num" clearable :placeholder="t('printNumPlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('value')" >
                    <el-input v-model="formData.value" clearable :placeholder="t('valuePlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('printWidth')" prop="print_width">
                    <el-input v-model="formData.print_width" clearable :placeholder="t('printWidthPlaceholder')" class="input-width" />
                </el-form-item>
                
                <el-form-item :label="t('status')" prop="status">
                    <el-input v-model="formData.status" clearable :placeholder="t('statusPlaceholder')" class="input-width" />
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
import { ref, reactive, computed, watch } from 'vue'
import { useDictionary } from '@/app/api/dict'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { addPrinter, editPrinter, getPrinterInfo } from '@/app/api/printer'

let showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    printer_id: '',
    brand: '',
    printer_name: '',
    printer_code: '',
    printer_key: '',
    open_id: '',
    apikey: '',
    print_num: '',
    value: '',
    print_width: '',
    status: '',

}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
    brand: [
        { required: true, message: t('brandPlaceholder'), trigger: 'blur' },
        
    ]
,
    printer_name: [
        { required: true, message: t('printerNamePlaceholder'), trigger: 'blur' },
        
    ]
,
    printer_code: [
        { required: true, message: t('printerCodePlaceholder'), trigger: 'blur' },
        
    ]
,
    printer_key: [
        { required: true, message: t('printerKeyPlaceholder'), trigger: 'blur' },
        
    ]
,
    open_id: [
        { required: true, message: t('openIdPlaceholder'), trigger: 'blur' },
        
    ]
,
    apikey: [
        { required: true, message: t('apikeyPlaceholder'), trigger: 'blur' },
        
    ]
,
    print_num: [
        { required: true, message: t('printNumPlaceholder'), trigger: 'blur' },
        
    ]
,
    value: [
        { required: true, message: t('valuePlaceholder'), trigger: 'blur' },
        
    ]
,
    print_width: [
        { required: true, message: t('printWidthPlaceholder'), trigger: 'blur' },
        
    ]
,
    status: [
        { required: true, message: t('statusPlaceholder'), trigger: 'blur' },
        
    ]
,
    }
})

const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    let save = formData.printer_id ? editPrinter : addPrinter

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            let data = formData

            save(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(err => {
                loading.value = false
            })
        }
    })
}

// 获取字典数据
    

    
const setFormData = async (row: any = null) => {
    Object.assign(formData, initialFormData)
    loading.value = true
    if(row){
        const data = await (await getPrinterInfo(row.printer_id)).data
        if (data) Object.keys(formData).forEach((key: string) => {
            if (data[key] != undefined) formData[key] = data[key]
        })
    }
    loading.value = false
}

// 验证手机号格式
const mobileVerify = (rule: any, value: any, callback: any) => {
    if (value && !/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error(t('generateMobile')))
    } else {
        callback()
    }
}

// 验证身份证号
const idCardVerify = (rule: any, value: any, callback: any) => {
    if (value && !/^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/.test(value)) {
        callback(new Error(t('generateIdCard')))
    } else {
        callback()
    }
}

// 验证邮箱号
const emailVerify = (rule: any, value: any, callback: any) => {
    if (value && !/\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/.test(value)) {
        callback(new Error(t('generateEmail')))
    } else {
        callback()
    }
}

// 验证请输入整数
const numberVerify = (rule: any, value: any, callback: any) => {
    if (!Number.isInteger(value)) {
        callback(new Error(t('generateNumber')))
    } else {
        callback()
    }
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
<style lang="scss">
.diy-dialog-wrap .el-form-item__label{
    height: auto  !important;
}
</style>
