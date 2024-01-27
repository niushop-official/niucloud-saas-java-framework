<template>
	<view class="bg-[#f8f8f8] min-h-screen">
		<view class="mescroll-box" v-if="tabsData.length">
			<view v-if="config.search.control" class="search-box z-2 bg-[#fff] fixed top-0 left-0 right-0">
				<input class="search-ipt text-sm" type="text" v-model="searchName" :placeholder="config.search.title">
				<view class="flex items-center z-2 h-[70rpx] absolute right-[48rpx] top-[18rpx]">
					<text class="iconfont iconxiazai17 text-[30rpx]" @click="searchNameFn"></text>
				</view>
			</view>
			<view class="tabs-box z-2 fixed left-0 bg-[#fff] bottom-[50px] top-0" :class="{ '!top-[106rpx]': config.search.control }">
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
			<scroll-view :scroll-y="true">
				<view class="flex flex-wrap pl-[182rpx] pb-[70rpx] pt-[20rpx]" :class="{ '!pt-[126rpx]': config.search.control }">

					<template v-for="(item, index) in tabsData[tabActive]?.child_list" :key="item.category_id">
						<view class="w-[160rpx] ml-[22rpx] mb-[24rpx]" @click="toLink(item.category_id)">
							<u--image width="160rpx" height="160rpx" :src="img(item.image ? item.image : '')" model="aspectFill">
								<template #error>
									<u-icon name="photo" color="#999" size="50"></u-icon>
								</template>
							</u--image>
							<view class="text-[28rpx] text-center mt-[12rpx]">{{ item.category_name }}</view>
						</view>
					</template>
					<view class="flex justify-center w-[100%]" v-if="!tabsData[tabActive]?.child_list && !loading">
						<mescroll-empty :option="{ 'icon': img('static/resource/images/empty.png') }"></mescroll-empty>
					</view>

				</view>
			</scroll-view>
		</view>
		<tabbar addon="shop"/>
		<view class="flex justify-center w-[100%]" v-if="!tabsData.length && !loading">
			<mescroll-empty :option="{ 'icon': img('static/resource/images/empty.png') }"></mescroll-empty>
		</view>
		<u-loading-page bg-color="rgb(248,248,248)" :loading="loading" fontSize="16" color="#333"></u-loading-page>
	</view>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue';
import { img, redirect } from '@/utils/common';
import { getGoodsCategoryTree } from '@/addon/shop/api/goods';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import { t } from '@/locale';

const prop = defineProps({
	config: {
		type: Object,
		default: (() => { return {} })
	}
})
let config = prop.config
let searchName = ref("");
let loading = ref<boolean>(true);

onMounted(() => {
	getCategoryData()
})
/**
 * @description 获取分类数据
 * */
const tabsData = ref<Array<Object>>([])
const getCategoryData = () => {
	loading.value = true;
	getGoodsCategoryTree().then((res: any) => {
		tabsData.value = res.data;
		loading.value = false;
	}).catch(() => {
		loading.value = false;
	});
}

// 一级菜单样式控制
const tabActive = ref<number>(0)

// 一级菜单点击事件
const firstLevelClick = (index: number, data: Object) => {
	tabActive.value = index;

}
const toLink = (curr_goods_category: string) => {
	redirect({ url: '/addon/shop/pages/goods/list', param: { curr_goods_category } })
}
// 搜索名字
const searchNameFn = () => {
	// getMescroll().resetUpScroll();
	redirect({ url: '/addon/shop/pages/goods/list', param: { goods_name: searchName.value } })
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

$white-bj: #fff;

.mescroll-box {
	height: 100vh;
}

.panic-buying {
	background-color: var(--primary-color);
	color: $white-bj;
}

:deep(.mescroll-upwarp) {
	box-sizing: border-box;
	padding-left: 182rpx;
}

:deep(.tab-bar-placeholder) {
	display: none !important;
}

:deep(.u-tabbar__placeholder) {
	display: none !important;
}
</style>
