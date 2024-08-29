<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center mb-[5px]">
                <span class="text-lg">{{pageName}}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addPrinter') }}
                </el-button>
            </div>

            <el-tabs class="demo-tabs" model-value="/printer/list" @tab-change="handleClick">
                <el-tab-pane :label="t('打印机管理')" name="/printer/list" />
                <el-tab-pane :label="t('打印模板')" name="/printer/template/list" />
            </el-tabs>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="printerTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('siteId')" prop="site_id">
                        <el-input v-model="printerTable.searchParam.site_id" :placeholder="t('siteIdPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('brand')" prop="brand">
                        <el-input v-model="printerTable.searchParam.brand" :placeholder="t('brandPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('printerName')" prop="printer_name">
                        <el-input v-model="printerTable.searchParam.printer_name" :placeholder="t('printerNamePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('printerCode')" prop="printer_code">
                        <el-input v-model="printerTable.searchParam.printer_code" :placeholder="t('printerCodePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('printerKey')" prop="printer_key">
                        <el-input v-model="printerTable.searchParam.printer_key" :placeholder="t('printerKeyPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('openId')" prop="open_id">
                        <el-input v-model="printerTable.searchParam.open_id" :placeholder="t('openIdPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('apikey')" prop="apikey">
                        <el-input v-model="printerTable.searchParam.apikey" :placeholder="t('apikeyPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('printNum')" prop="print_num">
                        <el-input v-model="printerTable.searchParam.print_num" :placeholder="t('printNumPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('value')" prop="value">
                        <el-input v-model="printerTable.searchParam.value" :placeholder="t('valuePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('printWidth')" prop="print_width">
                        <el-input v-model="printerTable.searchParam.print_width" :placeholder="t('printWidthPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('status')" prop="status">
                        <el-input v-model="printerTable.searchParam.status" :placeholder="t('statusPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-input v-model="printerTable.searchParam.create_time" :placeholder="t('createTimePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('updateTime')" prop="update_time">
                        <el-input v-model="printerTable.searchParam.update_time" :placeholder="t('updateTimePlaceholder')" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadPrinterList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="printerTable.data" size="large" v-loading="printerTable.loading">
                    <template #empty>
                        <span>{{ !printerTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="printer_id" :label="t('printerId')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="brand" :label="t('brand')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="printer_name" :label="t('printerName')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="printer_code" :label="t('printerCode')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="printer_key" :label="t('printerKey')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="open_id" :label="t('openId')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="apikey" :label="t('apikey')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="print_num" :label="t('printNum')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="value" :label="t('value')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="print_width" :label="t('printWidth')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="status" :label="t('status')" min-width="120" :show-overflow-tooltip="true"/>
                    

                    <el-table-column :label="t('operation')" fixed="right" min-width="120">
                       <template #default="{ row }">
                           <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                           <el-button type="primary" link @click="deleteEvent(row.printer_id)">{{ t('delete') }}</el-button>
                       </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="printerTable.page" v-model:page-size="printerTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="printerTable.total"
                        @size-change="loadPrinterList()" @current-change="loadPrinterList" />
                </div>
            </div>

            <edit ref="editPrinterDialog" @complete="loadPrinterList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { t } from '@/lang'
import { useDictionary } from '@/app/api/dict'
import { getPrinterList, deletePrinter } from '@/app/api/printer'
import { img } from '@/utils/common'
import { ElMessageBox,FormInstance } from 'element-plus'
import Edit from '@/app/views/printer/components/printer-edit.vue'
import { useRoute,useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title;

const handleClick = (path: string) => {
    router.push({ path })
}

let printerTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam:{
      "site_id":"",
      "brand":"",
      "printer_name":"",
      "printer_code":"",
      "printer_key":"",
      "open_id":"",
      "apikey":"",
      "print_num":"",
      "value":"",
      "print_width":"",
      "status":"",
      "create_time":"",
      "update_time":""
    }
})

const searchFormRef = ref<FormInstance>()

// 选中数据
const selectData = ref<any[]>([])

// 字典数据
    

/**
 * 获取小票打印机列表
 */
const loadPrinterList = (page: number = 1) => {
    printerTable.loading = true
    printerTable.page = page

    getPrinterList({
        page: printerTable.page,
        limit: printerTable.limit,
         ...printerTable.searchParam
    }).then(res => {
        printerTable.loading = false
        printerTable.data = res.data.data
        printerTable.total = res.data.total
    }).catch(() => {
        printerTable.loading = false
    })
}
loadPrinterList()

const editPrinterDialog: Record<string, any> | null = ref(null)

/**
 * 添加小票打印机
 */
const addEvent = () => {
    editPrinterDialog.value.setFormData()
    editPrinterDialog.value.showDialog = true
}

/**
 * 编辑小票打印机
 * @param data
 */
const editEvent = (data: any) => {
    editPrinterDialog.value.setFormData(data)
    editPrinterDialog.value.showDialog = true
}

/**
 * 删除小票打印机
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('printerDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning',
        }
    ).then(() => {
        deletePrinter(id).then(() => {
            loadPrinterList()
        }).catch(() => {
        })
    })
}

    

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadPrinterList()
}
</script>

<style lang="scss" scoped>
/* 多行超出隐藏 */
.multi-hidden {
		word-break: break-all;
		text-overflow: ellipsis;
		overflow: hidden;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}
</style>
