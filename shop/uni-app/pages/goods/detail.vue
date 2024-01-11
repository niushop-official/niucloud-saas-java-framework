<template>
	<view class="bg-[#f6f6f6] min-h-[100vh] relative" v-if="Object.keys(goodsDetail).length">
		<u-swiper :list="goodsDetail.goods.goods_image" :autoplay="false" height="100vw"></u-swiper>
		<view class="absolute top-[20rpx] right-[40rpx]">
			<view class="w-[60rpx] h-[60rpx] flex items-center justify-center bg-[rgba(0,0,0,.2)] rounded-full"
				@click="collectFn">
				<text
					:class="['iconfont', (isCollect ? 'text-[#ff0000]' : 'text-[#fff]'), (isCollect ? 'iconshoucang_shoucang' : 'icona-shoucang-weishoucang')]"></text>
			</view>
		</view>
		<view class="mt-[20rpx] bg-white mx-[24rpx] rounded-[16rpx] py-[24rpx] px-[24rpx]">
			<view class="flex items-center">
				<text class="text-[var(--price-text-color)]">
					<text class="text-[28rpx] font-bold price-font">￥</text>
					<text class="text-[42rpx] mr-[10rpx]  font-bold price-font">{{ goodsDetail.sale_price }}</text>
					<text class="text-[26rpx] text-[#999] line-through font-500">
						<text class="price-font">￥{{ goodsDetail.market_price }}</text>
					</text>
				</text>
				<view class="text-[24rpx] text-[#666] flex items-center ml-auto">
					<text>销量</text>
					<text class="mx-[6rpx]">{{ goodsDetail.goods.sale_num }}</text>
					<text>{{ goodsDetail.goods.unit }}</text>
				</view>
			</view>
			<view class="mt-[10rpx] font-600 text-[32rpx] max-h-[108rpx] multi-hidden leading-[54rpx]">
				{{ goodsDetail.goods.goods_name }}
			</view>
			<view class="flex flex-wrap" v-if="goodsDetail.label_info && goodsDetail.label_info.length">
				<view v-for="item in goodsDetail.label_info" :key="item.label_id"
					class="mt-[10rpx] text-[#FA6400] leading-[40rpx] text-[22rpx] h-[40rpx] px-[10rpx] border-[2rpx] border-solid border-[#FA6400] rounded-[6rpx] mr-[15rpx] box-border">
					{{ item.label_name }}
				</view>
			</view>
		</view>

		<view class="mt-[20rpx] bg-white mx-[24rpx] rounded-[16rpx]">
			<view @click="servicesDataShow = !servicesDataShow" v-if="goodsDetail.service"
				class="flex items-center h-[88rpx] border-0 border-b-[2rpx] border-solid border-[#ebebec] px-[20rpx]">
				<text class="text-[#999] text-[30rpx] leading-[42rpx] font-500 mr-[20rpx]">服务</text>
				<view class="flex-1 text-[#343434] text-sm leading-[42rpx] font-500">
					{{ goodsDetail.service[0].service_name }}
				</view>
				<text class="iconfont iconxiangyoujiantou text-sm"></text>
			</view>
			<view @click="buyFn" v-if="goodsDetail.goodsSpec && goodsDetail.goodsSpec.length"
				class="flex items-center h-[88rpx] px-[20rpx] border-0 border-b-[2rpx] border-solid border-[#ebebec]">
				<text class="text-[#999] text-[30rpx] leading-[42rpx] font-500 mr-[20rpx]">已选</text>
				<view class="flex-1 text-[#343434] text-sm leading-[42rpx] font-500">
					{{ goodsDetail.sku_spec_format }}
				</view>
				<text class="iconfont iconxiangyoujiantou text-sm"></text>
			</view>
			<view class="flex items-center h-[88rpx] border-0 border-b-[2rpx] border-solid border-[#ebebec] px-[20rpx]"
				v-if="goodsDetail.goods.goods_type == 'real'&&goodsDetail.delivery_type_list&&goodsDetail.delivery_type_list.length">
				<text class="text-[#999] text-[30rpx] leading-[42rpx] font-500 mr-[20rpx]">配送</text>
				<view class="flex-1 flex items-center text-[#343434] text-sm leading-[42rpx] font-500">
					<template v-for="(item, index) in goodsDetail.delivery_type_list">
						<text v-if="index" class="w-[7rpx] h-[7rpx] rounded-[7rpx] mx-[10rpx] bg-[#333]"></text>
						<text>{{ item }}</text>
					</template>
				</view>
				<!-- <text class="iconfont iconxiangyoujiantou text-sm"></text> -->
			</view>
			<view v-if="couponList.length" class="flex items-center h-[88rpx] px-[20rpx]">
				<text class="text-[#999] text-[30rpx] leading-[42rpx] font-500 mr-[20rpx]">领券</text>
				<view
					class="flex-1 flex items-center whitespace-nowrap overflow-hidden h-[44rpx] flex-wrap content-between">
					<view v-for="(item, index) in couponList" :key="index"
						class="text-xs rounded-sm border-[2rpx] px-[6rpx] py-[2rpx] border-solid border-[var(--primary-color)] text-[var(--primary-color)] mr-[8rpx] mt-[3rpx]">
						{{ item.title }}
					</view>
				</view>
				<view class="ml-[8rpx] flex items-center" @click="couponListShow = true">
					<text class="text-xs text-[#737373]">领取</text>
					<text class="iconfont iconxiangyoujiantou text-sm"></text>
				</view>
			</view>

		</view>

		<view class="mt-[20rpx] bg-white mx-[24rpx] rounded-[16rpx] px-[20rpx]">
			<view class="flex items-center justify-between h-[88rpx]">
				<text class="text-[30rpx]">宝贝评价({{ evaluate.count }})</text>
				<view v-if="evaluate.count" class="flex items-center" @click="toLink(goodsDetail.goods_id)">
					<!--  -->
					<text class="text-xs text-[#737373]">查看全部</text>
					<text class="iconfont iconxiangyoujiantou text-xs"></text>

				</view>
				<text v-if="!evaluate.count" class="text-xs text-[#737373]">暂无评价</text>
			</view>
			<view>
				<template v-for="(item, index) in evaluate.list" :key="index">
					<view class="mx-[20rpx] pb-[20rpx] border-0 border-b-[2rpx] border-solid border-[#eee] mb-[20rpx]">
						<view class="flex items-center  justify-between">
							<view class="flex items-center">
								<u-avatar class="mr-[10rpx]" :src="img(item.member_head)" :size="'50rpx'"
									leftIcon="none"></u-avatar>
								<text class="text-sm">{{ item.member_name }}</text>
							</view>
							<text class="text-xs text-[#737373]">{{ item.create_time ? item.create_time.slice(0, 10) : ''
							}}</text>
						</view>
						<view class="text-sm text-[#666] mt-[10rpx] multi-hidden">{{ item.content }}</view>
						<scroll-view scroll-x="true" class="scroll-Y box-border py-[24rpx] bg-white">
							<view class="flex items-center">
								<template v-for="(imageItem, imageIndex) in item.image_small" :key="'item'+imageIndex">
									<u--image class="rounded-[8rpx] overflow-hidden mr-[14rpx] mb-[14rpx]" width="200rpx"
										height="200rpx" :src="img(imageItem)" model="aspectFill"
										@click="imgListPreview(imageItem)">
										<template #error>
											<u-icon name="photo" color="#999" size="50"></u-icon>
										</template>
									</u--image>
								</template>
							</view>
						</scroll-view>
					</view>

				</template>
			</view>
		</view>

		<view class="px-[10px] pd-[10px] mt-[20rpx] bg-white mx-[24rpx] rounded-[16rpx]">
			<view class="py-[20rpx] text-base">商品详情</view>
			<u-parse :content="goodsDetail.goods.goods_desc"></u-parse>
		</view>
		<!-- tabber -->
		<view class="tab-bar-placeholder"></view>
		<view
			class=" w-[100%] flex justify-between py-[9rpx] px-[27rpx] bg-[#fff] box-border fixed left-0 bottom-0 tab-bar">
			<view class="flex items-center">
				<view class="flex flex-col justify-center items-center mr-[39rpx]"
					@click="redirect({ url: '/addon/shop/pages/index' })">
					<view class="iconfont iconshouye text-[42rpx] mb-[4rpx]"></view>
					<text class="text-[18rpx] mt-1">首页</text>
				</view>
				<view class="flex flex-col justify-center items-center mr-[39rpx]">
					<view class="iconfont iconkefu1 text-[46rpx]"></view>
					<text class="text-[18rpx] mt-1">客服</text>
				</view>
				<view class="flex flex-col justify-center items-center"
					@click="redirect({ url: '/addon/shop/pages/goods/cart' })">
					<view class="relative">
						<text class="iconfont icongouwuche text-[46rpx]"></text>
						<view v-if="cartTotalNum"
							:class="['absolute left-[26rpx] top-0 rounded-[25rpx] h-[25rpx] min-w-[25rpx] text-center leading-[25rpx] bg-[#FF4646] text-[#fff] text-[20rpx] font-500 box-border', cartTotalNum > 9 ? 'px-[10rpx]' : '']">
							{{ cartTotalNum }}
						</view>
					</view>
					<text class="text-[18rpx] mt-1">购物车</text>
				</view>
			</view>
			<view class="flex">
				<button
					class="!w-[200rpx] !h-[80rpx] text-sm !text-[#fff] !m-0 !mr-[20rpx] leading-[80rpx] rounded-full remove-border"
					style="background: linear-gradient(127deg, #FFB000 0%, #FFA029 100%);" @click="buyFn('join_cart')">
					加入购物车</button>
				<button
					class="!w-[200rpx] !h-[80rpx] text-sm !text-[#fff] !bg-[#FF4646] !m-0 leading-[80rpx] rounded-full remove-border"
					@click="buyFn('buy_now')">立即购买</button>
			</view>
		</view>
		<!-- 服务 -->
		<u-popup class="popup-type" :show="servicesDataShow" @close="servicesDataShow = false">
			<view class="min-h-[480rpx] rounded-t-[20rpx] overflow-hidden bg-[#fff]">
				<view class="flex items-center justify-center py-[34rpx] relative">
					<text class="text-[32rpx] leading-[36rpx] font-600">商品服务</text>
					<view class="absolute right-[37rpx]  iconfont iconguanbi text-[50rpx]"
						@click="servicesDataShow = false">
					</view>
				</view>
				<scroll-view class="h-[520rpx]" scroll-y="true">
					<view class="pl-[22rpx] pt-[28rpx] pr-[37rpx]">
						<view class="flex mb-[28rpx]" v-for="(item, index) in goodsDetail.service">
							<image class="max-w-[34rpx] max-h-[34rpx] mr-[14rpx]"
								:src="img(item.image || 'addon/shop/icon_service.png')" mode="aspectFit" />
							<view class="flex-1">
								<view class="text-[26rpx] leading-[36rpx] text-[#222] mb-[4rpx] w-[643rpx]">
									{{ item.service_name }}</view>
								<view class="text-[22rpx] leading-[36rpx] text-[#888] w-[643rpx]">{{ item.desc }}</view>
							</view>
						</view>
					</view>
				</scroll-view>
				<view class="px-[32rpx] pb-[67rpx] pt-[42rpx]">
					<button
						class="!w-[100%] !h-[80rpx] text-[30rpx] !bg-[#FF4646]  !m-0  leading-[80rpx] rounded-full text-white"
						@click="servicesDataShow = false">确定</button>
				</view>
			</view>
		</u-popup>
		<!-- 优惠券 -->
		<u-popup class="popup-type" :show="couponListShow" @close="couponListShow = false">
			<view class="min-h-[480rpx] rounded-t-[20rpx] overflow-hidden bg-[#fff]">
				<view class="flex items-center justify-center py-[34rpx] relative">
					<text class="text-[32rpx] leading-[36rpx] font-600">优惠券</text>
					<view class="absolute right-[37rpx]  iconfont iconguanbi text-[50rpx]" @click="couponListShow = false">
					</view>
				</view>
				<scroll-view class="h-[520rpx]" scroll-y="true">
					<view class="px-[20rpx]">
						<view
							class="mb-[30rpx] flex items-center border-[2rpx] border-solid border-[rgba(0,0,0,.1)] rounded"
							v-for="(item, index) in couponList" :key="index">
							<view
								class="flex flex-col items-center py-[20rpx] w-[240rpx] border-0 border-r-[2rpx] border-dashed border-[rgba(0,0,0,.1)]">
								<view class="text-xs price-font">
									<text class="text-[28rpx]">￥</text>
									<text class="text-[48rpx]">{{ item.price }}</text>
								</view>
								<text class="text-xs mt-[12rpx]">{{ Number(item.min_condition_money) ? ('满' +
									item.min_condition_money + '元可以使用') : '无门槛优惠券' }}</text>
							</view>
							<view class="ml-[20rpx] flex-1 flex flex-col py-[20rpx]">
								<text class="text-xs">{{ item.title }}</text>
								<text class="text-xs text-[#ABABAB] mt-[12rpx]">{{ item.valid_type == 1 &&
									('领取之日起' + item.length + '天内有效') || item.valid_type == 2 &&
									('有效期' + item.valid_start_time + '至' + item.valid_end_time) }}</text>
							</view>
							<text v-if="item.btnType === 'collecting'"
								class="bg-[var(--primary-color)] rounded-2xl text-[#fff] text-xs mr-[20rpx] py-[8rpx] px-[16rpx]"
								@click="getCouponFn(item, index)">领取</text>
							<text v-else
								class="!bg-[#fff] rounded-2xl text-[#ABABAB] text-xs mr-[20rpx] py-[8rpx] px-[16rpx]">{{
									item.btnType
									=== 'collected' ? '已领玩' : '已领取' }}</text>
						</view>
					</view>
				</scroll-view>
				<view class="px-[32rpx] pb-[67rpx] pt-[42rpx]">
					<button
						class="!w-[100%] !h-[80rpx] text-[30rpx] !bg-[var(--primary-color)] !text-[#fff] !m-0 rounded-full leading-[80rpx]"
						@click="couponListShow = false">确定</button>
				</view>
			</view>
		</u-popup>
	</view>
	<ns-goods-sku ref="goodsSkuRef" :goods-detail="goodsDetail" @change="specSelectFn"></ns-goods-sku>
	<u-loading-page bg-color="rgb(248,248,248)" :loading="loading" fontSize="16" color="#333"></u-loading-page>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app'
import { img, redirect } from '@/utils/common';
import { t } from '@/locale';
import { getGoodsDetail, collect, cancelCollect, getEvaluateList } from '@/addon/shop/api/goods';
import { getShopCouponList, getCoupon } from '@/addon/shop/api/coupon';
import nsGoodsSku from '@/addon/shop/components/ns-goods-sku/ns-goods-sku.vue';
import useCartStore from '@/addon/shop/stores/cart'
import { useLogin } from '@/hooks/useLogin'
import useMemberStore from '@/stores/member'

// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

// 购物车数量
const cartStore = useCartStore();
let cartTotalNum = computed(() => cartStore.totalNum)

let goodsSkuRef = ref(null);
let goodsDetail = ref({});

let loading = ref<boolean>(false);
let servicesDataShow = ref<boolean>(false)
let couponListShow = ref<boolean>(false) //优惠券
onLoad((option) => {
	getGoodsDetail({
		goods_id: option.goods_id || '',
		sku_id: option.sku_id || '',
	}).then(res => {
		if (JSON.stringify(res.data) === '[]') {
			uni.showToast({ title: '找不到该商品', icon: 'none' })
			setTimeout(() => {
				redirect({ url: '/addon/shop/pages/index', mode: 'reLaunch' })
			}, 600)
			return false
		}

		goodsDetail.value = res.data;
		isCollect.value = goodsDetail.value.goods.is_collect;
		goodsDetail.value.delivery_type_list = goodsDetail.value.goods.delivery_type_list ? Object.values(goodsDetail.value.goods.delivery_type_list).map(el => el.name) : [];
		goodsDetail.value.goods.goods_image = goodsDetail.value.goods.goods_image_thumb_big;
		goodsDetail.value.goods.goods_image.forEach((item, index) => {
			goodsDetail.value.goods.goods_image[index] = img(item);
		})

		// 获取优惠券列表
		getShopCouponListFn();

		// 获取评价
		getEvaluateListFn();
	})
})

onShow(() => {

	cartStore.getList();
})

const specSelectFn = (id) => {
	goodsDetail.value.skuList.forEach((item, index) => {
		if (item.sku_id == id) {
			Object.assign(goodsDetail.value, item);
		}
	})
}

const buyFn = (type) => {
	goodsSkuRef.value.open(type)
}

// 收藏
let isCollect = ref(0);
const collectFn = () => {
	// 检测是否登录
	if (!userInfo.value) {
		useLogin().setLoginBack({ url: '/addon/shop/pages/goods/detail', param: { sku_id: goodsDetail.value.sku_id } })
		return false
	}
	let api = isCollect.value ? cancelCollect(goodsDetail.value.goods_id) : collect(goodsDetail.value.goods_id);
	api.then(res => {
		isCollect.value = !isCollect.value;
		if (isCollect.value) {
			uni.showToast({
				title: '收藏成功',
				icon: 'none'
			});
		} else {
			uni.showToast({
				title: '取消收藏',
				icon: 'none'
			});
		}
	})
}

// 优惠券
let couponList = ref([]);
const getShopCouponListFn = () => {
	getShopCouponList({
		category_id: goodsDetail.value.goods.goods_category || '',
		goods_id: goodsDetail.value.goods_id || ''
	}).then(res => {
		couponList.value = res.data.data.map((el: any) => {
			if (el.sum_count != -1 && el.receive_count === el.sum_count) {
				el.btnType = 'collected'//已领完
			}
			if (!userInfo.value) {
				el.btnType = 'collecting'//领用
			} else {
				if (el.is_receive && el.limit_count === el.member_receive_count) {
					el.btnType = 'using'//去使用
				} else {
					el.btnType = 'collecting'//领用
				}
			}
			return el
		});
	})
}

// 领取优惠券
const getCouponFn = (data, index) => {
	// 检测是否登录
	if (!userInfo.value) {
		useLogin().setLoginBack({ url: '/addon/shop/pages/goods/detail', param: { sku_id: goodsDetail.value.sku_id } })
		return false
	}
	getCoupon({
		coupon_id: data.id || '',
		number: 1,
	}).then(res => {
		couponList.value[index].btnType = 'using'
	})
}

// 获取评价
const evaluate = ref({})
const getEvaluateListFn = () => {
	getEvaluateList(goodsDetail.value.goods_id).then(res => {
		evaluate.value = res.data
	})
}
//进入评论
const toLink = () => {
	redirect({ url: '/addon/shop/pages/evaluate/list', param: { goods_id: goodsDetail.value.goods_id } })
}

//预览图片
const imgListPreview = (item) => {
	if (item === '') return false
	var urlList = []
	urlList.push(img(item))  //push中的参数为 :src="item.img_url" 中的图片地址
	uni.previewImage({
		indicator: "number",
		loop: true,
		urls: urlList
	})
}

</script>
<style>
@import '@/addon/shop/styles/common.scss';
</style>
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

.text-color {
	color: var(--primary-color);
}

.border-color {
	border-color: var(--primary-color);
}

.background-color {
	background-color: var(--primary-color-light);
}

:deep(.u-cell-group__wrapper) {
	.u-cell__body {
		padding: 23rpx 32rpx;
	}
}

.chatQrcode {
	background: linear-gradient(#ecf5ff 0%, #fff 70%);
}

.popup-type {
	:deep(.u-popup__content) {
		background: transparent;
	}
}

.label-select {
	color: var(--primary-color);
	border-color: var(--primary-color);
	background-color: var(--primary-color-light);
}

.tab-bar-placeholder {
	padding-bottom: calc(constant(safe-area-inset-bottom) + 100rpx);
	padding-bottom: calc(env(safe-area-inset-bottom) + 100rpx);
}

.tab-bar {
	padding-bottom: calc(constant(safe-area-inset-bottom) + 9rpx);
	padding-bottom: calc(env(safe-area-inset-bottom) + 9rpx);
}
</style>
