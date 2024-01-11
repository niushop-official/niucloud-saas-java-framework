<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-[20px]">{{pageName}}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addLabel') }}
                </el-button>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="labelTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('labelName')" prop="label_name">
                        <el-input v-model="labelTable.searchParam.label_name" :placeholder="t('labelNamePlaceholder')" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadLabelList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="labelTable.data" size="large" v-loading="labelTable.loading">
                    <template #empty>
                        <span>{{ !labelTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="label_name" :label="t('labelName')" min-width="120" />
                    <el-table-column prop="memo" :label="t('memo')" min-width="200" />
                    <el-table-column prop="sort" :label="t('sort')" min-width="120" />

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                       <template #default="{ row }">
                           <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                           <el-button type="primary" link @click="deleteEvent(row.label_id)">{{ t('delete') }}</el-button>
                       </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="labelTable.page" v-model:page-size="labelTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="labelTable.total"
                        @size-change="loadLabelList()" @current-change="loadLabelList" />
                </div>
            </div>

            <label-edit ref="editLabelDialog" @complete="loadLabelList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getLabelPageList, deleteLabel } from '@/addon/shop/api/goods'
import { ElMessageBox, FormInstance } from 'element-plus'
import LabelEdit from '@/addon/shop/views/goods/components/label-edit.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const labelTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        label_name: ''
    }
})

const searchFormRef = ref<FormInstance>()

// 选中数据
// const selectData = ref<any[]>([])

/**
 * 获取商品标签列表
 */
const loadLabelList = (page: number = 1) => {
    labelTable.loading = true
    labelTable.page = page

    getLabelPageList({
        page: labelTable.page,
        limit: labelTable.limit,
        ...labelTable.searchParam
    }).then(res => {
        labelTable.loading = false
        labelTable.data = res.data.data
        labelTable.total = res.data.total
    }).catch(() => {
        labelTable.loading = false
    })
}
loadLabelList()

const editLabelDialog: Record<string, any> | null = ref(null)

/**
 * 添加商品标签
 */
const addEvent = () => {
    editLabelDialog.value.setFormData()
    editLabelDialog.value.showDialog = true
}

/**
 * 编辑商品标签
 * @param data
 */
const editEvent = (data: any) => {
    editLabelDialog.value.setFormData(data)
    editLabelDialog.value.showDialog = true
}

/**
 * 删除商品标签
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('labelDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteLabel(id).then(() => {
            loadLabelList()
        }).catch(() => {
        })
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadLabelList()
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
