<!-- eslint-disable vue/multi-word-component-names -->
<template>
	<div class="main-container">
		<el-card class="box-card !border-none" shadow="never">

			<div class="flex justify-between items-center">
				<div class="detail-head !m-0">
					<div class="left" @click="router.push('/shop/order/delivery')">
						<span class="iconfont iconxiangzuojiantou !text-xs"></span>
						<span class="ml-[1px]">{{ t('returnToPreviousPage') }}</span>
					</div>
					<span class="adorn">|</span>
					<span class="right">{{ pageName }}</span>
				</div>
				<el-button type="primary" @click="addEvent">
					{{ t('addCompany') }}
				</el-button>
			</div>

			<el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
				<el-form :inline="true" :model="companyTable.searchParam" ref="searchFormRef">
					<el-form-item :label="t('companyName')" prop="company_name">
						<el-input v-model="companyTable.searchParam.company_name"  :placeholder="t('companyNamePlaceholder')"/>
					</el-form-item>
					<el-form-item>
						<el-button type="primary" @click="loadCompanyList()">{{ t('search') }}</el-button>
						<el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
					</el-form-item>
				</el-form>
			</el-card>

			<div class="mt-[10px]">
				<el-table :data="companyTable.data" size="large" v-loading="companyTable.loading">
					<template #empty>
						<span>{{ !companyTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column prop="company_name" :label="t('companyName')" min-width="120"/>
					<el-table-column prop="logo" :label="t('logo')" min-width="120">
						<template #default="{ row }">
							<img v-if="row.logo" class="w-[50px] h-[50px]" :src="img(row.logo)"/>
						</template>
					</el-table-column>
					<el-table-column prop="url" :label="t('url')" min-width="120"/>
					<el-table-column prop="express_no" :label="t('expressNo')" min-width="120"/>
					<el-table-column prop="create_time" :label="t('createTime')" min-width="120"/>

					<el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
						<template #default="{ row }">
							<el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
							<el-button type="danger" link @click="deleteEvent(row.company_id)">{{ t('delete') }}
							</el-button>
						</template>
					</el-table-column>

				</el-table>
				<div class="mt-[16px] flex justify-end">
					<el-pagination v-model:current-page="companyTable.page" v-model:page-size="companyTable.limit"   layout="total, sizes, prev, pager, next, jumper" :total="companyTable.total"   @size-change="loadCompanyList()" @current-change="loadCompanyList"/>
				</div>
			</div>

			<company-edit ref="editCompanyDialog" @complete="loadCompanyList"></company-edit>
		</el-card>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getCompanyList, deleteCompany } from '@/addon/shop/api/delivery'
import { img } from '@/utils/common'
import { ElMessageBox, FormInstance } from 'element-plus'
import CompanyEdit from '@/addon/shop/views/delivery/components/company-edit.vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const companyTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        company_name: '',
        logo: '',
        url: '',
        create_time: '',
        modify_time: ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取物流公司列表
 */
const loadCompanyList = (page: number = 1) => {
    companyTable.loading = true
    companyTable.page = page

    getCompanyList({
        page: companyTable.page,
        limit: companyTable.limit,
        ...companyTable.searchParam
    }).then(res => {
        companyTable.loading = false
        companyTable.data = res.data.data
        companyTable.total = res.data.total
    }).catch(() => {
        companyTable.loading = false
    })
}
loadCompanyList()

const editCompanyDialog: Record<string, any> | null = ref(null)

/**
 * 添加物流公司
 */
const addEvent = () => {
    editCompanyDialog.value.setFormData()
    editCompanyDialog.value.showDialog = true
}

/**
 * 编辑物流公司
 * @param data
 */
const editEvent = (data: any) => {
    editCompanyDialog.value.setFormData(data)
    editCompanyDialog.value.showDialog = true
}

/**
 * 删除物流公司
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('companyDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteCompany(id).then(() => {
            loadCompanyList()
        }).catch(() => {
        })
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadCompanyList()
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
