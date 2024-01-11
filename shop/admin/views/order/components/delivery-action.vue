<template>
    <el-dialog v-model="showDialog" :title="t('delivery')" width="700px" class="diy-dialog-wrap" :destroy-on-close="true">
        <div v-loading="loading">
            <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules" class="page-form mb-[30px]">
                <el-form-item :label="t('deliveryType')" prop="delivery_type">
                    <el-radio-group v-model="formData.delivery_type" @change="deliveryChange">
                        <el-radio :label="index" v-for="(item, index) in deliveryType" :key="index">{{ item }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('company')" v-if="formData.delivery_type == 'express'" prop="express_company_id">
                    <el-select v-model="formData.express_company_id" :placeholder="t('companyPlaceholder')"
                        class="input-width">
                        <el-option v-for="(item) in companyData" :key="item.index" :label="item.company_name"
                            :value="item.company_id" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('expressNumber')" v-if="formData.delivery_type == 'express'" prop="express_number">
                    <el-input v-model="formData.express_number" clearable :placeholder="t('expressNumberPlaceholder')"
                        class="input-width" />
                </el-form-item>
            </el-form>
            <el-table :data="goodsDataArr" size="large" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" :selectable="selectable" />
                <el-table-column prop="goods_name" :label="t('goodsName')" min-width="200" />
                <el-table-column prop="num" :label="t('num')" min-width="80" />
                <el-table-column prop="status_name" :label="t('refundStatusName')" min-width="120" />
                <el-table-column prop="delivery_status_name" :label="t('deliveryStatusName')" min-width="120"
                    align="right" />
            </el-table>
        </div>
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
import { FormInstance, ElMessage } from 'element-plus'
import { getCompanyList } from '@/addon/shop/api/delivery'
import { getOrderDeliveryType, orderDelivery } from '@/addon/shop/api/order'
import { cloneDeep } from 'lodash-es'

const showDialog = ref(false)
const loading = ref(false)
interface companyDataType{
    company_id: number,
    company_name: string,
    index: number
}
const companyData = ref<companyDataType[]>([])
const isHasVirtual = ref(false)
const deliveryType = ref([])

getCompanyList({}).then((data) => {
    companyData.value = data.data.data
})

/**
 * 表单数据
 */
const goodsData = ref([])
const initialFormData = {
    order_id: 0,
    delivery_type: '',
    express_company_id: '',
    express_number: '',
    order_goods_ids: []
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        delivery_type: [
            { required: true, message: t('deliveryTypePlaceholder'), trigger: 'blur' }
        ],
        express_company_id: [
            { required: true, validator: companyPass, trigger: 'blur' }
        ],
        express_number: [
            { required: true, validator: expressNumberPass, trigger: 'blur' }
        ]
    }
})

const companyPass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'express' && value === '') {
        callback(new Error(t('companyPlaceholder')))
    } else {
        callback()
    }
}

const expressNumberPass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'express' && value === '') {
        callback(new Error(t('expressNumberPlaceholder')))
    } else {
        callback()
    }
}

const selectable = (row:any, index:number) => {
    if (row.status == 2) {
        return false
    }
    return true
}
interface goodsDataType{
    goods_type:string
}
const goodsDataArr = ref<goodsDataType[]>([])
const deliveryChange = () => {
    goodsDataArr.value = cloneDeep(goodsData.value)
    if (formData.delivery_type && formData.delivery_type == 'virtual') {
        for (const k in goodsDataArr.value) {
            if (goodsDataArr.value[k].goods_type != 'virtual') {
                goodsDataArr.value.splice(k, 1)
            }
        }
    } else if (formData.delivery_type && formData.delivery_type != 'virtual') {
        for (const k in goodsDataArr.value) {
            if (goodsDataArr.value[k].goods_type == 'virtual') {
                goodsDataArr.value.splice(k, 1)
            }
        }
    }
}

const handleSelectionChange = (val:any) => {
    formData.order_goods_ids = cloneDeep([])
    for (const v in val) {
        formData.order_goods_ids.push(val[v].order_goods_id)
    }
}

const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    if (formData.order_goods_ids.length <= 0) {
        ElMessage({
            message: t('orderGoodsPlaceholder'),
            type: 'warning'
        })
        return
    }

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = formData
            orderDelivery(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            // eslint-disable-next-line n/handle-callback-err
            }).catch(err => {
                loading.value = false
            })
        }
    })
}

const setFormData = async (row: any = null) => {
    loading.value = true
    if (row) {
        formData.order_id = row.order_id
        formData.delivery_type = ''
        goodsData.value = row.order_goods
        goodsDataArr.value = row.order_goods
        await getOrderDeliveryType({
            delivery_type: row.delivery_type
        }).then((data) => {
            deliveryType.value = data.data
            // eslint-disable-next-line no-unreachable-loop
            for (const v in data.data) {
                formData.delivery_type = v
                break
            }
            deliveryChange()
        })

        for (let i = 0; i < row.order_goods.length; i++) {
            if (row.order_goods[i].goods_type == 'virtual') {
                isHasVirtual.value = true
                break
            }
        }
        if (isHasVirtual.value == true) {
            Object.assign(deliveryType.value, { virtual: t('virtualDelivery') })
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
