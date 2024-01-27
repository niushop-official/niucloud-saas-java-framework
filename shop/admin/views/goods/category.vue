<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-[20px]">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addCategory') }}
                </el-button>
            </div>
            <div class="mt-[10px]">
                <el-table :data="categoryTable.data" ref="tableRef" size="large" v-loading="categoryTable.loading"
                    row-key="category_id" :tree-props="{ hasChildren: 'hasChildren', children: 'child_list' }"
                    >
                    <template #empty>
                        <span>{{ !categoryTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column :label="t('categoryName')" min-width="120">
                        <template #default="{ row }">
                            <i class="order-0 iconfont icontuodong vues-rank mr-[8px]"></i>
                            <span class="order-2">{{ row.category_name }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('image')" width="170" align="left">
                        <template #default="{ row }">
                            <div class="h-[30px]">
                                <el-image class="w-[30px] h-[30px] " :src="img(row.image)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[30px] h-[30px]"
                                                src="@/addon/shop/assets/category_default.png" />
                                        </div>
                                    </template>
                                </el-image>
                            </div>

                        </template>
                    </el-table-column>
                    <el-table-column prop="is_show" :label="t('isShow')" width="400">
                        <template #default="{ row }">
                            <el-tag class="cursor-pointer" :type="row.is_show != 2 ? 'success' : 'danger'"
                                @click="showClick(row)">{{ row.is_show != 2 ? '是' : '否'
                                }}</el-tag>
                        </template>
                    </el-table-column>
                    <!-- <el-table-column prop="sort" :label="t('sort')" width="120" /> -->
                    <el-table-column :label="t('operation')" fixed="right" align="right" width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row)">{{ t('delete')
                            }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
            </div>

            <category-edit ref="editCategoryDialog" @complete="loadCategoryList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, nextTick } from 'vue'
import { t } from '@/lang'
import { getCategoryTree, deleteCategory, updateCategory, editCategory } from '@/addon/shop/api/goods'
import { img } from '@/utils/common'
import { ElMessageBox } from 'element-plus'
import categoryEdit from '@/addon/shop/views/goods/components/category-edit.vue'
import { useRoute } from 'vue-router'
import Sortable from 'sortablejs'
import { useTemplateRefsList } from '@vueuse/core'
import { cloneDeep } from 'lodash-es'

const route = useRoute()
const pageName = route.meta.title
const tableRef = useTemplateRefsList<HTMLElement>()
const categoryTable = reactive({
    loading: true,
    data: [],
    searchParam: {
        category_name: ''
    }
})

// const searchFormRef = ref<FormInstance>()
onMounted(() => {
    nextTick(() => {
        rowDrop()
    })
    loadCategoryList()
})

// 拖拽
const activeRows = ref<any[]>([])
// 拖拽排序
const rowDrop = () => {
    const tbody = tableRef.value.$el.querySelector(
        '.el-table__body-wrapper tbody'
    )
    Sortable.create(tbody, {
        handle: '.vues-rank',
        animation: 300,
        onMove: ({ dragged, related }) => {
            const oldRow = activeRows.value[dragged.rowIndex] // 移动的那个元素
            const newRow = activeRows.value[related.rowIndex] // 新的元素
            if (oldRow.pid !== newRow.pid) { // 移动的元素与新元素父级id不相同
                return false // 不允许跨级拖动
            }
        },
        onStart: () => { // 开始拖拽前把树形结构数据扁平化
            activeRows.value = treeToTile(cloneDeep(categoryTable.data)) // 把树形的结构转为列表再进行拖拽
        },
        onEnd: e => {
            const oldRow = activeRows.value[e.oldIndex] // 移动的那个元素
            const newRow = activeRows.value[e.newIndex] // 新的元素
            if (e.oldIndex === e.newIndex || oldRow.pid !== newRow.pid) return false
            const index = activeRows.value.indexOf(oldRow)
            if (index < 0) return false
            //   activeRows.value.splice(index, 1)
            //   activeRows.value.splice(index + changeIndex, 0, oldRow)
            const currRow = activeRows.value.splice(e.oldIndex, 1)[0]
            activeRows.value.splice(e.newIndex, 0, currRow)
            const pid = newRow.pid
            const currentRows = activeRows.value
                .filter(c => c.pid === pid)
                ?.map((item, index) => {
                    if (item.level === 1 && item.category_id === currRow.category_id) {
                        categoryTable.data = categoryTable.data.filter(c => c.category_id !== currRow.category_id)
                        categoryTable.data.splice(index, 0, currRow)
                    }
                    if (item.level === 2 && item.category_id === currRow.category_id) {
                        const treeIndex = categoryTable.data.findIndex(el => el.category_id === item.pid)
                        const obj = cloneDeep(categoryTable.data[treeIndex].child_list.filter(c => c.category_id !== currRow.category_id))
                        categoryTable.data[treeIndex].child_list = []
                        categoryTable.data[treeIndex].child_list.push(...obj)
                        categoryTable.data[treeIndex].child_list.splice(index, 0, currRow)
                        // obj.child_list//.splice(index,0,currRow)
                    }
                    return {
                        category_id: item.category_id, // 当前行的唯一标识
                        sort: 9999 - index
                    }
                })
            // 请求接口排序，根据后端要求填写参数
            // nextTick(() => {
                // rowDrop()
            // })
            updateCategoryFn({ category_sort_array: currentRows })
        }
    })
}
/**
  * 将树数据转化为平铺数据
  * @param <Array> treeData当前要转的id
  * @param <String> childKey 子级字段
  * @return <Array> 返回数据
  */
const treeToTile = (treeData:any, childKey = 'child_list') => {
    const arr:Array<any> = []
    const expanded = (data:any) => {
        if (data && data.length > 0) {
            data.filter((d:any) => d).forEach((e:any) => {
                arr.push(e)
                expanded(e[childKey] || [])
            })
        }
    }
    expanded(treeData)
    return arr
}
/**
 * 获取商品分类列表
 */
const loadCategoryList = () => {
    categoryTable.loading = true

    getCategoryTree({
        ...categoryTable.searchParam
    }).then(res => {
        categoryTable.loading = false
        categoryTable.data = res.data
    }).catch(() => {
        categoryTable.loading = false
    })
}
const updateCategoryFn = (params: any) => {
    updateCategory(params).then(res => {
        // getCategoryTree({
        //     page: categoryTable.page,
        //     limit: categoryTable.limit,
        //     ...categoryTable.searchParam
        // }).then(res => {
        //     categoryTable.data = res.data.data
        //     categoryTable.total = res.data.total
        // })
    })
}

const showClick = (row: any) => {
    row.is_show = row.is_show === 1 ? 2 : 1
    const obj = cloneDeep(row)
    delete obj.child_list
    editCategory(obj)
}
const editCategoryDialog: Record<string, any> | null = ref(null)

/**
 * 添加商品分类
 */
const addEvent = () => {
    editCategoryDialog.value.setFormData()
    editCategoryDialog.value.showDialog = true
}

/**
 * 编辑商品分类
 * @param data
 */
const editEvent = (data: any) => {
    editCategoryDialog.value.setFormData(data)
    editCategoryDialog.value.showDialog = true
}

/**
 * 删除商品分类
 */
const deleteEvent = (row: any) => {
    ElMessageBox.confirm(!row.child_list || !row.child_list.length ? t('categoryDeleteTips') : t('categoryDeleteTips1'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteCategory(row.category_id).then(() => {
            loadCategoryList()
        }).catch(() => {
        })
    })
}

// const resetForm = (formEl: FormInstance | undefined) => {
//     if (!formEl) return
//     formEl.resetFields()
//     loadCategoryList()
// }
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

:deep(.el-table__row) {
    >.el-table__cell:nth-child(1) {
        .cell {
            display: flex;
            align-items: center;

            .el-table__expand-icon,
            .el-table__indent,
            .el-table__placeholder {
                order: 1;
            }

        }

    }
}</style>
