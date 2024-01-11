<template>
	<view class="bg-[#f8f8f8] min-h-screen overflow-hidden">

		<mescroll-body ref="mescrollRef" top="30rpx" @init="mescrollInit" @down="downCallback" @up="getRefundListFn">
			<view class="goods-wrap">
				<view class="mb-[30rpx] bg-[#fff] pb-[24rpx]" v-for="(item,index) in list" :key="index">
					<view @click="toLink(item)">
						<view class="text-[30rpx] text-[#222] flex justify-between px-[24rpx] py-[24rpx] rounded-[16rpx]">
							<view>{{item.order_refund_no}}</view>
							<view class="text-[var(--primary-color)]">{{item.status_name}}</view>
						</view>
						<view class="order-goods-item flex mt-[30rpx] px-[24rpx]">
							<view class="w-[160rpx] h-[160rpx] flex-2">
								<u--image class="rounded-[10rpx] overflow-hidden" width="160rpx" height="160rpx"
									:src="img(item.order_goods.goods_image_thumb_small ? item.order_goods.goods_image_thumb_small : '')" model="aspectFill">
									<template #error>
										<u-icon name="photo" color="#999" size="50"></u-icon>
									</template>
								</u--image>
							</view>
							<view class="ml-[20rpx] flex flex-1 flex-col justify-between">
								<view >
									<text class="text-[28rpx] text-item">{{ item.order_goods.goods_name }}</text>
									<view class="flex" v-if="item.order_goods.sku_name">
										<text class="block text-[20rpx] text-item mt-[10rpx] text-[#ccc] bg-[#f5f5f5] px-[16rpx] py-[6rpx] rounded-[999rpx]">{{ item.order_goods.sku_name }}</text>
									</view>
								</view>
								<view class="flex justify-between">
									<text class="text-right text-[28rpx] text-[var(--price-text-color)] price-font">￥{{ item.order_goods.price }}</text>
									<text class="text-right text-[24rpx]">x{{ item.order_goods.num }}</text>
								</view>
							</view>
						</view>
					</view>
					<view class="flex px-[24rpx] mt-[40rpx]">
						<text class="ml-auto mr-[20rpx] text-xs">{{ t('actualPayment') }}：<text class="text-sm price-font">￥{{ item.order_goods.order_goods_money }}</text></text>
						<text class="text-xs">{{t('refundMoney')}}：<text class="text-sm text-[var(--price-text-color)] price-font">￥{{ item.apply_money }}</text></text>
					</view>
					<view class="mt-[26rpx] flex flex-wrap justify-end z-10 px-[24rpx]">
						<view class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full ml-[20rpx]" @click="toLink(item)">{{ t('orderDetail') }}</view>
						<view class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full ml-[20rpx]" @click="refundBtnFn(item,'cancel')" v-if="['6','7','8','-1'].indexOf(item.status) == -1">{{t('refundApply')}}</view>
						<view v-if="['3'].indexOf(item.status) != -1" class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full ml-[20rpx]" @click="refundBtnFn(item,'edit')" >编辑退款信息</view>
						<view v-if="['2'].indexOf(item.status) != -1" class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full ml-[20rpx]" @click="refundBtnFn(item,'logistics')">填写发货物流</view>
						<view v-if="['5'].indexOf(item.status) != -1" class="inline-block text-[26rpx] leading-[56rpx] px-[30rpx] border-[3rpx] border-solid border-[#999] rounded-full ml-[20rpx]" @click="refundBtnFn(item,'editLogistics')">编辑发货物流</view>
					</view>
				</view>
			</view>
			<mescroll-empty :option="{'icon': img('static/resource/images/empty.png')}" v-if="!list.length && loading"></mescroll-empty>
		</mescroll-body>
		<u-modal :show="cancelRefundshow" :content="t('cancelRefundContent')" :showCancelButton="true" :closeOnClickOverlay="true" @cancel="refundCancel" @confirm="refundConfirm"></u-modal>
	</view>
</template>

<script setup lang="ts">
	import { ref } from 'vue';
	import { t } from '@/locale'
	import { img, redirect } from '@/utils/common';
	import { getRefundList, closeRefund } from '@/addon/shop/api/refund';
	import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
	import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
	import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
	import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';

	const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);
	let list = ref<Array<Object>>([]);
	let loading = ref<boolean>(false);
	let cancelRefundshow = ref(false);

	const getRefundListFn = (mescroll) => {
		loading.value = false;
		let data : object = {
			page: mescroll.num,
			limit: mescroll.size
		};

		getRefundList(data).then((res) => {
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

	const toLink = (data) => {
		redirect({ url: '/addon/shop/pages/refund/detail', param: { order_refund_no : data.order_refund_no } })
	}

	let currRefundOn = "";
	const refundBtnFn = (data, type) => {
		if(type == 'cancel'){
			currRefundOn = data.order_refund_no;
			cancelRefundshow.value = true;
		}else if(type == 'edit'){
			redirect({ url: '/addon/shop/pages/refund/edit_apply', param: { order_refund_no : data.order_refund_no } })
		}else if(type == 'logistics'){
			redirect({ url: '/addon/shop/pages/refund/detail', param: { order_refund_no : data.order_refund_no, type: 'logistics' } })
		}else if(type == 'editLogistics'){
			redirect({ url: '/addon/shop/pages/refund/detail', param: { order_refund_no : data.order_refund_no, type: 'logistics', is_edit_delivery: true } })
		}
	}

	const refundConfirm = ()=>{
		closeRefund(currRefundOn).then((res) => {
			cancelRefundshow.value = false;
			getMescroll().resetUpScroll();
		}).catch(() => {
			cancelRefundshow.value = false;
		})
	}

	const refundCancel = ()=>{
		cancelRefundshow.value = false;
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
	.order-goods-item:nth-child(1) {
		margin-top: 20rpx;
	}
	.font-scale{
		transform: scale(0.75);
	}
	.text-color{
		color: $u-primary;
	}
	.bg-color{
		background-color: $u-primary;
	}

	.goods-wrap{
		margin: 0 20rpx;
		.goods-item{
			@apply w-full flex flex-col mb-3 bg-[#fff] py-3 px-4 box-border;
			border-radius: 18rpx;
			overflow: hidden;
			.goods-head{
				@apply flex justify-between pb-3 border-0 border-b-1 border-solid border-[#F0F0F0] mb-4;
				font-size: 26rpx;
				color: #666;
			}
			.goods-content{
				@apply flex;
				& > image{
					width: 40rpx;
					height: 40rpx;
					margin-right: 30rpx;
				}
				& > view{
					flex: 1;
				}
				.name-wrap{
					display: flex;
					justify-content: space-between;
					margin-bottom: 30rpx;
					&> view{
						&:first-of-type{
							font-weight: bold;
							font-size: 30rpx;
						}
						&:last-of-type{
							color: #EA4B69;
							font-size: 28rpx;
							font-weight: bold;
						}
					}
				}
				.desc{
					color: #686868;
					font-size: 26rpx;
					margin-bottom: 14rpx;
				}
				.time-wrap{
					display: flex;
					align-items: center;
					background-color: #F6F7FB;
					border-radius: 8rpx;
					height: 62rpx;
					font-size: 26rpx;
					padding: 0 16rpx;
					text{
						&:nth-child(1){
							color: #444;
							margin-right: 14rpx;
						}
						&:nth-child(2){
							color: #686868;
						}
						&:nth-child(3){
							color: #333;
							font-weight: bold;
						}
					}
				}
				.btn-wrap{
					justify-content: flex-end;
					@apply flex margin-0 mt-2 flex-wrap;
					button{
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
						&[type="primary"]{
							background-color: $u-primary;
						}
						&::after{
							border: none;
						}
					}
				}
			}
		}
	}
</style>
