<template>
    <view
    v-if="Object.keys(detail).length&&!loading"
    class="min-h-screen bg-style relative"
        :style="{ 'background': 'url(' + img('addon/shop/coupon/coupon_header.png') + ') no-repeat' }">
        <view class="absolute top-[34%] w-[100%]">
            <view class="w-[348rpx] h-[348rpx] code box-border p-[14rpx] bg-[#fff] mx-[auto]">
                <image class="w-[318rpx] h-[318rpx]" :src="codeUrl" mode="aspectFill"></image>
            </view>
            <view class="mt-[40rpx] flex justify-center items-center">
                <text class="text-[28rpx] text-[#303133]">{{ detail.title || '优惠券' }}</text>
                <view
                v-if="detail.limit_count"
                    class="text-[20rpx] leading-[34rpx] border-[1rpx] border-solid border-color text-color px-[17rpx] box-border min-w-[94rpx] h-[34rpx] ml-[24rpx] rounded-[17rpx] flex items-center">
                    限领{{ detail.limit_count }}张
                </view>
            </view>
            <view class="text-[52rpx] text-color mt-[32rpx] text-center price-font">￥{{ detail.coupon_price || 0.00 }}</view>
            <view class="text-[28rpx] text-[#909399] mt-[14rpx] text-center">
                <text v-if="detail.min_condition_money === '0.00'">无门槛</text>
                <text v-else>满{{ detail.coupon_min_price }}元可用</text>
            </view>
            <view class="text-[28rpx] text-[#909399] mt-[14rpx] text-center">
                <text v-if="detail.valid_type == 1">领取之日起<text class="text-[#FF4543]">{{
                    detail.length}}</text>天内有效</text>
                <text v-else> 有效期至<text class="text-[#FF4543]">{{ detail.valid_end_time ?
                    detail.valid_end_time.slice(0, 10) : '' }}</text></text>
            </view>
            <view class="flex justify-center items-center mt-[80rpx]">
                <button v-if="detail.btnType === 'collected'"
                    class="!w-[500rpx] !h-[80rpx] !leading-[80rpx] !text-[28rpx] !m-0 !border-0 !mr-[34rpx] !bg-[#93979D] !border-[#93979D]"
                    type="primary" shape="circle" disabled>该优惠券领取已达到上限</button>
                <button v-if="detail.btnType === 'collecting'"
                    class="!w-[500rpx] !h-[80rpx] !leading-[80rpx] !text-[28rpx] !m-0 !border-0 !mr-[34rpx] !bg-[#FF4646]" type="primary"
                    shape="circle" @click="collecting(detail.id)">领取</button>
                <button v-if="detail.btnType === 'using'"
                    class="!w-[500rpx] !h-[80rpx] !leading-[80rpx] !text-[28rpx] !m-0 !border-0 !mr-[34rpx] !bg-[#FF4646]" type="primary"
                    shape="circle" @click="toLink(detail.id)">去使用</button>
            </view>
            <view class="text-[28rpx] text-[#909399] mt-[24rpx] text-center">
                注:扫描二维码或点击右上角进行分享
            </view>
        </view>

    </view>
    <u-loading-page bg-color="rgb(248,248,248)" :loading="loading" fontSize="16" color="#333"></u-loading-page>
</template>
<script lang="ts" setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { img, redirect } from '@/utils/common'
import QRCode from "qrcode";
import { getShopCouponInfo, getCoupon } from '@/addon/shop/api/coupon'
import useMemberStore from '@/stores/member'
import { useLogin } from '@/hooks/useLogin'

const loading = ref(false)
const codeUrl = ref('')
const detail = ref({})
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)
onLoad((option) => {
    if(!option.coupon_id){
        uni.showToast({ title: '优惠券不存在', icon: 'none' })
        setTimeout(() => {
				redirect({ url: '/addon/shop/pages/index', mode: 'reLaunch' })
			}, 600)
        return false
    }else{
        getShopCouponInfoFn(option.coupon_id)
    }

})
QRCode.toDataURL(window.location.href, { errorCorrectionLevel: 'L', margin: 0, width: 100 }).then(url => {
    codeUrl.value = url
})
const getShopCouponInfoFn = (id: number) => {
    loading.value = true
    getShopCouponInfo(id).then((res:any) => {
        detail.value = res.data
        if (detail.value.sum_count != -1 && detail.value.receive_count === detail.value.sum_count) {
            detail.value.btnType = 'collected'//已领完
        }
        if (!userInfo.value) {
            detail.value.btnType = 'collecting'//领用
        } else {
            if (detail.value.is_receive && detail.value.limit_count === detail.value.member_receive_count) {
                detail.value.btnType = 'using'//去使用
            } else {
                detail.value.btnType = 'collecting'//领用
            }
        }
        loading.value = false

    }).catch(() => {
        loading.value = false
        detail.value = {}
        setTimeout(() => {
				redirect({ url: '/addon/shop/pages/index', mode: 'reLaunch' })
		}, 600)
    })
}
const collecting = (coupon_id: any) => {
    if (!userInfo.value) {
        useLogin().setLoginBack({ url: '/addon/shop/pages/coupon/list' })
        return false
    }
    getCoupon({ coupon_id, number: 1 }).then(res => {
        detail.value.btnType = 'using'
    })
}
const toLink = (id: any) => {
    redirect({ url: '/addon/shop/pages/goods/list', param: { coupon_id: id } })
}
</script>
<style lang="scss" scoped>
@import '@/addon/shop/styles/common.scss';

.border-color {
    border-color: var(--primary-color) !important;
}

.text-color {
    color: var(--primary-color) !important;
}

.bg-color {
    background-color: var(--primary-color) !important;
}

.bg-style {
    background-size: 100% auto !important;
}

.code {
    box-shadow: 0 0 20px -1px;
}
</style>
