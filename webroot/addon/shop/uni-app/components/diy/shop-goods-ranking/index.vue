<template>
	<view :style="warpCss">
		<scroll-view scroll-x="true" class="w-[100%] whitespace-nowrap">
			<view class="inline-block overflow-hidden w-[480rpx] px-[20rpx] pb-[20rpx] box-border" v-for="(item,index) in diyComponent.list" :key="item.id" :class="{'pt-[30rpx]': item.title.icon || item.title.img, 'pt-[10rpx]': !item.title.icon && !item.title.img,'mr-[20rpx]': (diyComponent.list.length-1) != index}" :style="swiperItemCss(item)">
				<view class="flex items-center">
					<image class="w-[30rpx] h-[30rpx] mr-[10rpx]" v-if="item.title.icon" :src="img(item.title.icon)" mode="aspectFill"></image>
					<image class="h-[34rpx] w-[auto]" v-if="item.title.img" :src="img(item.title.img)" mode="heightFix"></image>
				</view>
				<view class="mb-[6rpx]" v-if="item.subTitle.text" :style="{'color': item.subTitle.textColor}" @click="diyStore.toRedirect(item.subTitle.link)">
					<text class="text-[24rpx]">{{item.subTitle.text}}</text>
					<text class="iconfont iconyouV6xx !text-[24rpx]"></text>
				</view>
				<view>
					<view class="flex bg-[#fff] p-[10rpx] rounded-[16rpx] mb-[16rpx]">
						<view class="relative w-[130rpx] h-[130rpx] mr-[16rpx]">
							<text class="absolute z-5 w-[30rpx] h-[32rpx] left-[10rpx] top-[2rpx]"  :style="{'background-image':'url(' + img('addon/shop/diy/shop_goods_ranking/top_01.png') + ')','background-size':'100%','background-repeat':'no-repeat'}"></text>
							<u--image radius="var(--goods-rounded-big)" width="130rpx" height="130rpx" :src="img(item.goods_cover_thumb_mid || '')" model="aspectFill">
								<template #error>
									<image class="w-[130rpx] h-[130rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
								</template>
							</u--image>
						</view>
						<view class="flex flex-col">
							<view class="leading-[1.3] multi-hidden w-[270rpx] text-[28rpx] whitespace-normal">十月结晶第三代爽肤水示意文字内容展示十月结晶第三代爽肤水示意文字内容展示</view>
							<view class="flex items-center justify-between mt-[auto]">
								<view class="flex items-inherit text-[red]">
									<text class="text-[20rpx] mt-[4rpx] mr-[2rpx]">￥</text>
									<text class="text-[30rpx] font-[500]">68</text>
								</view>
								<text class="iconfont icongouwuche3 text-[var(--primary-color)] border-[2rpx] border-solid border-[var(--primary-color)] rounded-[50%] text-[22rpx] p-[6rpx]"></text>
							</view>
						</view>
					</view>
					<view class="flex bg-[#fff] p-[10rpx] rounded-[16rpx] mb-[16rpx]">
						<view class="relative w-[130rpx] h-[130rpx] mr-[16rpx]">
							<text class="absolute z-5 w-[30rpx] h-[32rpx] left-[10rpx] top-[2rpx]"  :style="{'background-image':'url(' + img('addon/shop/diy/shop_goods_ranking/top_02.png') + ')','background-size':'100%','background-repeat':'no-repeat'}"></text>
							<u--image radius="var(--goods-rounded-big)" width="130rpx" height="130rpx" :src="img(item.goods_cover_thumb_mid || '')" model="aspectFill">
								<template #error>
									<image class="w-[130rpx] h-[130rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
								</template>
							</u--image>
						</view>
						<view class="flex flex-col">
							<view class="leading-[1.3] multi-hidden w-[270rpx] text-[28rpx] whitespace-normal">十月结晶第三代爽肤水示意文字内容展示十月结晶第三代爽肤水示意文字内容展示</view>
							<view class="flex items-center justify-between mt-[auto]">
								<view class="flex items-inherit text-[red]">
									<text class="text-[20rpx] mt-[4rpx] mr-[2rpx]">￥</text>
									<text class="text-[30rpx] font-[500]">68</text>
								</view>
								<text class="iconfont icongouwuche3 text-[var(--primary-color)] border-[2rpx] border-solid border-[var(--primary-color)] rounded-[50%] text-[22rpx] p-[6rpx]"></text>
							</view>
						</view>
					</view>
					<view class="flex bg-[#fff] p-[10rpx] rounded-[16rpx]">
						<view class="relative w-[130rpx] h-[130rpx] mr-[16rpx]">
							<text class="absolute z-5 w-[30rpx] h-[32rpx] left-[10rpx] top-[2rpx]"  :style="{'background-image':'url(' + img('addon/shop/diy/shop_goods_ranking/top_03.png') + ')','background-size':'100%','background-repeat':'no-repeat'}"></text>
							<u--image radius="var(--goods-rounded-big)" width="130rpx" height="130rpx" :src="img(item.goods_cover_thumb_mid || '')" model="aspectFill">
								<template #error>
									<image class="w-[130rpx] h-[130rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
								</template>
							</u--image>
						</view>
						<view class="flex flex-col">
							<view class="leading-[1.3] multi-hidden w-[270rpx] text-[28rpx] whitespace-normal">十月结晶第三代爽肤水示意文字内容展示十月结晶第三代爽肤水示意文字内容展示</view>
							<view class="flex items-center justify-between mt-[auto]">
								<view class="flex items-inherit text-[red]">
									<text class="text-[20rpx] mt-[4rpx] mr-[2rpx]">￥</text>
									<text class="text-[30rpx] font-[500]">68</text>
								</view>
								<text class="iconfont icongouwuche3 text-[var(--primary-color)] border-[2rpx] border-solid border-[var(--primary-color)] rounded-[50%] text-[22rpx] p-[6rpx]"></text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script lang="ts" setup>
	// 排行榜
    import { ref,computed, watch, onMounted, nextTick,getCurrentInstance } from 'vue';
	import useDiyStore from '@/app/stores/diy';
    import { img } from '@/utils/common';

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
        style += 'position:relative;';
        if(diyComponent.value.componentStartBgColor) {
            if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${diyComponent.value.componentGradientAngle},${diyComponent.value.componentStartBgColor},${diyComponent.value.componentEndBgColor});`;
            else style += 'background-color:' + diyComponent.value.componentStartBgColor + ';';
        }

        if(diyComponent.value.componentBgUrl) {
            style += `background-image:url('${ img(diyComponent.value.componentBgUrl) }');`;
            style += 'background-size: cover;background-repeat: no-repeat;';
        }
		
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
	                if (newValue && newValue.componentName == 'shopGoodsRanking') {
	                    refresh();
	                }
	            }
	        )
	    }
	});
	
	const refresh = ()=> {}
	
	const swiperItemCss = (data: any)=>{
		var style = '';
		if(data.listFrame){
			style += 'background: linear-gradient(90deg, '+ data.listFrame.startColor + ', '+ data.listFrame.endColor + ');';
		}
		if (data.imageUrl){
			style += 'background-image:' + 'url('+img(data.imageUrl)+');';
			style += 'background-size: cover;';
			style += 'background-repeat: no-repeat;';
		}
		
		if (diyComponent.value.topRankingRounded) style += 'border-top-left-radius:' + diyComponent.value.topRankingRounded * 2 + 'rpx;';
		if (diyComponent.value.topRankingRounded) style += 'border-top-right-radius:' + diyComponent.value.topRankingRounded * 2 + 'rpx;';
		if (diyComponent.value.bottomRankingRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRankingRounded * 2 + 'rpx;';
		if (diyComponent.value.bottomRankingRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRankingRounded * 2 + 'rpx;';
		return style;
	}
</script>

<style lang="scss" scoped>
</style>
