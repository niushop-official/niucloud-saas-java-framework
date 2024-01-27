<template>
	<view :style="warpCss" class="overflow-hidden flex items-center pl-[24rpx]">
		<image :src="img('addon/shop/diy/search_01.png')" class="w-[40rpx] h-[40rpx]" mode="widthFix" @click="toLink('/addon/shop/pages/goods/category')"></image>
		<view class="flex-1 ml-[24rpx] rounded-[32rpx] flex items-center bg-[#F7F8FD] opacity-90 py-[18rpx] pl-[38rpx] pr-[32rpx] justify-between" @click="toLink('/addon/shop/pages/goods/list')">
			<text class="text-[#6D7278] text-[26rpx]">请输入搜索关键词</text>
			<text class="iconfont iconxiazai17"></text>
		</view>
	</view>
</template>

<script setup lang="ts">
	// 搜索
	import { ref, computed, watch, onMounted } from 'vue';
	import { img, redirect } from '@/utils/common';
	import useDiyStore from '@/app/stores/diy';

	const props = defineProps(['component', 'index', 'pullDownRefreshCount']);
	const diyStore = useDiyStore();

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

		}
	}

	const toLink = (url)=>{
		if (diyStore.mode == 'decorate') return false;
		redirect({ url: url})
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
