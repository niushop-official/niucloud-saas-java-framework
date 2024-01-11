<template>
	<view class="bg-[#f8f8f8] min-h-screen overflow-hidden" v-if="!loading">
		<view class="pb-[200rpx]" v-if="type != 'logistics'">
			<view v-if="detail.status_name" class="flex status-item text-[32rpx] bg-primary h-[170rpx]">
				<view class="ml-[50rpx]">
					<img v-if="['1','2','4','6','7'].indexOf(detail.status) != -1" class="w-[70rpx] h-[70rpx] mt-[45rpx]" :src="img('addon/shop/payment.png')" />
					<img v-if="['8'].indexOf(detail.status) != -1" class="w-[70rpx] h-[70rpx] mt-[45rpx]" :src="img('addon/shop/complete.png')" />
					<img v-if="['3','5','-1'].indexOf(detail.status) != -1" class="w-[70rpx] h-[70rpx] mt-[45rpx]" :src="img('addon/shop/close.png')" />
				</view>
				<view class="ml-[20rpx] text-[#fff] mt-[50rpx] text-[40rpx]">
					{{ detail.status_name }}
				</view>
			</view>

			<view class="bg-[#fff] mx-[30rpx] p-[30rpx] rounded-[10rpx] flex justify-between flex-wrap mt-[30rpx]">
				<view class="w-[160rpx] h-[160rpx] flex-2" @click="goodsEvent(detail.order_goods.goods_id)">
					<u--image class="rounded-[10rpx] overflow-hidden" width="160rpx" height="160rpx"
						:src="img(detail.order_goods.goods_image_thumb_small ? detail.order_goods.goods_image_thumb_small : '')"
						model="aspectFill">
						<template #error>
							<u-icon name="photo" color="#999" size="50"></u-icon>
						</template>
					</u--image>
				</view>
				<view class="ml-[20rpx] flex flex-1 flex-col justify-between">
					<view>
						<text class="text-[28rpx] text-item leading-[40rpx]">{{ detail.order_goods.goods_name }}</text>
						<view class="flex" v-if="detail.order_goods.sku_name">
							<text
								class="block text-[20rpx] text-item mt-[10rpx] text-[#ccc] bg-[#f5f5f5] px-[16rpx] py-[6rpx] rounded-[999rpx]">{{
									detail.order_goods.sku_name }}</text>
						</view>
					</view>
					<view class="flex justify-between">
						<text class="text-right text-[28rpx]  text-[var(--price-text-color)] price-font">￥{{ detail.order_goods.price }}</text>
						<text class="text-right text-sm"><text class="text-[26rpx]">x</text>{{ detail.order_goods.num }}</text>
					</view>
				</view>
			</view>
			<view class="bg-[#fff] mx-[30rpx] p-[30rpx] mt-[30rpx] rounded-[10rpx]">
				<view
					class="flex justify-between text-[28rpx] pt-[20rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1]">
					<view>{{t('refundType')}}</view>
					<view>{{ detail.refund_type_name }}</view>
				</view>
				<view
					class="flex justify-between text-[28rpx] pt-[20rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1] mt-[40rpx]">
					<view>{{t('refundCause')}}</view>
					<view class="w-[400rpx] multi-hidden text-right leading-[1.2]">{{ detail.reason || '--' }}</view>
				</view>
				<view
					class="flex justify-between text-[28rpx] pt-[20rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1] mt-[40rpx]">
					<view>{{t('refundNo')}}</view>
					<view>{{ detail.order_refund_no }}</view>
				</view>
				<view
					class="flex justify-between text-[28rpx] pt-[20rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1] mt-[40rpx]">
					<view>{{t('createTime')}}</view>
					<view>{{ detail.create_time }}</view>
				</view>
				<view
					class="flex justify-between text-[28rpx] pt-[20rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1] mt-[40rpx]">
					<view>{{t('createExplain')}}</view>
					<view>{{ detail.remark }}</view>
				</view>
			</view>

			<view class="bg-[#fff] mx-[30rpx] p-[30rpx] mt-[30rpx] rounded-[10rpx]">
				<view
					class="flex justify-between text-[28rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1]">
					<view>{{t('record')}}</view>
					<view class="flex items-center" @click="redirect({url: '/addon/shop/pages/refund/log', param: { order_refund_no: orderRefundNo }})">
						<text>{{t('check')}}</text>
						<text class="iconfont iconxiangyoujiantou text-xs"></text>
					</view>
				</view>
			</view>

			<view class="flex tab-bar justify-between items-center bg-[#fff] fixed left-0 right-0 bottom-0 min-h-[100rpx] px-1 flex-wrap">
				<view class="flex ml-[30rpx] w-[70rpx] flex-col justify-center items-center" @click="redirect({ url: '/addon/shop/pages/index' });">
					<text class="iconfont iconshouye text-[32rpx]"></text>
					<text class="text-xs mt-1">{{t('index')}}</text>
				</view>
				<view class="flex justify-end mr-[30rpx]">
					<view class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full ml-[20rpx]" @click="cancelRefundFn(detail)" v-if="['6','7','8','-1'].indexOf(detail.status) == -1">{{t('refundApply')}}</view>
				</view>
			</view>
		</view>
		<view v-else>
			<view class="bg-[#fff] mx-[30rpx] p-[30rpx] rounded-[10rpx] flex justify-between flex-wrap mt-[30rpx]">
				<view class="w-[160rpx] h-[160rpx] flex-2" @click="goodsEvent(detail.order_goods.goods_id)">
					<u--image class="rounded-[10rpx] overflow-hidden" width="160rpx" height="160rpx"
						:src="img(detail.order_goods.sku_image ? detail.order_goods.sku_image : '')"
						model="aspectFill">
						<template #error>
							<u-icon name="photo" color="#999" size="50"></u-icon>
						</template>
					</u--image>
				</view>
				<view class="ml-[20rpx] flex flex-1 flex-col justify-between">
					<view>
						<text class="text-[28rpx] text-item leading-[40rpx]">{{ detail.order_goods.goods_name }}</text>
						<view class="flex" v-if="detail.order_goods.sku_name">
							<text
								class="block text-[20rpx] text-item mt-[10rpx] text-[#ccc] bg-[#f5f5f5] px-[16rpx] py-[6rpx] rounded-[999rpx]">{{
									detail.order_goods.sku_name }}</text>
						</view>
					</view>
					<view class="flex justify-between">
						<text class="text-right text-[28rpx]  text-[var(--price-text-color)] price-font">￥{{ detail.order_goods.price }}</text>
						<text class="text-right text-sm"><text class="text-[26rpx]">x</text>{{ detail.order_goods.num }}</text>
					</view>
				</view>
			</view>
			<view class="bg-[#fff] mx-[30rpx] p-[30rpx] rounded-[10rpx] mt-[30rpx]">
				<view
					class="flex justify-between text-[28rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1]">
					<view>联系人</view>
					<view>{{ detail.contact_name }}</view>
				</view>
				<view
					class="flex justify-between text-[28rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1] mt-[40rpx]">
					<view>手机号</view>
					<view>{{ detail.mobile }}</view>
				</view>
				<view
					class="flex justify-between text-[28rpx] border-top-[2rpx] border-[solid] border-[#f1f1f1] mt-[40rpx]">
					<view>退货地址</view>
					<view class="w-[460rpx] text-sm">{{ detail.refund_address.full_address || '--' }}</view>
				</view>
			</view>
			<view class="bg-[#fff] mx-[30rpx] px-[30rpx] py-[10rpx] rounded-[10rpx] mt-[30rpx]">
				<u--form labelPosition="left" :model="formData" :rules="rules" ref="deliveryForm" labelWidth="140rpx" :labelStyle="{'fontSize': '28rpx'}">
					<u-form-item label="物流公司" prop="express_company" borderBottom="true" ref="item1">
						<u--input pl border="none" v-model="formData.express_company" placeholder="请输入物流公司" placeholderClass="text-sm" fontSize="28rpx"></u--input>
					</u-form-item>
					<u-form-item label="物流单号" prop="express_number" borderBottom="true" ref="item1">
						<u--input border="none" placeholder="请输入物流单号" v-model="formData.express_number" placeholderClass="text-sm" fontSize="28rpx"></u--input>
					</u-form-item>
					<u-form-item label="物流说明" borderBottom ref="item1">
						<u--input border="none" placeholder="选填" v-model="formData.remark" placeholderClass="text-sm" fontSize="28rpx"></u--input>
					</u-form-item>
				</u--form>
			</view>
			<view class="mx-[30rpx]">
				<u-button class="mt-[30rpx]" type="primary" shape="circle" @click="deliverySave">提交</u-button>
			</view>
		</view>
		<logistics-tracking ref="materialRef"></logistics-tracking>
		<u-modal :show="cancelRefundshow" :content="t('cancelRefundContent')" :showCancelButton="true" :closeOnClickOverlay="true" @cancel="refundCancel" @confirm="refundConfirm"></u-modal>
	</view>

	<u-loading-page bg-color="rgb(248,248,248)" :loading="loading" fontSize="16" color="#333"></u-loading-page>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app'
import { t } from '@/locale'
import { img, redirect, copy } from '@/utils/common';
import { getRefundDetail, refundDelivery, editRefundDelivery, closeRefund } from '@/addon/shop/api/refund';
import logisticsTracking from '@/addon/shop/pages/order/components/logistics-tracking/logistics-tracking.vue'

let detail = ref<Object>({});
let loading = ref<boolean>(true);
let orderRefundNo = ref('');
let type = ref('');
let isEditDelivery = ref(false);
// 物流信息
const formData = ref({
	express_number: '',
	express_company: '',
	remark: ''
})

// 物流验证
const rules = {
	'express_number': {
		type: 'string',
		required: true,
		message: '请输入物流单号',
		trigger: ['blur', 'change']
	},
	'express_company': {
		type: 'string',
		required: true,
		message: '请输入物流公司',
		trigger: ['blur', 'change']
	}
}

onLoad((option) => {
	orderRefundNo.value = option.order_refund_no;
	type.value = option.type;
	isEditDelivery.value = option.is_edit_delivery;

	refundDetailFn(orderRefundNo.value);
});

const refundDetailFn = (refundNo) => {
	loading.value = true;
	getRefundDetail(refundNo).then((res) => {
		detail.value = res.data;
		// 赋值物流信息
		if(isEditDelivery.value && detail.value.delivery){
			formData.value.express_number = detail.value.delivery.express_number
			formData.value.express_company = detail.value.delivery.express_company
			formData.value.remark = detail.value.delivery.remark
		}
		loading.value = false;
	}).catch(() => {
		loading.value = false;
	})
}

const goodsEvent = (id: number) => {
	redirect({
		url: '/addon/shop/pages/goods/detail',
		param: {
			goods_id: id
		}
	})
}

// 撤销维权
let cancelRefundshow = ref(false);
let currRefundOn = "";
const cancelRefundFn = (data) => {
	currRefundOn = data.order_refund_no;
	cancelRefundshow.value = true;
}

const refundConfirm = ()=>{
	closeRefund(currRefundOn).then((res) => {
		cancelRefundshow.value = false;
		refundDetailFn(orderRefundNo.value);
	}).catch(() => {
		cancelRefundshow.value = false;
	})
}

const refundCancel = ()=>{
	cancelRefundshow.value = false;
}

// 提交物流信息
let deliveryForm = ref()
const deliverySave = ()=>{
	deliveryForm.value.validate().then(res => {
		let obj = {delivery: formData.value,order_refund_no: detail.value.order_refund_no}
		let api = isEditDelivery.value ? editRefundDelivery(obj) : refundDelivery(obj);
		api.then((res) => {
			setTimeout(()=>{
				redirect({ url: '/addon/shop/pages/refund/list' })
			},500)
		}).catch(() => {
		})
	})
}

</script>
<style lang="scss" scoped>
@import '@/addon/shop/styles/common.scss';

.text-item {
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.text-color {
	color: $u-primary;
}
</style>
