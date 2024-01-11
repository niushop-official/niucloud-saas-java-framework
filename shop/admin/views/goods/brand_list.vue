<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-[20px]">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addBrand') }}
                </el-button>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="brandTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('brandName')" prop="brand_name">
                        <el-input v-model="brandTable.searchParam.brand_name" :placeholder="t('brandNamePlaceholder')" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadBrandList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="brandTable.data" size="large" v-loading="brandTable.loading">
                    <template #empty>
                        <span>{{ !brandTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="brand_name" :label="t('brandName')" min-width="120" />
                    <el-table-column :label="t('logo')" min-width="120">
                        <template #default="{ row }">
                            <div class="h-[30px]">
                                <el-image class="w-[30px] h-[30px] " :src="img(row.logo)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[30px] h-[30px]" src="@/addon/shop/assets/brand_default.png" />
                                        </div>
                                    </template>
                                </el-image>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort" :label="t('sort')" min-width="120" />

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row.brand_id)">{{ t('delete') }}
                            </el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="brandTable.page" v-model:page-size="brandTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="brandTable.total"
                        @size-change="loadBrandList()" @current-change="loadBrandList" />
                </div>
            </div>

            <brand-edit ref="editBrandDialog" @complete="loadBrandList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getBrandPageList, deleteBrand } from '@/addon/shop/api/goods'
import { img } from '@/utils/common'
import { ElMessageBox, FormInstance } from 'element-plus'
import BrandEdit from '@/addon/shop/views/goods/components/brand-edit.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const brandTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        brand_name: ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取商品品牌列表
 */
const loadBrandList = (page: number = 1) => {
    brandTable.loading = true
    brandTable.page = page

    getBrandPageList({
        page: brandTable.page,
        limit: brandTable.limit,
        ...brandTable.searchParam
    }).then(res => {
        brandTable.loading = false
        brandTable.data = res.data.data
        brandTable.total = res.data.total
    }).catch(() => {
        brandTable.loading = false
    })
}
loadBrandList()

const editBrandDialog: Record<string, any> | null = ref(null)

/**
 * 添加商品品牌
 */
const addEvent = () => {
    editBrandDialog.value.setFormData()
    editBrandDialog.value.showDialog = true
}

/**
 * 编辑商品品牌
 * @param data
 */
const editEvent = (data: any) => {
    editBrandDialog.value.setFormData(data)
    editBrandDialog.value.showDialog = true
}

/**
 * 删除商品品牌
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('brandDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteBrand(id).then(() => {
            loadBrandList()
        }).catch(() => {
        })
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadBrandList()
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
