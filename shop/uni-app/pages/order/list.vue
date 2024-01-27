<template>
	<view class="bg-[#f8f8f8] min-h-screen overflow-hidden order-list">
		<view class="fixed left-0 top-0 right-0 z-10" v-if="statusLoading">
			<scroll-view scroll-x="true" class="scroll-Y box-border px-[24rpx] bg-white">
				<view class="flex whitespace-nowrap justify-around">
					<view :class="['text-sm leading-[90rpx]', { 'class-select': orderState === item.status.toString() }]"
						@click="orderStateFn(item.status)" v-for="(item, index) in orderStateList">{{ item.name }}</view>
				</view>
			</scroll-view>
		</view>

		<mescroll-body ref="mescrollRef" top="104rpx" @init="mescrollInit" @down="downCallback" @up="getShopOrderFn">
			<view class="goods-wrap">
				<template v-for="(item, index) in list" :key="index">
					<view class="mb-[30rpx] bg-[#fff] pb-[24rpx]">
						<view @click="toLink(item)">
							<view class="bg-[#fff] px-[24rpx] py-[24rpx] rounded-[16rpx]">
								<view class="text-[30rpx] text-[#222] flex  justify-between">
									<view class="">
										{{ t('orderNo') }}：{{ item.order_no }}
									</view>
									<view class="text-[var(--primary-color)]">{{ item.status_name.name }}</view>
								</view>
							</view>
							<view class="px-[24rpx]">
								<view class="order-goods-item flex mt-[50rpx]" v-for="(subitem, index) in item.order_goods"
									:key="index">
									<view class="w-[160rpx] h-[160rpx] flex-2">
										<u--image class="rounded-[10rpx] overflow-hidden" width="160rpx" height="160rpx"
											:src="img(subitem.goods_image_thumb_small ? subitem.goods_image_thumb_small : '')"
											model="aspectFill">
											<template #error>
												<u-icon name="photo" color="#999" size="50"></u-icon>
											</template>
										</u--image>
									</view>
									<view class="ml-[20rpx] flex flex-1 flex-col justify-between">
										<view>
											<text class="text-[28rpx] text-item  leading-[40rpx]">{{ subitem.goods_name
											}}</text>
											<view class="flex" v-if="subitem.sku_name">
												<text
													class="block text-[20rpx] text-item mt-[10rpx] text-[#ccc] bg-[#f5f5f5] px-[16rpx] py-[6rpx] rounded-[999rpx]">{{
														subitem.sku_name }}</text>
											</view>
										</view>
										<view class="flex justify-between">
											<text class="text-right text-[28rpx] text-[var(--price-text-color)] price-font">￥{{
												subitem.price }}</text>
											<text class="text-right text-[24rpx]">x{{ subitem.num }}</text>
										</view>
									</view>
								</view>
							</view>
						</view>
						<view class="flex justify-between text-[28rpx] px-[24rpx] mt-[40rpx]">
							<text class="text-[#999]">{{ item.create_time }}</text>
							<text>{{ t('actualPayment') }}：<text class=" text-[var(--price-text-color)] price-font">￥{{
								item.order_money }}</text></text>
						</view>
						<view class="mt-[34rpx] flex justify-end z-10 px-[24rpx]">
							<view
								class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full"
								v-if="item.status == 1" @click="orderBtnFn(item, 'close')">{{ t('orderClose') }}</view>
							<view
								class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full ml-[20rpx]"
								@click="toLink(item)">{{ t('orderDetail') }}</view>
							<view
								class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid text-[#fff] border-primary bg-primary rounded-full ml-[20rpx]"
								@click="orderBtnFn(item, 'pay')" v-if="item.status == 1">{{ t('topay') }}</view>
							<view
								class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid text-[#fff] border-primary bg-primary rounded-full ml-[20rpx]"
								@click="orderBtnFn(item, 'finish')" v-if="item.status == 3">{{ t('orderFinish') }}</view>
							<view
								class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full ml-[20rpx]"
								v-if="item.status == 5 && evaluateConfig.is_evaluate == 1"
								@click="orderBtnFn(item, 'evaluate')">{{ t('evaluate') }}</view>
						</view>
					</view>
				</template>
			</view>
			<mescroll-empty :option="{ 'icon': img('static/resource/images/empty.png') }"
				v-if="!list.length && loading"></mescroll-empty>
		</mescroll-body>
		<pay ref="payRef" @close="payClose"></pay>
	</view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { t } from '@/locale'
import { img, redirect } from '@/utils/common';
import { getShopOrderStatus, getShopOrder, orderClose, orderFinish } from '@/addon/shop/api/order';
import { getEvaluateConfig } from '@/addon/shop/api/shop';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);
let list = ref<Array<Object>>([]);
let loading = ref<boolean>(false);
let statusLoading = ref<boolean>(false);
let orderState = ref('')
let orderStateList = ref([]);
const evaluateConfig = ref("")

onLoad((option) => {
	orderState.value = option.status || "";
	evaluateEvent()
	getShopOrderStatusFn();
});

const evaluateEvent = () => {
	getEvaluateConfig().then((data) => {
		evaluateConfig.value = data.data
	})
}

const getShopOrderFn = (mescroll) => {
	loading.value = false;
	let data: object = {
		page: mescroll.num,
		limit: mescroll.size,
		status: orderState.value
	};

	getShopOrder(data).then((res) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (mescroll.num == 1) {
			list.value = []; //如果是第一页需手动制空列表
		}
		list.value = list.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}

const getShopOrderStatusFn = () => {
	statusLoading.value = false;
	orderStateList.value = [];
	let obj = { name: '全部', status: '' };
	orderStateList.value.push(obj);

	getShopOrderStatus().then((res) => {
		Object.values(res.data).forEach((item, index) => {
			orderStateList.value.push(item);
		});
		statusLoading.value = true;
	}).catch(() => {
		statusLoading.value = true;
	})
}

const orderStateFn = (status) => {
	orderState.value = status.toString();
	list.value = [];
	getMescroll().resetUpScroll();
};

const toLink = (data) => {
	redirect({ url: '/addon/shop/pages/order/detail', param: { order_id: data.order_id } })
}

// 支付
const payRef = ref(null)
const orderBtnFn = (data, type = '') => {
	if (type == 'pay')
		payRef.value?.open(data.order_type, data.order_id, `/addon/shop/pages/order/detail?order_id=${data.order_id}`);
	else if (type == 'close') {
		close(data);
	} else if (type == 'finish') {
		finish(data);
	} else if (type == 'evaluate') {
		if (!data.is_evaluate) {
			redirect({ url: '/addon/shop/pages/evaluate/order_evaluate', param: { order_id: data.order_id } })
		} else {
			redirect({ url: '/addon/shop/pages/evaluate/order_evaluate_view', param: { order_id: data.order_id } })
		}

	}
}

//关闭订单
const close = (item: any) => {
	uni.showModal({
		title: '提示',
		content: '您确定要关闭该订单吗？',
		success: res => {
			if (res.confirm) {
				orderClose(item.order_id).then((data) => {
					getMescroll().resetUpScroll();
				})
			}
		}
	})
}

//订单完成
const finish = (item: any) => {
	uni.showModal({
		title: '提示',
		content: '您确定物品已收到吗？',
		success: res => {
			if (res.confirm) {
				orderFinish(item.order_id).then((data) => {
					getMescroll().resetUpScroll();
				})
			}
		}
	})
}
</script>
<style>
@import '@/addon/shop/styles/common.scss';
.order-list .mescroll-body {
	padding-bottom: env(safe-area-inset-bottom) !important;

}
</style>
<style lang="scss" scoped>
.text-item {
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.order-goods-item:nth-child(1) {
	margin-top: 20rpx;
}

.font-scale {
	transform: scale(0.75);
}

.text-color {
	color: var(--primary-color);
}

.bg-color {
	background-color: var(--primary-color);
}

.goods-wrap {
	margin: 20rpx 20rpx 0;

	.goods-item {
		@apply w-full flex flex-col mb-3 bg-[#fff] py-3 px-4 box-border;
		border-radius: 18rpx;
		overflow: hidden;

		.goods-head {
			@apply flex justify-between pb-3 border-0 border-b-1 border-solid border-[#F0F0F0] mb-4;
			font-size: 26rpx;
			color: #666;
		}

		.goods-content {
			@apply flex;

			&>image {
				width: 40rpx;
				height: 40rpx;
				margin-right: 30rpx;
			}

			&>view {
				flex: 1;
			}

			.name-wrap {
				display: flex;
				justify-content: space-between;
				margin-bottom: 30rpx;

				&>view {
					&:first-of-type {
						font-weight: bold;
						font-size: 30rpx;
					}

					&:last-of-type {
						color: #EA4B69;
						font-size: 28rpx;
						font-weight: bold;
					}
				}
			}

			.desc {
				color: #686868;
				font-size: 26rpx;
				margin-bottom: 14rpx;
			}

			.time-wrap {
				display: flex;
				align-items: center;
				background-color: #F6F7FB;
				border-radius: 8rpx;
				height: 62rpx;
				font-size: 26rpx;
				padding: 0 16rpx;

				text {
					&:nth-child(1) {
						color: #444;
						margin-right: 14rpx;
					}

					&:nth-child(2) {
						color: #686868;
					}

					&:nth-child(3) {
						color: #333;
						font-weight: bold;
					}
				}
			}

			.btn-wrap {
				justify-content: flex-end;
				@apply flex margin-0 mt-2 flex-wrap;

				button {
					width: 172rpx;
					height: 64rpx;
					font-size: 26rpx;
					@apply rounded-3xl;
					line-height: 64rpx;
					background-color: transparent;
					margin: 0;
					margin-left: 20rpx;
					@apply mt-2;
					border: 2rpx solid #E2E2E2;

					&[type="primary"] {
						background-color: var(--primary-color);
					}

					&::after {
						border: none;
					}
				}
			}
		}
	}
}

.class-select {
	position: relative;
	font-weight: bold;

	&::after {
		content: "";
		position: absolute;
		bottom: 0;
		height: 6rpx;
		background-color: var(--primary-color);
		width: 90%;
		left: 50%;
		transform: translateX(-50%);
	}
}</style>
