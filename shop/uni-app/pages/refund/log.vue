<template>
	<view class="bg-[#f8f8f8] min-h-screen overflow-hidden" v-if="!loading">
		<view class="bg-[#fff] mx-[30rpx] p-[20rpx] mt-[30rpx] rounded-[10rpx]" v-for="(item,index) in detail.refund_log">
			<view class="text-sm">{{item.main_type_name}}</view>
			<view class="text-xs my-[6rpx] text-[#909399]">{{item.create_time}}</view>
			<view class="text-sm">{{item.type_name || '--'}}</view>
		</view>
		<view class="pt-[140rpx]"></view>
		<view class="flex tab-bar items-center bg-[#fff] fixed left-0 right-0 bottom-0 min-h-[120rpx] px-1 flex-wrap">
			<u-button class="!text-sm" type="primary" shape="circle" @click="redirect({url: '/addon/shop/pages/refund/detail', param: { order_refund_no: orderRefundNo }})">返回详情</u-button>
		</view>
	</view>

	<u-loading-page bg-color="rgb(248,248,248)" :loading="loading" fontSize="16" color="#333"></u-loading-page>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app'
import { t } from '@/locale'
import { img, redirect } from '@/utils/common';
import { getRefundDetail } from '@/addon/shop/api/refund';

let detail = ref<Object>({});
let loading = ref<boolean>(true);
let orderRefundNo = ref('')

onLoad((option) => {
	orderRefundNo.value = option.order_refund_no;
	refundDetailFn(option.order_refund_no);
});

const refundDetailFn = (refundNo) => {
	loading.value = true;
	getRefundDetail(refundNo).then((res) => {
		detail.value = res.data;
		loading.value = false;
	}).catch(() => {
		loading.value = false;
	})
}
</script>
<style>
@import '@/addon/shop/styles/common.scss';
</style>
