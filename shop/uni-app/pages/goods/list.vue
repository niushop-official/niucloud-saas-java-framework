<template>
	<view class="bg-gray-100 min-h-[100vh]">
		<view class="fixed left-0 right-0 top-0 product-warp bg-[#fff] px-[24rpx]">
			<view class="flex items-center h-[106rpx] box-border py-[24rpx]">
				<view class="bg-[#F5F5F5]  flex items-center justify-between h-[66rpx] rounded-[33rpx] flex-1 pl-[20rpx] mr-[40rpx]">
					<input class="uni-input text-sm flex-1" maxlength="50" v-model="goods_name" @confirm="searchTypeFn('all')" placeholder="请搜索您想要的商品" />
					<text class="iconfont iconxiazai17 text-[30rpx] mr-[18rpx]" @click="searchTypeFn('all')"></text>
				</view>
				<text :class="['iconfont text-[44rpx]', listType ? 'iconshangpinliebiao' : 'iconliebiaoxingshi']" @click="listIconBtn"></text>
			</view>
			<view class="pb-3 pt-1 flex items-center justify-between">
				<text :class="['text-sm', { 'text-color': searchType == 'all' }]" @click="searchTypeFn('all')">综合</text>
				<view class="flex items-center" :class="[{ 'text-color': searchType == 'sale_num' }]" @click="searchTypeFn('sale_num')">
					<text class="text-sm mr-[4rpx]">销量</text>
					<text v-if="sale_num == 'asc'" class="text-xs iconfont iconjiantoushang font-bold"></text>
					<text v-else class="text-xs iconfont iconxialajiantouxiao"></text>
				</view>
				<view class="flex items-center" :class="[{ 'text-color': searchType == 'price' }]" @click="searchTypeFn('price')">
					<text class="text-sm mr-[4rpx]">价格</text>
					<text v-if="price == 'asc'" class="text-xs iconfont iconjiantoushang font-bold"></text>
					<text v-else class="text-xs iconfont iconxialajiantouxiao"></text>
				</view>
				<view class="flex items-center" :class="[{ 'text-color': searchType == 'label' }]" @click="searchTypeFn('label')">
					<text class="text-sm mr-[2rpx]">筛选</text>
					<text class="iconfont iconshaixuan"></text>
				</view>
			</view>
		</view>
		<u-popup :show="labelPopup" mode="top" @close="labelPopup = false">
			<view class="text-sm font-bold px-[30rpx] mt-3">全部分类</view>
			<view class="flex flex-wrap pl-[30rpx] pt-[30rpx]">
				<text @click="loadCategory(item.category_id)" v-for="(item, index) in categoryList" :key="item.category_id"
					:class="['px-[26rpx] border-[2rpx] border-solid border-transparent h-[60rpx] mr-[30rpx] mb-[30rpx] flex items-center justify-center bg-[#F4F4F4] rounded-[8rpx] text-xs', { 'label-select': currGoodsCategory == item.category_id }]">
					{{ item.category_name }}
				</text>
			</view>
		</u-popup>

		<mescroll-body ref="mescrollRef" top="174rpx" bottom="50px" @init="mescrollInit" @down="downCallback" @up="getAllAppListFn">
			<view :class="['p-[24rpx] !pb-0', !listType ? 'flex justify-between flex-wrap' : '']">
				<template v-for="(item, index) in articleList">
					<template v-if="listType" :key="item.app_id">
						<view class="bg-white flex p-[20rpx] rounded-[16rpx]" :class="{ 'mt-[20rpx]': index }"
							@click="toDetail(item.goods_id)">
							<u--image class="rounded-[10rpx] overflow-hidden" width="200rpx" height="200rpx"
								:src="img(item.goods_cover_thumb_small ? item.goods_cover_thumb_small : '')"
								model="aspectFill">
								<template #error>
									<u-icon name="photo" color="#999" size="50"></u-icon>
								</template>
							</u--image>
							<view class="flex-1 flex flex-col ml-[20rpx]">
								<view class="text-[26rpx] font-500 h-[80rpx] leading-[40rpx]  multi-hidden mb-[10rpx]">
									{{ item.goods_name }}
								</view>
								<view class="mt-auto flex justify-between items-end">
									<view class="flex flex-col">
										<text class="text-[28rpx] text-[var(--price-text-color)] price-font">￥{{ item.goodsSku.price }}</text>
									</view>
									<text class="text--[24rpx] text-[#666]">已售{{ item.sale_num }}{{ item.unit }}</text>
								</view>
							</view>
						</view>
					</template>
					<template v-else>
						<view class="w-[342rpx] bg-[#fff] box-border rounded-[10rpx] overflow-hidden mt-[20rpx]" @click="toDetail(item.goods_id)">
							<u--image width="342rpx" height="342rpx" :src="img(item.goods_cover_thumb_small ? item.goods_cover_thumb_small : '')" model="aspectFill">
								<template #error>
									<u-icon name="photo" color="#999" size="50"></u-icon>
								</template>
							</u--image>
							<view class="pl-[22rpx] pr-[30rpx] mt-[18rpx] h-[80rpx] leading-[40rpx] text-[26rpx] font-500 multi-hidden">
								{{ item.goods_name }}
							</view>
							<view class="pl-[22rpx] pb-[20rpx] pr-[30rpx] flex justify-between items-end mt-[12rpx]">
								<view class="flex justify-between items-end">
									<text class="text-[28rpx] text-[var(--price-text-color)] price-font">￥{{ item.goodsSku.price }}</text>
									<!-- <text class="text-[28rpx] font-bold text-[#FF3223]"
									v-if="!parseFloat(item.price) && parseFloat(item.balance)">{{item.balance}}牛币</text>
								<text class="text-[24rpx] ml-[6rpx]"
									v-if="parseFloat(item.price) && parseFloat(item.balance) || !parseFloat(item.price) && !parseFloat(item.balance)">{{item.balance}}牛币</text> -->
								</view>
								<text class="text--[24rpx] text-[#666] leading-[31rpx]">已售{{ item.sale_num }}{{ item.unit}}</text>
							</view>
						</view>
					</template>
				</template>
			</view>
			<mescroll-empty v-if="!articleList.length && loading"></mescroll-empty>
		</mescroll-body>

		<tabbar addon="shop"/>
	</view>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { t } from '@/locale'
import { redirect, img } from '@/utils/common';
import { getGoodsCategoryTree, getGoodsPages } from '@/addon/shop/api/goods';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import { useShare } from '@/hooks/useShare'

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

const { setShare, onShareAppMessage, onShareTimeline } = useShare()
setShare()
onShareAppMessage()
onShareTimeline()

let categoryList = ref<Array<Object>>([]);
let articleList = ref<Array<any>>([]);
let coupon_id = ref<number | string>('');
let currGoodsCategory = ref<number | string>('');
let mescrollRef = ref(null);
let loading = ref<boolean>(false);
// 标签
let labelPopup = ref(false);
let goods_name = ref("");
let price = ref("asc");
let sale_num = ref("asc");
let searchType = ref('all');
//列表类型
let listType = ref(true)
onLoad(async (option) => {
	currGoodsCategory.value = option.curr_goods_category || ''
	goods_name.value = option.goods_name || ''
	coupon_id.value = option.coupon_id || ''
	await getGoodsCategoryTree().then((res: any) => {
		const initData = { category_name: "全部", category_id: '' };
		categoryList.value.push(initData);
		categoryList.value = categoryList.value.concat(res.data);
	});
})

interface mescrollStructure {
	num: number,
	size: number,
	endSuccess: Function,
	[propName: string]: any
}
const getAllAppListFn = (mescroll: mescrollStructure) => {
	loading.value = false;
	let data: object = {
		goods_category: currGoodsCategory.value,
		page: mescroll.num,
		limit: mescroll.size,
		keyword: goods_name.value,
		coupon_id: coupon_id.value,
		order: searchType.value === 'all' ? '' : searchType.value,
		sort: searchType.value == 'price' ? price.value : sale_num.value
	};

	getGoodsPages(data).then((res: any) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (Number(mescroll.num) === 1) {
			articleList.value = []; //如果是第一页需手动制空列表
		}
		articleList.value = articleList.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}

const loadCategory = (id: string) => {
	currGoodsCategory.value = id;
	articleList.value = [];
	getMescroll().resetUpScroll();
	labelPopup.value = false;
}
// 搜索
const searchTypeFn = (type) => {
	if (type == searchType.value && type == 'price') {
		sale_num.value = 'asc';
		price.value = price.value == 'asc' ? 'desc' : 'asc';
	}
	if (type == searchType.value && type == 'sale_num') {
		price.value = 'asc';
		sale_num.value = sale_num.value == 'asc' ? 'desc' : 'asc';
	}
	searchType.value = type;
	if (type == 'label') {
		sale_num.value = 'asc';
		price.value = 'asc';
		labelPopup.value = true;
	} else {
		labelPopup.value = false;
		articleList.value = [];
		getMescroll().resetUpScroll();
	}
}
//列表样式切换
const listIconBtn = () => {
	listType.value = !listType.value
}
const toDetail = (id: string | number) => {
	redirect({ url: '/addon/shop/pages/goods/detail', param: { goods_id: id }, mode: 'navigateTo' })
}
onMounted(() => {
	setTimeout(() => {
		getMescroll().optUp.textNoMore = t("end");
	}, 500)
});
</script>

<style lang="scss" scoped>
.nav-item.active {
	color: $u-primary;
}

.scroll-view-wrap {
	word-break: keep-all;
}

/* 单行超出隐藏 */
.using-hidden {
	word-break: break-all;
	text-overflow: ellipsis;
	overflow: hidden;
	display: -webkit-box;
	-webkit-line-clamp: 1;
	-webkit-box-orient: vertical;
	white-space: break-spaces;
}

/* 多行超出隐藏 */
.multi-hidden {
	word-break: break-all;
	text-overflow: ellipsis;
	overflow: hidden;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.label-chunk {
	color: var(--primary-color);
	background-color: var(--primary-color-light);
}

.text-color {
	color: var(--primary-color);
}

.label-select {
	color: var(--primary-color);
	border-color: var(--primary-color);
	background-color: var(--primary-color-light);
}

:deep(.u-popup .u-transition) {
	top: 174rpx !important;
}

.product-warp {
	z-index: 99999;
}

:deep(.tab-bar-placeholder) {
	display: none !important;
}

:deep(.u-tabbar__placeholder) {
	display: none !important;
}
</style>
<style>
@import '@/addon/shop/styles/common.scss';
</style>
