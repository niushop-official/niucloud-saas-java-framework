<template>
    <div class="main-container">
      <el-card class="box-card !border-none" shadow="never">
        <div class="flex justify-between items-center">
            <span class="text-page-title">{{ pageName }}</span>
            <el-button type="primary" @click="handleChange">{{ t("addFullDiscountBonus") }}</el-button>
        </div>

        <!-- 搜索 -->
        <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
            <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef">
                <el-form-item :label="t('name')" prop="manjian_name">
                    <el-input v-model.trim="tableData.searchParam.manjian_name" :placeholder="t('namePlaceholder')" />
                </el-form-item>
                <el-form-item>
                <el-form-item :label="t('activityTime')" prop="create_time">
				    <el-date-picker v-model="tableData.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" />
			    </el-form-item>
                <el-button type="primary" @click="loadManjianList()">{{ t("search") }}</el-button>
                <el-button @click="resetForm(searchFormRef)">{{ t("reset") }}</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 列表 -->
        <div>
            <el-tabs v-model="tableData.searchParam.status" class="goods-tabs" @tab-click="tabHandleClick">
                <el-tab-pane :label="t('all')" name=""></el-tab-pane>
                <el-tab-pane v-for="(label, value) in statusList" :key="value" :label="label" :name="value"></el-tab-pane>
            </el-tabs>
            <el-table :data="tableData.data" size="large" v-loading="tableData.loading" ref="goodBankListTableRef">
                <el-table-column prop="manjian_name" :label="t('name')" min-width="130" />
                <el-table-column prop="status_name" :label="t('status')" min-width="130" />
                <el-table-column prop="total_order_num" :label="t('activeOrderNum')" min-width="130" />
                <el-table-column prop="total_order_money" :label="t('activeOrderMoney')" min-width="130" />
                <el-table-column prop="total_member_num" :label="t('activeMemberNum')" min-width="130" />
                <el-table-column :label="t('activityTime')"  min-width="150">
                    <template #default="{ row }">
                        <div>
                            <p>开始：{{ row.start_time }}</p>
                            <p>结束：{{ row.end_time }}</p>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                    <template #default="{ row }">
                        <el-button v-if="row.status!='-1'|| row.status!='2'" type="primary" link @click="editEvent(row)">{{t("edit")}}</el-button>
                        <el-button type="primary" link @click="detailEvent(row.manjian_id)">{{ t('detail') }}</el-button>
                        <el-button v-if="row.status=='1'" type="primary" link @click="closeEvent(row.manjian_id)">{{ t('close') }}</el-button>
                        <el-button v-if="row.status!='1'" type="primary" link @click="deleteEvent(row.manjian_id)">{{ t("delete") }}</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" @size-change="loadManjianList()" @current-change="loadManjianList" />
            </div>
        </div>
      </el-card>
      <manjian-detail ref="manjianDetailDialog"></manjian-detail>
    </div>
  </template>
<script lang="ts" setup>
// 添加满减送活动
import { ref, reactive } from "vue";
import {t} from "@/lang";
import { useRoute, useRouter } from "vue-router";
import {closeManjian, deleteManjian,getManjianList,getManjianStatusList} from "@/addon/shop/api/marketing";
import { FormInstance, ElMessageBox } from "element-plus";
import manjianDetail from '@/addon/shop/views/marketing/manjian/detail.vue'
import { setTablePageStorage,getTablePageStorage } from "@/utils/common";

const router = useRouter();
const route = useRoute();
const pageName = route.meta.title;
const searchFormRef = ref<FormInstance>();
// 表单内容
const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
    searchParam: {
        create_time: [],
        manjian_name: "",
        status: route.query.status || '',
    },
});

// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    tableData.searchParam.status = tab.props.name
    loadManjianList()
}

// 获取列表
const loadManjianList = (page: number = 1) => {
   tableData.loading = true;
   tableData.page = page;

    getManjianList({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam,
    }).then((res) => {
        tableData.loading = false;
        tableData.data = res.data.data;
        tableData.total = res.data.total;
        setTablePageStorage(tableData.page, tableData.limit, tableData.searchParam);
    }).catch(() => {
        tableData.loading = false;
    });
};

loadManjianList(getTablePageStorage(tableData.searchParam).page);

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    formEl.resetFields();
    loadManjianList();
};

const handleChange = () => {
    router.push('/shop/marketing/manjian/edit')
}

const statusList = ref<{ [key: string]: string }>({})
const getManjianStatusListFn = () => {
    getManjianStatusList().then((res) => {
        statusList.value= res.data;
    })
}

getManjianStatusListFn()
//编辑满减送活动
const editEvent = (data: any) => {
    router.push('/shop/marketing/manjian/edit?id='+data.manjian_id)
}

//详情
const manjianDetailDialog: Record<string, any> | null = ref(null)
const detailEvent=(id:number)=>{
    let data = {id: id};
    manjianDetailDialog.value.setFormData(data);
    manjianDetailDialog.value.showDialog = true;
}

//关闭
const closeEvent = (id:number)=>{
    ElMessageBox.confirm(t('closeTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        closeManjian(id).then(() => {
            loadManjianList()
        }).catch(() => {
        })
    })
}

// 删除
const deleteEvent = (id:number) => {
    ElMessageBox.confirm(t('deleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteManjian(id).then(() => {
            loadManjianList()
        }).catch(() => {
        })
    })
}
</script>

<style lang="scss" scoped>
</style>
