<template>
	<view :style="warpCss">
		<div class="flex flex-wrap justify-between">
			<block v-if="diyComponent.style == 'style1'">
				<view :class="['w-[332rpx] bg-[#fff] box-border rounded-[12rpx] overflow-hidden',{'mt-[24rpx]': index > 1}]" v-for="(item,index) in goodsList" :key="item.goods_id" @click="toLink(item)">
					<u--image width="332rpx" height="332rpx" :src="img(item.goods_cover_thumb_small || '')" model="aspectFill">
						<template #error>
							<u-icon name="photo" color="#999" size="50"></u-icon>
						</template>
					</u--image>
					<view class="px-[16rpx] mt-[18rpx] h-[80rpx] leading-[40rpx] text-[28rpx] multi-hidden">
						{{item.goods_name}}
					</view>
					<view class="px-[16rpx] pb-[20rpx] flex justify-between items-end mt-[12rpx]" >
						<text class="text-[28rpx] text-[var(--price-text-color)] price-font">￥{{item.goodsSku.price}}</text>
						<text class="text-[22rpx] text-[#888] leading-[31rpx]">已售{{item.sale_num}}{{item.unit || '件'}}</text>
					</view>
				</view>
			</block>
			<block v-if="diyComponent.style == 'style2'">
				<view :class="['bg-white w-full flex p-[20rpx] rounded-[16rpx]',{'mt-[20rpx]': index > 0}]"  v-for="(item,index) in goodsList" :key="item.goods_id" @click="toLink(item)">
					<u--image class="rounded-[10rpx] overflow-hidden" width="200rpx" height="200rpx" :src="img(item.goods_cover_thumb_small || '')" model="aspectFill">
						<template #error>
							<u-icon name="photo" color="#999" size="50"></u-icon>
						</template>
					</u--image>
					<view class="flex-1 flex flex-col ml-[20rpx]">
						<view class="text-[26rpx] h-[80rpx] leading-[40rpx]  multi-hidden mb-[10rpx]">
							{{item.goods_name}}
						</view>
						<view class="mt-auto flex justify-between items-end">
							<text class="text-[28rpx] text-[var(--price-text-color)] price-font">￥{{item.goodsSku.price}}</text>
							<text class="text--[24rpx] text-[#666]">已售{{item.sale_num}}{{item.unit || '件'}}</text>
						</view>
					</view>
				</view>
			</block>
		</div>
	</view>
</template>

<script setup lang="ts">
	// 商品列表
	import { ref, computed, watch, onMounted } from 'vue';
	import { redirect, img } from '@/utils/common';
	import useDiyStore from '@/app/stores/diy';
	import { getGoodsComponents } from '@/addon/shop/api/goods';

	const props = defineProps(['component', 'index', 'pullDownRefreshCount']);
	const diyStore = useDiyStore();

	const goodsList = ref<Array<any>>([]);

	const diyComponent = computed(() => {
		if (diyStore.mode == 'decorate') {
			return diyStore.value[props.index];
		} else {
			return props.component;
		}
	})

	const warpCss = computed(() => {
		var style = '';
		if (diyComponent.value.componentBgColor) style += 'background-color:' + diyComponent.value.componentBgColor + ';';
		if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
		if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
		if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
		if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
		return style;
	})

	watch(
		() => props.pullDownRefreshCount,
		(newValue, oldValue) => {
			// 处理下拉刷新业务
		}
	)

	const getGoodsListFn = () => {
		let data = {
			num: diyComponent.value.source == 'all' ? diyComponent.value.num : '',
			goods_ids: diyComponent.value.source == 'custom' ? diyComponent.value.goods_ids : '',
			goods_category: diyComponent.value.source == 'category' ? diyComponent.value.goods_category : ''
		}
		getGoodsComponents(data).then((res) => {
			goodsList.value = res.data;
		});
	}

	onMounted(() => {
		refresh();
		// 装修模式下刷新
		if (diyStore.mode == 'decorate') {
			watch(
				() => diyComponent.value,
				(newValue, oldValue) => {
					if (newValue && newValue.componentName == 'AddonList') {
						refresh();
					}
				}
			)
		}
	});

	const refresh = () => {
		// 装修模式下设置默认图
		if (diyStore.mode == 'decorate') {
			let obj = {
				goods_cover: "",
				goods_name: "商品名称",
				sale_num: "100",
				unit: "件",
				goodsSku:{price:100}
			};
			goodsList.value.push(obj);
			goodsList.value.push(obj);
		}else{
			getGoodsListFn();
		}
	}

	const toLink = (data) => {
		if (diyStore.mode == 'decorate') return false;
		redirect({ url: '/addon/shop/pages/goods/detail', param: { goods_id: data.goods_id } })
	}
</script>

<style lang="scss" scoped>
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
</style>
<style>
	@import '@/addon/shop/styles/common.scss';
</style>
