<template>
	<!-- 内容 -->
	<div class="content-wrap" v-show="diyStore.editTab == 'content'">
		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('selectStyle') }}</h3>
			<div class="flex items-center mb-[18px] rounded overflow-hidden">
				<span
					class="iconfont icongudingzhanshi border-[1px] border-solid border-[#eee] cursor-pointer flex-1 flex items-center justify-center py-[5px]"
					:class="{ 'border-[var(--el-color-primary)] text-[var(--el-color-primary)]': diyStore.editComponent.style == 'style1' }"
					@click="diyStore.editComponent.style = 'style1'"></span>
				<span
					class="iconfont icontuwendaohang3 border-[1px] border-solid border-[#eee] cursor-pointer flex-1 flex items-center justify-center py-[5px]"
					:class="{ 'border-[var(--el-color-primary)] text-[var(--el-color-primary)]': diyStore.editComponent.style == 'style2' }"
					@click="diyStore.editComponent.style = 'style2'"></span>
			</div>
		</div>
		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t("selectSource") }}</h3>
			<el-form label-width="80px" class="px-[10px]">
				<el-form-item :label="t('goodsSelectPopupSelectGoodsButton')">
					<el-radio-group v-model="diyStore.editComponent.source" :title="t('goodsSelectPopupSelectGoodsButton')" class="mb-[18px]">
						<el-radio label="all">{{ t('goodsSelectPopupAllGoods') }}</el-radio>
						<el-radio label="category">{{ t('selectCategory') }}</el-radio>
						<el-radio label="custom">{{ t('manualSelectionSources') }}</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item :label="t('goodsNum')" v-if="diyStore.editComponent.source == 'all'">
					<div class="flex items-center w-full ml-[5px]">
						<el-slider class="flex-1" v-model="diyStore.editComponent.num" max="20" size="small" />
						<span class="ml-[15px]">{{ diyStore.editComponent.num }}</span>
					</div>
				</el-form-item>
				<el-form-item :label="t('selectCategory')" v-if="diyStore.editComponent.source == 'category'">
					<div class="flex items-center w-full">
						<div class="cursor-pointer ml-auto" @click="categoryShowDialogOpen">
							<span class="text-[var(--el-color-primary)]">{{ currCategoryName }}</span>
							<span class="iconfont iconxiangyoujiantou"></span>
						</div>
					</div>
				</el-form-item>
				<el-form-item :label="t('customGoods')" v-if="diyStore.editComponent.source == 'custom'">
					<goods-select-popup ref="goodsSelectPopupRef" v-model="diyStore.editComponent.goods_ids" :min="1" :max="99" />
				</el-form-item>
			</el-form>

			<el-dialog v-model="categoryShowDialog" :title="t('goodsCategoryTitle')" width="1000px" :close-on-press-escape="false" :destroy-on-close="true" :close-on-click-modal="false">
				<el-table :data="categoryTable.data" ref="categoryTableRef" size="large" v-loading="categoryTable.loading"
					height="490px" @selection-change="handleSelectionChange" row-key="category_id"
					:tree-props="{ hasChildren: 'hasChildren', children: 'child_list' }">
					<template #empty>
						<span>{{ !categoryTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column type="selection" width="55" />
					<el-table-column :label="t('categoryName')" min-width="120">
						<template #default="{ row }">
							<span class="order-2">{{ row.category_name }}</span>
						</template>
					</el-table-column>
					<el-table-column :label="t('categoryImage')" width="170" align="left">
						<template #default="{ row }">
							<div class="h-[30px]">
								<el-image class="w-[30px] h-[30px] " :src="img(row.image)" fit="contain">
									<template #error>
										<div class="image-slot">
											<img class="w-[30px] h-[30px]" src="@/addon/shop/assets/category_default.png" />
										</div>
									</template>
								</el-image>
							</div>
						</template>
					</el-table-column>
				</el-table>
				<div class="flex items-center justify-end mt-[15px]">
					<el-button type="primary" @click="saveCategoryId">{{ t('confirm') }}</el-button>
					<el-button @click="categoryShowDialog = false">{{ t('cancel') }}</el-button>
				</div>
			</el-dialog>

		</div>
	</div>

	<!-- 样式 -->
	<div class="style-wrap" v-show="diyStore.editTab == 'style'">
		<!-- 组件样式 -->
		<slot name="style"></slot>
	</div>
</template>

<script lang="ts" setup>
import { getCategoryTree } from '@/addon/shop/api/goods'
import { t } from '@/lang'
import { img } from '@/utils/common'
import useDiyStore from '@/stores/modules/diy'
import { ref, reactive, onMounted } from 'vue'
import { ElTable } from 'element-plus'
import goodsSelectPopup from '@/addon/shop/views/goods/components/goods-select-popup.vue'

const diyStore:any = useDiyStore()
diyStore.editComponent.ignore = [] // 忽略公共属性

const categoryShowDialog = ref(false)

const categoryTable = reactive({
    loading: true,
    data: [],
    searchParam: {
        category_name: ''
    }
})
onMounted(() => {
    loadCategoryList()
})

const categoryTableRef = ref<InstanceType<typeof ElTable>>()
/**
 * 获取商品分类列表
 */
let currCategoryData: string | null = null
const loadCategoryList = () => {
    categoryTable.loading = true

    getCategoryTree({
        ...categoryTable.searchParam
    }).then(res => {
        categoryTable.loading = false
        categoryTable.data = res.data
        if (diyStore.editComponent.source == 'category') {
            categoryTable.data.forEach((item:any, index) => {
                if (diyStore.editComponent.goods_category == item.category_id) {
                    currCategoryName.value = item.category_name
                    currCategoryData = item
                }
            })
        }
    }).catch(() => {
        categoryTable.loading = false
    })
}

// 选择商品分类
let currCategory:any = {}
const currCategoryName = ref('请选择')
const handleSelectionChange = (val: string | any[]) => {
    let data = ''
    if (val) data = val[val.length - 1]
    if (val.length > 1) categoryTableRef.value!.clearSelection()
    if (data) categoryTableRef.value!.toggleRowSelection(data, true)
    currCategoryData = data
    currCategory = data
}

const saveCategoryId = () => {
    diyStore.editComponent.goods_category = currCategory.category_id
    currCategoryName.value = currCategory.category_name

    categoryShowDialog.value = false
}

const categoryShowDialogOpen = () => {
    categoryShowDialog.value = true
    if (currCategoryData) {
        setTimeout(() => {
			categoryTableRef.value!.toggleRowSelection(currCategoryData, true)
        }, 200)
    }
}

defineExpose({})

</script>

<style lang="scss" scoped></style>
