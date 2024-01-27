<template>
	<view class="min-h-screen">
		<view class="mescroll-box bg-[#F4F6F8]"
			:class="{ 'cart': config.cart.control && config.cart.event === 'cart', 'detail': !(config.cart.control && config.cart.event === 'cart') }"
			v-if="tabsData.length">
			<mescroll-body ref="mescrollRef" :down="{ use: false }" @init="mescrollInit" @up="getListFn">
				<view v-if="config.search.control" class="search-box z-10 bg-[#fff] fixed top-0 left-0 right-0">
					<input class="search-ipt text-sm" type="text" v-model="searchName" :placeholder="config.search.title">
					<view class="flex items-center h-[70rpx] absolute right-[48rpx] top-[18rpx]  z-2">
						<text class="iconfont iconxiazai17 text-[30rpx]" @click.stop="searchNameFn"></text>
					</view>
				</view>
				<!--  #ifdef  H5 -->
				<view class="tabs-box z-2 fixed left-0 bg-[#fff] bottom-[50px] top-0"
					:class="{ '!top-[106rpx]': config.search.control, 'pb-[98rpx]': config.cart.control && config.cart.event === 'cart' }">
					<scroll-view :scroll-y="true" height="100%">
						<view>
							<view class="tab-item truncate" :class="{ 'tab-item-active': index == tabActive }"
								v-for="(item, index) in tabsData" :key="item.site_id" @click="firstLevelClick(index, item)">
								<view class="text-box">
									{{ item.category_name }}
								</view>
							</view>
						</view>
					</scroll-view>
				</view>
				<!--  #endif -->
				<!--  #ifndef  H5 -->
				<view class="tabs-box z-2 fixed left-0 bg-[#fff] pb-[env(safe-area-inset-bottom)] bottom-[100rpx] top-0"
					:class="{ 'top-[106rpx]': config.search.control, '!bottom-[198rpx]': config.cart.control && config.cart.event === 'cart' }">
					<scroll-view :scroll-y="true" height="100%">
						<view>
							<view class="tab-item" :class="{ 'tab-item-active': index == tabActive }"
								v-for="(item, index) in tabsData" :key="item.site_id" @click="firstLevelClick(index, item)">
								<view class="text-box">
									{{ item.category_name }}
								</view>
							</view>
						</view>
					</scroll-view>
				</view>
				<!--  #endif -->
				<view
					class="flex justify-center items-center h-[92rpx] px-[24rpx] py-[20rpx] z-10 bg-white fixed left-[182rpx] right-0 box-border top-0"
					:class="{ '!top-[106rpx]': config.search.control }"
					v-if="tabsData[tabActive]?.child_list && tabsData[tabActive]?.child_list.length">
					<template v-if="!labelPopup">
						<scroll-view :scroll-x="true" scroll-with-animation
							:scroll-into-view="'id' + (subActive ? subActive - 1 : 0)"
							class="flex-1 scroll-Y box-border px-[24rpx] bg-white">
							<view class="flex items-center h-[48rpx]">

								<text
									class="flex-shrink-0 px-[24rpx] h-[48rpx] text-[22rpx] leading-[48rpx] border-[2rpx] box-solid rounded-[24rpx] box-border"
									:class="{ 'sub-tab-active text-color border-color': index === subActive, 'border-[#E2E2E2]': index != subActive, 'ml-[24rpx]': index }"
									v-for="(item, index) in tabsData[tabActive]?.child_list"
									:key="tabsData[tabActive].category_id" :id="'id' + index"
									@click="subMenuClick(index, item)">{{ item.category_name }}</text>

							</view>
						</scroll-view>
						<text class="iconfont iconjiantoushang text-[30rpx]" @click="labelPopup = true"></text>
						<!-- @click="showAllTabs()" -->
					</template>
					<template v-else>
						<view class="flex-1 h-[48rpx] text-[22rpx] text-[#E2E2E2] px-[24rpx] leading-[48rpx]">全部分类</view>
						<text class="iconfont iconjiantoushang transform text-[30rpx] rotate-180" @click="labelPopup = false"></text>
					</template>

				</view>
				<view class="labelPopup" :class="{ 'active': config.search.control }">
					<u-popup :show="labelPopup" mode="top" @close="labelPopup = false">
						<view class="flex flex-wrap py-[24rpx]">
							<text
								class="flex-shrink-0 w-[160rpx] ml-[22rpx] mb-[24rpx] text-center h-[48rpx] text-[22rpx] leading-[48rpx] border-[2rpx] box-solid rounded-[24rpx] box-border"
								:class="{ 'sub-tab-active text-color border-color': index === subActive, 'border-[#E2E2E2]': index != subActive }"
								v-for="(item, index) in tabsData[tabActive]?.child_list"
								:key="tabsData[tabActive].category_id" @click="subMenuClick(index, item)">{{
									item.category_name }}</text>
						</view>
					</u-popup>
				</view>
				<view class="flex justify-center flex-wrap pl-[182rpx] pt-[20rpx]"
					:class="{ '!pt-[218rpx]': config.search.control && tabsData[tabActive]?.child_list && tabsData[tabActive]?.child_list.length, 'pt-[126rpx]': config.search.control && (!tabsData[tabActive].child_list || !tabsData[tabActive].child_list.length), 'pt-[112rpx]': tabsData[tabActive]?.child_list && tabsData[tabActive]?.child_list.length && !config.search.control }">
					<template v-for="(item, index) in list" :key="item.goods_id">
						<view
							class="w-[536rpx] box-border bg-white w-full flex mx-[16rpx] px-[24rpx] py-[20rpx] rounded-[12rpx]"
							:class="{ 'mt-[16rpx]': index }" @click.stop="toLink(item.goods_id)">
							<view class="overflow-hidden mr-[8rpx] rounded-md">
								<u--image width="168rpx" height="168rpx" :src="img(item.goods_cover_thumb_small ? item.goods_cover_thumb_small : '')"
									model="aspectFill">
									<template #error>
										<u-icon name="photo" color="#999" size="50"></u-icon>
									</template>
								</u--image>
							</view>
							<view class="flex flex-1 flex-wrap">
								<view class="w-[316rpx] max-h-[72rpx] text-[26rpx] font-500 leading-[36rpx] multi-hidden">
									{{ item.goods_name }}
								</view>
								<view class="w-[316rpx] flex self-end items-end justify-between">
									<text class="text-[var(--price-text-color)] price-font">
										<text class="text-[20rpx]">￥</text>
										<text class="text-[28rpx] mr-[10rpx]">{{ item.goodsSku.price }}</text>
									</text>

									<view
										v-if="item.goodsSku.sku_spec_format === '' && cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id]['sku_' + item.goodsSku.sku_id] && config.cart.control && config.cart.event === 'cart'"
										class="flex items-center">
										<text class="text-[44rpx] text-color iconfont iconjianhao"
											@click.stop="reduceCart(cartList['goods_' + item.goods_id]['sku_' + item.goodsSku.sku_id])"></text>
										<text class="text-[#333] fext-[23rpx] font-500 mx-[16rpx]">
											{{ cartList['goods_' + item.goods_id]['sku_' + item.goodsSku.sku_id].num
											}}</text>
										<text class="text-[44rpx] text-color iconfont iconjiahao2fill"
											@click.stop="addCartBtn(cartList['goods_' + item.goods_id]['sku_' + item.goodsSku.sku_id])"></text>
									</view>
									<template v-else>
										<view v-if="config.cart.control && config.cart.style === 'style-1'"
											class=" h-[44rpx] relative">
											<view :id="'itemCart' + index"
												class="text-[#fff] bg-color h-[44rpx] text-[28rpx] px-[10px] leading-[44rpx] rounded-[22rpx]"
												@click.stop="itemCart(item.goodsSku, 'itemCart' + index)">
												{{ config.cart.text }}
											</view>
											<view
												v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
												:class="['absolute right-[-10rpx] top-[-4rpx] transform scale-75 rounded-[28rpx] h-[28rpx] min-w-[28rpx] text-center leading-[30rpx] bg-[#FF4646] text-[#fff] text-[20rpx] font-500 box-border', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">
												{{ cartList['goods_' + item.goods_id].totalNum }}
											</view>
										</view>
										<view v-if="config.cart.control && config.cart.style === 'style-2'"
											class="w-[44rpx] h-[44rpx] relative">
											<text :id="'itemCart' + index"
												class="text-color iconfont iconjiahao1 text-[44rpx]"
												@click.stop="itemCart(item.goodsSku, 'itemCart' + index)"></text>
											<view
												v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
												:class="['absolute right-[-10rpx] top-[-4rpx] transform scale-75 rounded-[28rpx] h-[28rpx] min-w-[28rpx] text-center leading-[30rpx] bg-[#FF4646] text-[#fff] text-[20rpx] font-500 box-border', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">
												{{ cartList['goods_' + item.goods_id].totalNum }}
											</view>
										</view>
										<view v-if="config.cart.control && config.cart.style === 'style-3'"
											class="w-[44rpx] h-[44rpx] relative">
											<text :id="'itemCart' + index"
												class="text-color iconfont icongouwuche !text-[44rpx]"
												@click.stop="itemCart(item.goodsSku, 'itemCart' + index)"></text>
											<view
												v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
												:class="['absolute right-[-10rpx] top-[-4rpx] transform scale-75 rounded-[28rpx] h-[28rpx] min-w-[28rpx] text-center leading-[30rpx] bg-[#FF4646] text-[#fff] text-[20rpx] font-500 box-border', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">
												{{ cartList['goods_' + item.goods_id].totalNum }}
											</view>
										</view>
										<view v-if="config.cart.control && config.cart.style === 'style-4'"
											class="w-[44rpx] h-[44rpx] relative">
											<view :id="'itemCart' + index"
												class=" flex items-center justify-center text-[#fff] bg-color h-[44rpx] w-[44rpx] rounded-[22rpx] text-center"
												@click.stop="itemCart(item.goodsSku, 'itemCart' + index)">
												<text class=" iconfont icongouwuche !text-[30rpx]"></text>
											</view>
											<view
												v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
												:class="['absolute right-[-10rpx] top-[-4rpx] transform scale-75 rounded-[28rpx] h-[28rpx] min-w-[28rpx] text-center leading-[30rpx] bg-[#FF4646] text-[#fff] text-[20rpx] font-500 box-border', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">
												{{ cartList['goods_' + item.goods_id].totalNum }}
											</view>
										</view>
									</template>
								</view>
							</view>
						</view>

					</template>
					<mescroll-empty :option="{ 'icon': img('static/resource/images/empty.png') }" v-if="!list.length && !loading && listLoading"></mescroll-empty>

				</view>

				<add-cart-popup ref="cartRef" />
			</mescroll-body>
			<!--  #ifdef  H5 -->
			<view v-if="config.cart.control && config.cart.event === 'cart'"
				class="bg-[#fff] z-2 flex justify-between items-center fixed left-0 right-0 bottom-[50px] box-solid px-[24rpx] pt-[10rpx] pb-[18rpx]">
				<view class="flex items-center">
					<view class="w-[70rpx] h-[70rpx] mr-[27rpx] relative">
						<view id="animation-end"
							class="w-[70rpx] h-[70rpx] rounded-[35rpx] bg-[var(--primary-color)] text-center leading-[70rpx]"
							@click.stop="toCart">
							<text class="icongouwuche1 iconfont text-[#fff]"></text>
						</view>
						<view v-if="totalNum"
							:class="['absolute left-[50rpx] top-[-10rpx] rounded-[28rpx] h-[28rpx] min-w-[25rpx] text-center leading-[30rpx] bg-[#FF4646] text-[#fff] text-[20rpx] font-500 box-border', totalNum > 9 ? 'px-[10rpx]' : '']">
							{{ totalNum > 99 ? "99+" : totalNum }}
						</view>
					</view>
					<text class="text-[32rpx] font-500 text-[#333]">总计：</text>
					<text class="text-[32rpx] font-500 text-[var(--price-text-color)] price-font">￥{{ totalMoney }}</text>
				</view>
				<button
					class="w-[180rpx] h-[70rpx] text-[30rpx] leading-[70rpx] text-[#fff] m-0 rounded-full bg-color remove-border"
					shape="circle" @click="settlement">去结算</button>
			</view>
			<!--  #endif -->
			<!--  #ifndef  H5 -->
			<view v-if="config.cart.control && config.cart.event === 'cart'"
				class="bg-[#fff] z-2 flex justify-between items-center fixed left-0 right-0 bottom-[100rpx] box-solid px-[24rpx] pt-[10rpx] pb-[18rpx] mb-[env(safe-area-inset-bottom)]">
				<view class="flex items-center">
					<view class="w-[70rpx] h-[70rpx] mr-[27rpx] relative">
						<view id="animation-end"
							class="w-[70rpx] h-[70rpx] rounded-[35rpx] bg-[var(--primary-color)] text-center leading-[70rpx]"
							@click.stop="toCart">
							<text class="icongouwuche1 iconfont text-[#fff]"></text>
						</view>
						<view v-if="totalNum"
							:class="['absolute left-[50rpx] top-[-10rpx] rounded-[28rpx] h-[28rpx] min-w-[25rpx] text-center leading-[30rpx] bg-[#FF4646] text-[#fff] text-[20rpx] font-500 box-border', totalNum > 9 ? 'px-[10rpx]' : '']">
							{{ totalNum > 99 ? "99+" : totalNum }}
						</view>
					</view>
					<text class="text-[32rpx] font-500 text-[#333]">总计：</text>
					<text class="text-[32rpx] font-500 text-[var(--price-text-color)] price-font">￥{{ totalMoney }}</text>
				</view>
				<button
					class="w-[180rpx] h-[70rpx] text-[30rpx] leading-[70rpx] text-[#fff] m-0 rounded-full bg-color remove-border"
					shape="circle" @click="settlement">去结算</button>
			</view>
			<!--  #endif -->
		</view>

		<view v-show="animationElStatus" :style="animationElStatus"
			class="fixed z-999 flex items-center justify-center text-[#fff] bg-color h-[44rpx] w-[44rpx] rounded-[22rpx] text-center">
			<text class=" iconfont icongouwuche !text-[30rpx]"></text>
		</view>
		<view class="flex justify-center w-[100%]" v-if="!tabsData.length && !loading">
			<mescroll-empty :option="{ 'icon': img('static/resource/images/empty.png') }"></mescroll-empty>
		</view>
		<u-loading-page bg-color="rgb(248,248,248)" :loading="loading" fontSize="16" color="#333"></u-loading-page>
		<tabbar addon="shop"/>
	</view>
</template>

<script setup lang="ts">
import { ref, onMounted, toRaw, computed, getCurrentInstance } from 'vue';
import { t } from '@/locale';
import { img, redirect } from '@/utils/common';
import { getGoodsCategoryTree, getGoodsPages } from '@/addon/shop/api/goods';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import addCartPopup from './add-cart-popup.vue'
import { onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import { useLogin } from '@/hooks/useLogin'
import useMemberStore from '@/stores/member'
import useCartStore from '@/addon/shop/stores/cart'

const instance = getCurrentInstance(); // 获取组件实例
const cartStore = useCartStore();
// 查询购物车列表
cartStore.getList();

const cartList = computed(() => cartStore.cartList)
const totalNum = computed(() => cartStore.totalNum)
const totalMoney = computed(() => cartStore.totalMoney)
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)
const { mescrollInit, getMescroll } = useMescroll(onPageScroll, onReachBottom);
const prop = defineProps({
	config: {
		type: Object,
		default: (() => { return {} })
	}
})
let config = prop.config
let list = ref<Array<Object>>([]);
let searchName = ref("");
let category_id = ref("");
let loading = ref<boolean>(true);//页面加载动画
let listLoading = ref<boolean>(false);//列表加载动画
let labelPopup = ref<boolean>(false)
let cartData = ref<Array<any>>([])
cartData.value = uni.getStorageSync('shopCart') || []
interface acceptingDataStructure {
	data: acceptingDataItemStructure,
	msg: string,
	code: number
}
interface acceptingDataItemStructure {
	data: object,
	[propName: string]: number | string | object
}
interface mescrollStructure {
	num: number,
	size: number,
	endSuccess: Function,
	[propName: string]: any
}
const getListFn = (mescroll: mescrollStructure) => {
	// loading.value = true;
	listLoading.value = false
	getGoodsPages({
		page: mescroll.num,
		limit: mescroll.size,
		keyword: '', // 搜索关键词
		goods_category: category_id.value, // 商品分类id
		brand_id: '', // 品牌id
		label_ids: '', // 标签id
		start_price: '', // 价格开始区间
		end_price: '', // 价格结束区间
		order: 'price', // 排序方式（综合：空，销量：sale_num，价格：price）
		sort: 'desc' // 升序：asc，降序：desc
	}).then((res: acceptingDataStructure) => {
		let newArr = res.data.data
		//设置列表数据
		if (mescroll.num == 1) {
			list.value = []; //如果是第一页需手动制空列表
		}
		list.value = list.value.concat(newArr);
		loading.value = false;
		mescroll.endSuccess(newArr.length);
		if (!list.value.length) listLoading.value = true
	}).catch(() => {
		loading.value = false;
		listLoading.value = true
		mescroll.endErr(); // 请求失败, 结束加载
	})
}
const toLink = (goods_id: string) => {
	redirect({ url: '/addon/shop/pages/goods/detail', param: { goods_id } })
}

onMounted(() => {
	getCategoryData()
})
//设置购物车动画
const animationElStatus = ref('')
const animationAddCart = (row: any, id: any) => {
	// #ifdef  MP-WEIXIN
	uni.createSelectorQuery().in(instance).select('#animation-end').boundingClientRect((res) => {
		uni.createSelectorQuery().in(instance).select('#' + id).boundingClientRect((position) => {
			animationElStatus.value = `top: ${position.top}px; left: ${position.left}px;`
			setTimeout(() => {
				animationElStatus.value = `top: ${res.top}px; left: ${res.left}px; transition: all 1s; transform: rotate(-720deg);`
			}, 20);

			setTimeout(() => {
				animationElStatus.value = ''
				cartStore.increase({
					goods_id: row.goods_id,
					sku_id: row.sku_id,
					sale_price: row.sale_price,
					stock: row.stock
				});
			}, 1020);

		}).exec()

	}).exec()
	// #endif
	// #ifdef  H5
	const animationEnd = window.document.getElementById('animation-end');
	const animationEndLeft = animationEnd.getBoundingClientRect().left;
	const animationEndTop = animationEnd.getBoundingClientRect().top;
	const itemCart = window.document.getElementById(id);
	const itemCartLeft = itemCart.getBoundingClientRect().left;
	const itemCartTop = itemCart.getBoundingClientRect().top;
	animationElStatus.value = `top: ${itemCartTop}px; left: ${itemCartLeft}px;`
	setTimeout(() => {
		animationElStatus.value = `top: ${animationEndTop}px; left: ${animationEndLeft}px; transition: all 1s; transform: rotate(-720deg);`
	}, 20);

	setTimeout(() => {
		animationElStatus.value = ''
		cartStore.increase({
			goods_id: row.goods_id,
			sku_id: row.sku_id,
			sale_price: row.sale_price,
			stock: row.stock
		});
	}, 1020);
	// #endif
}
//获取购物车数据
/**
 * @description 获取分类数据
 * */
const tabsData = ref<Array<Object>>([])
const getCategoryData = () => {
	loading.value = true;
	getGoodsCategoryTree().then((res: any) => {
		tabsData.value = res.data;
		category_id.value = res.data[0].category_id;
		loading.value = false;
	}).catch(() => {
		loading.value = false;
	});
}

// 一级菜单样式控制
const tabActive = ref<number>(0)
// 二级菜单样式控制
const subActive = ref<number>(0)

// 一级菜单点击事件
const firstLevelClick = (index: number, data: Object) => {
	tabActive.value = index;
	if (data.child_list && data.child_list.length) {
		subMenuClick(0, data.child_list[0]);
	} else {
		category_id.value = data.category_id;
		getMescroll().resetUpScroll();
	}
}

/**
 * @description 二级菜单点击事件
 * @param {number} index
 * */
const subMenuClick = (index: number, data: object) => {
	subActive.value = index;
	category_id.value = data.category_id;
	labelPopup.value = false
	getMescroll().resetUpScroll();
}
// 搜索名字
const searchNameFn = () => {
	// getMescroll().resetUpScroll();
	redirect({ url: '/addon/shop/pages/goods/list', param: { goods_name: searchName.value } })
}
//点击商品购物车按钮
let cartRef = ref()
const itemCart = (row: any, id: any) => {
	if (config.cart.event !== 'cart') {
		return toLink(row.goods_id)
	}
	if (!userInfo.value) {
		useLogin().setLoginBack({ url: '/addon/shop/pages/goods/category' })
		return false
	}
	if (row.sku_spec_format) {
		cartRef.value.open(row.sku_id)
	} else {
		//单规格添加购物车
		animationAddCart(row, id)

	}
}
//点击购物车加号
const addCartBtn = (row: any) => {

	// 购物车添加数量
	cartStore.increase({
		id: row.id,
		goods_id: row.goods_id,
		sku_id: row.sku_id,
		stock: row.stock,
		sale_price: row.sale_price,
		num: row.num
	});

}
//点击购物车减号
const reduceCart = (row: any) => {

	cartStore.reduce({
		id: row.id,
		goods_id: row.goods_id,
		sku_id: row.sku_id,
		stock: row.stock,
		sale_price: row.sale_price,
		num: row.num
	});
}

//进入购物车
const toCart = () => {
	redirect({ url: '/addon/shop/pages/goods/cart' })
}
/**
	 * 结算
	 */
const settlement = () => {
	if (!totalNum.value) {
		uni.showToast({ title: '还没有选择商品', icon: 'none' })
		return
	}
	const ids = []
	Object.values(cartList.value).forEach(item => {
		Object.keys(item).forEach(v => {
			if (v != 'totalNum' && v != 'totalMoney') ids.push(item[v].id)
		})

	})
	uni.setStorage({
		key: 'orderCreateData',
		data: {
			cart_ids: ids
		},
		success() {
			redirect({ url: '/addon/shop/pages/order/payment' })
		}
	})
}
</script>

<style lang="scss" scoped>
.border-color {
	border-color: var(--primary-color) !important;
}

.text-color {
	color: var(--primary-color) !important;
}

.bg-color {
	background-color: var(--primary-color) !important;
}

.class-select {
	position: relative;
	font-weight: bold;

	&::after {
		content: "";
		position: absolute;
		bottom: 0;
		height: 6rpx;
		background-color: $u-primary;
		width: 90%;
		left: 50%;
		transform: translateX(-50%);
	}
}

.list-select {
	position: relative;
	margin-right: 28rpx;

	&::after {
		content: "";
		position: absolute;
		background-color: #999;
		width: 2rpx;
		height: 70%;
		top: 50%;
		right: -14rpx;
		transform: translatey(-50%);
	}
}

.transform-rotate {
	transform: rotate(180deg);
}

.font-scale {
	transform: scale(0.75);
}

.text-color {
	color: $u-primary;
}

.bg-color {
	background-color: $u-primary;
}

// search input
.search-box {
	// position: relative;
	padding: 20rpx 24rpx;
}

.search-box .search-ipt {
	height: 66rpx;
	background-color: #F6F8F8;
	padding-left: 20rpx;
	border-radius: 33rpx;
}

.search-box .search-ipt .input-placeholder {
	padding-left: 10rpx;
	color: #A5A6A6;
}

.tabs-box {
	width: 182rpx;
	font-size: 28rpx;
}

.tabs-box .tab-item {
	height: 92rpx;
	text-align: center;
	line-height: 92rpx;


}

.tabs-box .tab-item-active {
	position: relative;
	color: var(--primary-color);

	&::before {
		display: inline-block;
		position: absolute;
		left: 0rpx;
		top: 50%;
		transform: translateY(-50%);
		content: '';
		width: 8rpx;
		height: 34rpx;
		background-color: var(--primary-color);
		border-radius: 0rpx 5rpx 5rpx 0rpx;
	}

	&::after {
		display: inline-block;
		position: absolute;
		left: 0rpx;
		top: 50%;
		transform: translateY(-50%);
		content: '';
		width: 8rpx;
		height: 34rpx;
		background-color: var(--primary-color);
		border-radius: 0rpx 5rpx 5rpx 0rpx;
	}
}

/*  #ifdef  H5  */

.category .labelPopup :deep(.u-transition) {
	top: 92rpx !important;
	left: 182rpx !important;
	z-index: 8 !important;
}

.category .labelPopup.active :deep(.u-transition) {
	top: 198rpx !important;
}

/*  #endif  */
</style>
