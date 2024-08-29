<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center mb-[5px]">
                <span class="text-lg">{{pageName}}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addPrinterTemplate') }}
                </el-button>
            </div>

            <el-tabs class="demo-tabs" model-value="/printer/template/list" @tab-change="handleClick">
                <el-tab-pane :label="t('打印机管理')" name="/printer/list" />
                <el-tab-pane :label="t('打印模板')" name="/printer/template/list" />
            </el-tabs>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="printerTemplateTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('siteId')" prop="site_id">
                        <el-input v-model="printerTemplateTable.searchParam.site_id" :placeholder="t('siteIdPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('templateType')" prop="template_type">
                        <el-input v-model="printerTemplateTable.searchParam.template_type" :placeholder="t('templateTypePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('templateName')" prop="template_name">
                        <el-input v-model="printerTemplateTable.searchParam.template_name" :placeholder="t('templateNamePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('value')" prop="value">
                        <el-input v-model="printerTemplateTable.searchParam.value" :placeholder="t('valuePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-input v-model="printerTemplateTable.searchParam.create_time" :placeholder="t('createTimePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('updateTime')" prop="update_time">
                        <el-input v-model="printerTemplateTable.searchParam.update_time" :placeholder="t('updateTimePlaceholder')" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadPrinterTemplateList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="printerTemplateTable.data" size="large" v-loading="printerTemplateTable.loading">
                    <template #empty>
                        <span>{{ !printerTemplateTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="template_id" :label="t('templateId')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="template_type" :label="t('templateType')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="template_name" :label="t('templateName')" min-width="120" :show-overflow-tooltip="true"/>
                    
                    <el-table-column prop="value" :label="t('value')" min-width="120" :show-overflow-tooltip="true"/>
                    

                    <el-table-column :label="t('operation')" fixed="right" min-width="120">
                       <template #default="{ row }">
                           <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                           <el-button type="primary" link @click="deleteEvent(row.template_id)">{{ t('delete') }}</el-button>
                       </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="printerTemplateTable.page" v-model:page-size="printerTemplateTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="printerTemplateTable.total"
                        @size-change="loadPrinterTemplateList()" @current-change="loadPrinterTemplateList" />
                </div>
            </div>

            <edit ref="editPrinterTemplateDialog" @complete="loadPrinterTemplateList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { t } from '@/lang'
import { useDictionary } from '@/app/api/dict'
import { getPrinterTemplateList, deletePrinterTemplate } from '@/app/api/printer'
import { img } from '@/utils/common'
import { ElMessageBox,FormInstance } from 'element-plus'
import Edit from '@/app/views/printer/components/printer-template-edit.vue'
import { useRoute,useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title;

const handleClick = (path: string) => {
    router.push({ path })
}

let printerTemplateTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam:{
      "site_id":"",
      "template_type":"",
      "template_name":"",
      "value":"",
      "create_time":"",
      "update_time":""
    }
})

const searchFormRef = ref<FormInstance>()

// 选中数据
const selectData = ref<any[]>([])

// 字典数据
    

/**
 * 获取小票打印模板列表
 */
const loadPrinterTemplateList = (page: number = 1) => {
    printerTemplateTable.loading = true
    printerTemplateTable.page = page

    getPrinterTemplateList({
        page: printerTemplateTable.page,
        limit: printerTemplateTable.limit,
         ...printerTemplateTable.searchParam
    }).then(res => {
        printerTemplateTable.loading = false
        printerTemplateTable.data = res.data.data
        printerTemplateTable.total = res.data.total
    }).catch(() => {
        printerTemplateTable.loading = false
    })
}
loadPrinterTemplateList()

const editPrinterTemplateDialog: Record<string, any> | null = ref(null)

/**
 * 添加小票打印模板
 */
const addEvent = () => {
    editPrinterTemplateDialog.value.setFormData()
    editPrinterTemplateDialog.value.showDialog = true
}

/**
 * 编辑小票打印模板
 * @param data
 */
const editEvent = (data: any) => {
    editPrinterTemplateDialog.value.setFormData(data)
    editPrinterTemplateDialog.value.showDialog = true
}

/**
 * 删除小票打印模板
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('printerTemplateDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning',
        }
    ).then(() => {
        deletePrinterTemplate(id).then(() => {
            loadPrinterTemplateList()
        }).catch(() => {
        })
    })
}

    

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadPrinterTemplateList()
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
