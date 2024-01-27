<template>
	<view class="">
		<u-popup :show="showpop" mode="bottom" :round="10" @close="close" :closeable="true">
			<view class="h-[70vh] px-[24rpx] bg-page" v-if="Object.keys(showList).length">
				<view class="text-center text-[32rpx] leading-8">{{t('detailedInformation')}}</view>
				<view class="flex mt-[10rpx] menu" v-if="packageList.length > 1">
					<u-tabs :list="packageList" @click="handleClick" :current="current" itemStyle="font-size:28rpx;"
					        lineWidth="55" lineColor="#ff4500"></u-tabs>
				</view>
				<view class="text-[28rpx] mt-[35rpx] ">
					<view class="flex justify-between mb-[20rpx]">
						<text class="mr-[20rpx]">{{showList.company.company_name}}</text>
						<view>
							<text class="mr-[14rpx]">{{showList.express_number}}</text>
							<text @click="copy(showList.express_number)">{{t('copy')}}</text>
						</view>
					</view>
				</view>
				<view class="parcel " style="height: 56vh;">
					<view class="h-[40vh] flex items-center justify-center" v-if="showList.traces.success == false">
						<view class="">
							<text class="iconfont iconzanwuwuliuxinxi text-[180rpx] text-[#bfbfbf]"></text>
							<view class="text-[28rpx] text-[#bfbfbf] leading-8">暂无物流信息～～</view>
						</view>
					</view>
					<scroll-view v-else scroll-y="true" style="height:56vh;padding: 20rpx;box-sizing: border-box;"
					             class="bg-white rounded-md">
						<u-steps current="0" dot direction="column" activeColor="#ff4500">
							<template v-for="(item,index) in showList.traces.list">
								<u-steps-item :title="item.remark" :desc="item.datetime">
								</u-steps-item>
							</template>
						</u-steps>
					</scroll-view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup lang="ts">
    import {ref} from 'vue'
    import {t} from '@/locale'
    import {img, redirect, copy} from '@/utils/common';
    import {getMaterialflowList} from '@/addon/shop/api/order';
    import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';

    let showpop = ref(false)
    const packageList = ref([])
    const showList = ref({})
    const loadList = async (params: any) => {
        let data = await getMaterialflowList(params)
        showList.value = data.data
    }
    const open = (params: any) => {
        loadList(params)
        showpop.value = true
    }
    const close = () => {
        showpop.value = false
    }
    const current = ref(0)
    const handleClick = (item: any) => {
        current.value = item.index
        let params = {
            id: item.id,
            mobile: item.mobile
        }
        loadList(params)
    }
    defineExpose({
        packageList,
        open
    })
</script>

<style lang="scss" scoped>
	.menu :deep(.u-tabs__wrapper__nav__item__text) {
		font-size: 30rpx !important;
		padding-bottom: 10rpx;
	}

	.parcel :deep(.u-text__value) {
		font-size: 24rpx !important;
		line-height: 42rpx !important;
	}

	.parcel :deep(.u-steps-item__wrapper__dot) {
		width: 18px;
		height: 18px;
	}

	.parcel :deep(.u-steps-item__content) {
		margin-left: 20rpx !important;
	}
</style>
