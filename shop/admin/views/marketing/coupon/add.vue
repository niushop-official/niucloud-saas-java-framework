<!-- eslint-disable vue/multi-word-component-names -->
<template>
	<div class="main-container">
		<div class="detail-head">
			<div class="left" @click="router.push(`/shop/marketing/coupon/list`)">
				<span class="iconfont iconxiangzuojiantou !text-xs"></span>
				<span class="ml-[1px]">{{ t('returnToPreviousPage') }}</span>
			</div>
			<span class="adorn">|</span>
			<span class="right"> {{ t('addCoupon') }}</span>
		</div>

		<!-- 表单 -->
		<el-card class="box-card !border-none" shadow="never">
			<el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form">
				<!-- 优惠券信息： -->
				<!-- 优惠券名称： -->
				<el-form-item :label="t('title')" prop="title">
					<el-input v-model="formData.title" clearable :placeholder="t('titlePlaceholder')" class="input-width"
						:maxlength="20" />
				</el-form-item>

				<!-- 优惠券面额： -->
				<el-form-item :label="t('price')" prop="price">
					<el-input type="number" οninput="value=value.replace(/[^\d.]/g,'')" v-model="formData.price" clearable
						:placeholder="t('pricePlaceholder')" class="input-width" maxlength="60">
						<template #append>元</template>
					</el-input>
				</el-form-item>

				<!-- 优惠券类型 -->
				<el-form-item :label="t('type')" prop="type">
					<el-radio-group v-model="formData.type">
						<el-radio :label="1">通用券</el-radio>
						<el-radio :label="2" @click="categoryList">品类券</el-radio>
						<el-radio :label="3">商品券</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-show="formData.type == 2">
					<div>
						<el-cascader v-model="formData.goods_category_ids" :options="options" :props="props"
							placeholder="请选择商品分类" collapse-tags collapse-tags-tooltip clearable />
					</div>
				</el-form-item>

				<el-form-item v-show="formData.type == 3">
					<div>
						<el-form-item>
							<goods-select-popup ref="goodsSelectPopupRef" v-model="formData.goods_ids" :min="1" :max="99" />
						</el-form-item>
					</div>
				</el-form-item>

				<!-- 使用门槛 -->
				<el-form-item :label="t('threshold')">
					<el-radio-group v-model="formData.threshold">
						<el-radio :label="1">{{ t('reduction') }}</el-radio>
						<el-radio :label="2">{{ t('noThreshold') }}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-show="formData.threshold == 1">
					最低满
					<div class="flex items-center" type="number" οninput="value=value.replace(/[^\d.]/g,'')"
						style="padding-left: 5px;">
						<el-input type="number" v-model="formData.min_condition_money" clearable class="!w-[100px]" />
					</div>
					元可用
				</el-form-item>

				<!-- 使用时间 -->
				<el-form-item :label="t('validType')">
					<el-radio-group v-model="formData.valid_type">
						<el-radio :label="1">{{ t('days') }}</el-radio>
						<el-radio :label="2">{{ t('times') }}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-show="formData.valid_type == 1">
					领劵后立即生效，有效期
					<div class="flex items-center" style="padding-left: 5px;">
						<el-input type="number" v-model="formData.length" clearable class="!w-[100px]" />
						天
					</div>
				</el-form-item>

				<el-form-item prop="valid_time" v-show="formData.valid_type == 2">
					领劵后立即生效，使用时间截止至
					<div class="w-[220px]" style="padding-left: 5px;">
						<el-date-picker v-model="formData.valid_time" type="datetime" />
					</div>
				</el-form-item>

				<!--  活动信息  -->
				<!-- 领取方式： -->
				<el-form-item :label="t('receiveType')">
					<div>
						<el-radio-group v-model="formData.receive_type">
							<el-radio :label="1">{{ t('user') }}</el-radio>
							<el-radio :label="2">{{ t('grant') }}</el-radio>
						</el-radio-group>
					</div>
					<div class="form-tip">开启手动领取后，会员可以直接在优惠券列表以及优惠券推广中直接领取</div>
				</el-form-item>

				<!--  领取时间  -->
				<el-form-item :label="t('receiveTime')" v-show="formData.receive_type == 1">
					<el-radio-group v-model="formData.receive_type_time">
						<el-radio :label="1">{{ t('limitedTime') }}</el-radio>
						<el-radio :label="2">{{ t('unlimitedTime') }}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item prop="receive_time" v-show="formData.receive_type_time == 1 && formData.receive_type == 1">
					<div class="w-[180px]">
						<el-date-picker v-model="formData.receive_time" type="datetimerange" range-separator="至"
							start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
					</div>
				</el-form-item>

				<!--  优惠券数量  -->
				<el-form-item :label="t('receiveNumber')" v-show="formData.receive_type == 1">
					<el-radio-group v-model="formData.limit">
						<el-radio :label="1">{{ t('limit') }}</el-radio>
						<el-radio :label="2">{{ t('unlimited') }}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-show="formData.limit == 1 && formData.receive_type == 1" prop="remain_count">
					<div>
						<el-input type="number" onkeypress='return( /[\d]/.test(String.fromCharCode(event.keyCode) ) )'
							v-model="formData.remain_count" clearable :placeholder="t('remainCountPlaceholder')"
							class="input-width" :min="1" :max="100000" :controls="false">
							<template #append>张</template>
						</el-input>
					</div>
					<div class="form-tip">最多发放100000张</div>
				</el-form-item>

				<!-- 用户可领取数量 -->
				<el-form-item :label="t('userLimitCount')" prop="limit_count" v-show="formData.receive_type == 1">
					<el-input type="number" onkeypress='return( /[\d]/.test(String.fromCharCode(event.keyCode) ) )'
						v-model="formData.limit_count" clearable :placeholder="t('userLimitCountPlaceholder')"
						class="input-width" :min="1" :max="100000">
						<template #append>张</template>
					</el-input>
				</el-form-item>

			</el-form>
		</el-card>
		<!-- 提交按钮 -->
		<div class="fixed-footer-wrap">
			<div class="fixed-footer">
				<el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
				<el-button @click="back()">{{ t('cancel') }}</el-button>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { t } from '@/lang'
import { useRouter } from 'vue-router'
import { getGoodsCategoryList, addCoupon } from '@/addon/shop/api/marketing'
import type { FormInstance } from 'element-plus'
import goodsSelectPopup from '@/addon/shop/views/goods/components/goods-select-popup.vue'

// const route = useRoute()
const router = useRouter()
const loading = ref(false)
const goodsSelectPopupRef: any = ref(null)
const start = new Date()

const end = new Date()

end.setTime(end.getTime() + 3600 * 1000 * 2 * 360) // 设置结束默认时间为当前时间30天后

interface FormDataType {
    title: string
    price: string
    type: number
    limit: number
    receive_type: number
    remain_count: number
    threshold: number
    limit_count: number
    min_condition_money: number
    length: number
    goods_ids: any[]
    goods_category_ids: any[]
    receive_type_time: number
    valid_type: number
	receive_time: any
    valid_time: any
}

const formData = ref<FormDataType>({
    title: '', // 标题
    price: '', // 面值
    type: 1, // 优惠券类型 1通用优惠券 2商品品类优惠券 3商品优惠券
    limit: 2,
    receive_type: 1, // 领取方式
    remain_count: 1000, // 剩余数量
    threshold: 2, // 门槛
    limit_count: 1, // 单个用户领取数量
    // status: 1,  //状态 1 正常 2 未开启 3 已无效
    min_condition_money: 1, // 商品最低多少金额可用优惠券
    length: 30, // 有效期时长(天)
    goods_ids: [], // 关联商品id
    goods_category_ids: [], // 关联商品分类id
    receive_type_time: 2,
    valid_type: 1, // 有效方式，1=时长，2=范围
    receive_time: [start, end],
    valid_time: end
})

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        title: [
            { required: true, message: t('titlePlaceholder'), trigger: 'blur' }
        ],

        price: [
            { required: true, validator: priceRule, trigger: 'blur' }
        ],
        remain_count: [
            { required: true, validator: remainCount, trigger: 'blur' }
        ],
        limit_count: [
            { required: true, validator: limitCountRule, trigger: 'blur' }
        ]
    }
})

const remainCount = (rule: any, value: any, callback: any) => {
    if (formData.value.remain_count != '' && formData.value.remain_count > 100000) {
        callback(new Error(t('remainCountPlaceholder')))
    }
    callback()
}
const priceRule = (rule: any, value: any, callback: any) => {
    if (formData.value.price != '' && formData.value.price < 0.01) {
        callback(new Error(t('pricePlaceholder')))
    }
    callback()
}

const limitCountRule = (rule: any, value: any, callback: any) => {
    if (formData.value.limit_count != '' && formData.value.limit_count < 1) {
        callback(new Error(t('userLimitCountPlaceholder')))
    }
    callback()
}

// 发布数量
// const setBoundsTo = ref(true)
// 优惠券类型
// const type = ref(1)
const props = { multiple: true }
// const categoryData = ref([])
const options = ref([])
const categoryList = () => {
    getGoodsCategoryList({}).then((res) => {
        options.value = res.data.goods_category_tree
    })
}
const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = formData.value
            const save = addCoupon
            save(data).then(res => {
                loading.value = false
                history.back()
            // eslint-disable-next-line n/handle-callback-err
            }).catch(err => {
                loading.value = false
            })
        }
    })
}

const back = () => {
    history.back()
}

</script>
<style lang="scss">
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
	-webkit-appearance: none !important;
	-moz-appearance: none !important;
	-o-appearance: none !important;
	-ms-appearance: none !important;
	appearance: none !important;
	margin: 0;
}

input[type="number"] {
	-webkit-appearance: textfield;
	-moz-appearance: textfield;
	-o-appearance: textfield;
	-ms-appearance: textfield;
	appearance: textfield;
}
</style>
