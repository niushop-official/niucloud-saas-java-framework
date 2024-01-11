<template>
    <view class="bg-[#f8f8f8] min-h-screen overflow-hidden">
        <view class="fixed left-0 top-0 right-0 z-10">
            <scroll-view scroll-x="true" class="scroll-Y box-border px-[24rpx] bg-white">
                <view class="flex whitespace-nowrap justify-around">
                    <view :class="['text-[27rpx] leading-[90rpx]', { 'class-select': couponStatus === item.status }]"
                        @click="statusClickFn(item.status)" v-for="(item, index) in statusList">{{ item.name }}</view>
                </view>
            </scroll-view>
        </view>

        <mescroll-body ref="mescrollRef" top="90rpx" @init="mescrollInit" @down="downCallback" @up="getMyCouponListFn">
            <view class="py-[20rpx] px-[24rpx]">
                <template v-for="(item, index) in list">

                    <view v-if="couponStatus != 1"
						class="flex items-center relative w-[100%] rounded-[12rpx] overflow-hidden bg-[#EEEEEE]" :class="{'mt-[20rpx]':index}">
						<view
							class="relative pt-[50rpx] w-[244rpx] h-[222rpx] bg-[#93979D] text-[#fff] text-center box-border px-[40rpx] box-border">
							<view class="w-[100%]">
								<view class="leading-[40rpx] font-500 price-font">
									<text class="text-[28rpx]">￥</text><text class="text-[50rpx]">{{ item.coupon_price
									}}</text>
								</view>
								<view class="mt-[35rpx] text-[23rpx] leading-[32rpx] font-500">
									<text v-if="item.min_condition_money === '0.00'">无门槛</text>
									<text v-else>满{{ item.coupon_min_price }}元可用</text>
								</view>
							</view>
							<image class="w-[10rpx] h-[222rpx] absolute right-[-2rpx] top-0 "
								:src="img('addon/shop/coupon/coupon_border.png')" mode="aspectFill"></image>
						</view>
						<view class="h-[222rpx] flex flex-1 flex-wrap pt-[24rpx] box-border ml-[19rpx] mr-[9rpx]">
							<view class="text-[27rpx] text-[#303133] leading-[38rpx] w-[100%]">
								<view>{{ item.title }}</view>
								<view class="flex mt-[5rpx]">
									<text class="flex items-center bg-[#93979D] text-[#fff] text-[20rpx] h-[32rpx] leading-[32rpx] px-[16rpx] rounded-[16rpx]">{{ item.type_name }}</text>
								</view>
							</view>
							<view
								class="self-end w-[100%] pt-[19rpx] pb-[20rpx] text-[22rpx] leading-[30rpx] text-[#90939A] border-0 border-t-[1px] border-dashed border-[#ccc]">
								<text v-if="item.valid_type == 1">领取之日起{{ item.length || '' }}天内有效</text>
								<text v-else> 有效期至{{ item.valid_end_time ? item.valid_end_time.slice(0, 10) : '' }}</text>
							</view>
						</view>
						<button
							class="!w-[128rpx] !h-[50rpx] text-[23rpx] !border-0 !mr-[34rpx] !bg-[#EEEEEE] leading-[50rpx] text-[#93979D] remove-border"
							 disabled>{{statusList[couponStatus-1].name}}</button>
						<view
							class="absolute top-[50%] left-0 mt-[-20rpx] h-[40rpx] w-[20rpx] rounded-tr-[20rpx] rounded-br-[20rpx] bg-[#fff] ">
						</view>
						<view
							class="absolute top-[50%] right-0 mt-[-20rpx] h-[40rpx] w-[20rpx] rounded-tl-[20rpx] rounded-bl-[20rpx] bg-[#fff] ">
						</view>
					</view>
                    <view v-else
                    class="flex items-center relative w-[100%] rounded-[12rpx] overflow-hidden bg-[#FFF4F4]"
                        :class="{'mt-[20rpx]' : index}">
                        <view
                            class="relative pt-[50rpx] w-[244rpx] h-[222rpx] bg-[#FF4646] text-[#fff] text-center box-border px-[40rpx] box-border">
                            <view class="w-[100%]">
                                <view class="leading-[40rpx] font-500 price-font">
                                    <text class="text-[28rpx]">￥</text><text class="text-[50rpx]">{{ item.coupon_price
                                    }}</text>
                                </view>
                                <view class="mt-[35rpx] text-[23rpx] leading-[32rpx] font-500">
                                    <text v-if="item.min_condition_money === '0.00'">无门槛</text>
                                    <text v-else>满{{ item.coupon_min_price }}元可用</text>
                                </view>
                            </view>
                            <image class="w-[10rpx] h-[222rpx] absolute right-[-2rpx] top-0 "
                                :src="img('addon/shop/coupon/coupon_border_active.png')" mode="aspectFill">
                            </image>
                        </view>
                        <view :class="['h-[222rpx] flex flex-1 flex-wrap pt-[24rpx] box-border ml-[19rpx]',couponStatus===1?'mr-[9rpx]':'mr-[34rpx]']">
                            <view class="text-[27rpx] text-[#303133] leading-[38rpx] w-[100%]">
								<view>{{ item.title }}</view>
								<view class="flex mt-[5rpx]">
									<text class="flex items-center bg-[#FF4646] text-[#fff] text-[20rpx] h-[32rpx] leading-[32rpx] px-[16rpx] rounded-[16rpx]">{{ item.type_name }}</text>
								</view>
							</view>
                            <view
                                class="self-end w-[100%] pt-[19rpx] pb-[20rpx] text-[20rpx] leading-[30rpx] text-[#90939A] border-0 border-t-[1px] border-dashed border-[#ccc]">
                                <text> 有效期至<text class="text-[#FF4543]">{{ item.expire_time ?
                                    item.expire_time.slice(0, 10) : '' }}</text></text>
                            </view>
                        </view>
                        <button v-if="couponStatus === 1"
                            class="!w-[128rpx] !h-[50rpx] text-[23rpx]  !border-0 !mr-[34rpx] !bg-[#FF4646] leading-[50rpx] rounded-full text-white remove-border"  @click="toLink(item.id)">去使用</button>
                        <view
                            class="absolute top-[50%] left-0 mt-[-20rpx] h-[40rpx] w-[20rpx] rounded-tr-[20rpx] rounded-br-[20rpx] bg-[#fff] ">
                        </view>
                        <view
                            class="absolute top-[50%] right-0 mt-[-20rpx] h-[40rpx] w-[20rpx] rounded-tl-[20rpx] rounded-bl-[20rpx] bg-[#fff] ">
                        </view>
                    </view>
                </template>
            </view>
            <mescroll-empty :option="{ 'icon': img('static/resource/images/empty.png') }"
                v-if="!list.length && loading"></mescroll-empty>
        </mescroll-body>
        <pay ref="payRef"></pay>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { img, redirect } from '@/utils/common'
import { getMyCouponList } from '@/addon/shop/api/coupon'
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue'
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue'
import useMescroll from '@/components/mescroll/hooks/useMescroll.js'
import { onPageScroll, onReachBottom } from '@dcloudio/uni-app'
import { t } from '@/locale'

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom)
const list = ref<Array<Object>>([]);
const loading = ref<boolean>(false);
const statusList = <Array<Object>>([
    { status: 1, name: '待使用' },
    { status: 2, name: '已使用' },
    { status: 3, name: '已过期' },
]);
const couponStatus = ref(1);

const statusClickFn = (status: any) => {
    couponStatus.value = status;
    list.value = []; //如果是第一页需手动制空列表
    getMescroll().resetUpScroll();
};

const getMyCouponListFn = (mescroll) => {
    loading.value = false;
    let data: object = {
        page: mescroll.num,
        limit: mescroll.size,
        status: couponStatus.value
    };

    getMyCouponList(data).then((res) => {
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
const toLink = (id:any) => {
	redirect({ url: '/addon/shop/pages/goods/list',param:{coupon_id:id} })
}
</script>

<style lang="scss" scoped>
@import '@/addon/shop/styles/common.scss';

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

</style>
