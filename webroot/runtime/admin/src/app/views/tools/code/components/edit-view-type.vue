<template>
    <el-dialog v-model="showDialog" :title="title" width="480px" :before-close="beforeClose" :destroy-on-close="true">
        <el-form :model="formData" label-width="130px" ref="formRef" :rules="formRules" class="page-form">
            <el-form-item :label="t('selectType')" prop="type">
					<el-radio-group v-model="formData.select_type">
						<el-radio :label="1">{{ t('dictType' )}}</el-radio>
			
					</el-radio-group>
			</el-form-item>

            <el-form-item :label="t('dictType')"  v-if="formData.select_type == 1" prop="dict_type">
                <el-select class="input-width" :placeholder="t('dictTypePlaceholder')" v-model="formData.dict_type" filterable remote clearable>
                    <el-option :label="item.name" :value="item.key" v-for="item in dicList" :key="item.key" />
                </el-select>
            </el-form-item>
               
            <el-form-item :label="t('addons')" prop="addon" v-if="formData.select_type == 2">
                <el-select v-model="formData.addon" :placeholder="t('addonsPlaceholder')" class="input-width" @change="addonChange">
                    <el-option v-for="(item, index) in addonList" :label="item.title" :value="item.key" :key="index" />
                </el-select>
            </el-form-item>

            <el-form-item :label="t('associatedModel')" prop="model" v-if="formData.select_type == 2">
                <el-select :placeholder="t('associatedModelPlaceholder')" v-model="formData.model" class="input-width" filterable  @change="modelChange"> 
                    <el-option v-for="item in modelList" :label="item" :value="item" :key="item" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup async>
import { ref, computed, toRaw } from 'vue'
import { t } from '@/lang'
import { getDictAll } from '@/app/api/dict'
import type { FormInstance } from 'element-plus'
import { cloneDeep } from 'lodash-es'
const showDialog = ref(false)
const title = ref('')
/**
 * 表单数据
 */
const initialFormData = {
    select_type : 1,
    dict_type: "",
    addon: "",
    model: "",
    value_key: "",
    label_key: ""
}
const formData: Record<string, any> = ref({ ...initialFormData })

const formRef = ref<FormInstance>()
const dicList = ref<Array<any>>([])




// 表单验证规则
const formRules = computed(() => {
    return {
        dict_type: [
            {
            validator: (rule: any, value: any, callback: any) => {
                if (formData.value.select_type == 1 && formData.value.dict_type == '') {
                    callback(new Error(t('dictTypePlaceholder')))
                }else{
                        callback()
                    } 
            },
            trigger: 'blur'
        }
        ]

    }
})

const getDictAllFn = () => {
    getDictAll().then((res) => {
        dicList.value = res.data
    })
}
const emit = defineEmits(['complete'])
/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            emit('complete', toRaw(formData.value))
            showDialog.value = false
        }
    })
}

const setFormData = async (row: any = null) => {
    formData.value = cloneDeep(Object.assign(initialFormData, row))
    getDictAllFn()
    showDialog.value = true
}
const beforeClose = (next: any) => {
    formRef.value?.clearValidate()
    next()
}
defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
